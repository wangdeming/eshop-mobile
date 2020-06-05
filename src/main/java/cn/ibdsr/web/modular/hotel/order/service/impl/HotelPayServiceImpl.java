package cn.ibdsr.web.modular.hotel.order.service.impl;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessTip;
import cn.ibdsr.core.support.HttpKit;
import cn.ibdsr.core.util.DateUtil;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.HotelOrderStatus;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.constant.state.OrderCashTransSrc;
import cn.ibdsr.web.common.constant.state.OrderCashType;
import cn.ibdsr.web.common.constant.state.PaymentReturnCode;
import cn.ibdsr.web.common.constant.state.PaymentStatus;
import cn.ibdsr.web.common.constant.state.PlatformBalanceFlowTransSrc;
import cn.ibdsr.web.common.constant.state.ShopOrderStatus;
import cn.ibdsr.web.common.constant.state.ShopType;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.HotelOrderMapper;
import cn.ibdsr.web.common.persistence.dao.PaymentMapper;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.model.HotelOrder;
import cn.ibdsr.web.common.persistence.model.HotelOrderRefund;
import cn.ibdsr.web.common.persistence.model.ShopOrder;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.core.mq.rabbitmq.MessageSenderTask;
import cn.ibdsr.web.core.util.CommonUtils;
import cn.ibdsr.web.core.util.DateTimeUtils;
import cn.ibdsr.web.core.util.OrderUtils;
import cn.ibdsr.web.core.util.VerifyUtils;
import cn.ibdsr.web.modular.hotel.order.service.HotelPayService;
import cn.ibdsr.web.modular.shop.cash.service.ICashChangeService;
import cn.ibdsr.web.modular.shop.order.payment.PayType;
import cn.ibdsr.web.modular.shop.order.payment.PayUtils;
import cn.ibdsr.web.modular.shop.order.payment.Payment;
import cn.ibdsr.web.modular.shop.order.payment.PaymentResult;
import cn.ibdsr.web.modular.shop.order.payment.WXPayConfigImpl;
import cn.ibdsr.web.modular.shop.order.payment.WXPayUtils;
import cn.ibdsr.web.modular.shop.record.service.RecordService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.wxpay.sdk.WXPay;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/15 14:15
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/15 xujincai init
 */
@Service
public class HotelPayServiceImpl implements HotelPayService {

    @Autowired
    private HotelOrderMapper hotelOrderMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageSenderTask messageSenderTask;

    @Autowired
    private ICashChangeService cashChangeService;

    @Autowired
    private RecordService recordService;

    private void verifyPay(Integer payType, String wechatPayType) {
        if (!VerifyUtils.verifyPayType(payType)) {
            throw new BussinessException(BizExceptionEnum.PAY_TYPE_IS_NOT_EXIT);
        }
        if (!VerifyUtils.verifyWechatPayType(wechatPayType)) {
            throw new BussinessException(BizExceptionEnum.WECHAT_PAY_TYPE_IS_NOT_EXIT);
        }
    }

    /**
     * 获取支付通道之前，对订单进行校验
     */
    private void verifyOrder(HotelOrder hotelOrder) {
        //校验订单是否存在以及订单是否超时
        if (hotelOrder == null || hotelOrder.getIsDeleted() == IsDeleted.DELETED.getCode()) {
            throw new BussinessException(BizExceptionEnum.ORDER_JSON_IS_NULL);
        }
        //校验订单是否超时
        LocalDateTime createdTime = DateTimeUtils.toLocalDateTime(hotelOrder.getCreatedTime());
        if (createdTime.plusSeconds(Const.HOTEL_ORDER_OUT_TIME).isBefore(LocalDateTime.now())) {
            throw new BussinessException(BizExceptionEnum.ORDER_OUT_TIME);
        }
    }

    /**
     * 填充支付信息
     */
    private void paymentSetInfo(Payment payment, Integer payTypeId, BigDecimal amount, String outTradeCode) {
        payment.setAmount(amount);
        payment.setOutTradeCode(outTradeCode);
        payment.setSubject(Const.APP_NAME);
        payment.setIp(HttpKit.getRequest().getRemoteAddr());
        payment.setNotifyUrl(Const.HOTEL_WX_PAY_NOTIFY);
        User user = userMapper.selectById(ConstantFactory.me().getUserId());
        if (user == null) {
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER);
        }
        payment.setOpenid(user.getWxOpenId());
    }

    private JSONObject unifiedorder(String hotelOrderId, Integer payTypeId, String wechatPayType) throws Exception {
        HotelOrder hotelOrder = hotelOrderMapper.selectById(hotelOrderId);
        verifyOrder(hotelOrder);
        PayType payType = PayType.valueOf(payTypeId);
        cn.ibdsr.web.common.persistence.model.Payment param = new cn.ibdsr.web.common.persistence.model.Payment();
        param.setOrderIds(hotelOrderId);
        cn.ibdsr.web.common.persistence.model.Payment payment1 = paymentMapper.selectOne(param);
        //确认支付数据是否存在并已支付
        if (payment1 != null && payment1.getStatus() == PaymentStatus.SUCCESS.getCode()) {
            throw new BussinessException(BizExceptionEnum.ORDER_PAY_PAID);
        }
        //生成商户订单号，不是订单编号
        String outTradeNo;
        if (payment1 == null) {//支付数据不存在，生成outTradeNo
            outTradeNo = CommonUtils.getOrderCode();
        } else {//支付数据存在，使用原数据
            outTradeNo = payment1.getOutTradeNo();
        }
        //微信支付的三种支付方式JSAPI、NATIVE、APP不能使用同一个商户订单号，如果使用同一个会导致使用其中一个支付方式向微信下单未支付后，再使用另外一种方式不能进行微信支付
        String actualOutTradeNo = wechatPayType + outTradeNo;
        //根据支付方式，调用相应的支付通道
        Payment payment = new Payment();
        //填充支付信息
        paymentSetInfo(payment, payTypeId, hotelOrder.getTotalAmount(), actualOutTradeNo);
        Object result = payType.getChannel(payment);
        JSONObject resultJson = JSONObject.parseObject(JSONObject.toJSONString(result));
        resultJson.put("actualOutTradeNo", actualOutTradeNo);
        //插入订单支付信息
        if (payment1 == null) {//支付数据不存在，生成outTradeNo
            cn.ibdsr.web.common.persistence.model.Payment payment2 = new cn.ibdsr.web.common.persistence.model.Payment();
            payment2.setType(Const.ORDER_NO_BUSINESS_TYPE_1);
            payment2.setOrderIds(hotelOrderId);
            payment2.setMerchantId(ConstantFactory.me().getWXpayProperties().getMchid());
            payment2.setOutTradeNo(outTradeNo);
            payment2.setActualOutTradeNo(actualOutTradeNo);
            payment2.setPayAmount(hotelOrder.getTotalAmount());
            payment2.setPayWay(PayType.WEIXIN.getCode());
            payment2.setCreatedUser(ConstantFactory.me().getUserId());
            payment2.setCreatedTime(new Date());
            payment2.insert();
        } else {//支付数据存在，使用原数据
            cn.ibdsr.web.common.persistence.model.Payment payment2 = new cn.ibdsr.web.common.persistence.model.Payment();
            payment2.setId(payment1.getId());
            payment2.setMerchantId(ConstantFactory.me().getWXpayProperties().getMchid());
            payment2.setActualOutTradeNo(actualOutTradeNo);
            payment2.setModifiedUser(ConstantFactory.me().getUserId());
            payment2.setModifiedTime(new Date());
            payment2.updateById();
        }
        return resultJson;
    }

    /**
     * 订单发起支付
     *
     * @param hotelOrderId  订单ID
     * @param payType       支付方式，微信支付方式值为1，支付宝支付方式值为2
     * @param wechatPayType 支付渠道（此渠道不同于订单编号中的下单渠道），微信公众号支付值为JSAPI，微信APP支付值为APP，微信扫码支付为NATIVE，支付宝支付值为ALIPAY
     * @return SuccessDataTip
     * @throws Exception Exception
     */
    @Override
    public SuccessDataTip pay(String hotelOrderId, Integer payType, String wechatPayType) throws Exception {
        verifyPay(payType, wechatPayType);
        if (hotelOrderId == null) {
            throw new BussinessException(BizExceptionEnum.ORDER_ID_IS_NULL);
        }
        HotelOrder hotelOrder = hotelOrderMapper.selectById(hotelOrderId);
        if (hotelOrder == null) {
            throw new BussinessException(BizExceptionEnum.ORDER_JSON_IS_NULL);
        }
        if (hotelOrder.getStatus() != HotelOrderStatus.WAIT_PAY.getCode()) {
            throw new BussinessException(BizExceptionEnum.ORDER_PAY_ERROR);
        }
        JSONObject result = unifiedorder(hotelOrder.getId(), payType, wechatPayType);
        return new SuccessDataTip(result);
    }

    private void updateOrder(cn.ibdsr.web.common.persistence.model.Payment payment, Map<String, String> notifyMap) {
        Date now = new Date();
        cn.ibdsr.web.common.persistence.model.Payment payment1 = new cn.ibdsr.web.common.persistence.model.Payment();
        payment1.setId(payment.getId());
        payment1.setStatus(PaymentStatus.SUCCESS.getCode());
        payment1.setTransactionId(notifyMap.get("transaction_id"));
        payment1.setTotalFee(new BigDecimal(notifyMap.get("total_fee")));
        payment1.setOriginalString(JSONObject.toJSONString(notifyMap));
        payment1.setModifiedUser(ConstantFactory.me().getUserId());
        payment1.setModifiedTime(now);
        payment1.setCompleteTime(DateUtil.parse(notifyMap.get("time_end"), "yyyyMMddHHmmss"));
        payment1.updateById();

        HotelOrder hotelOrder = new HotelOrder();
        hotelOrder.setId(payment.getOrderIds());
        hotelOrder.setStatus(ShopOrderStatus.WAIT_DELIVERY.getCode());
        hotelOrder.setPayDatetime(now);
        hotelOrder.setModifiedUser(ConstantFactory.me().getUserId());
        hotelOrder.setModifiedTime(now);
        hotelOrder.updateById();

        //订单支付成功后超过30分钟未确认，系统自动确认订单
        messageSenderTask.sendMsgOfConfirmHotelOrder(payment.getOrderIds());

        //增加资金变动记录，用户已付款：待出账金额+（shop_order_cash_flow）；平台余额+（platform_balance_flow）
        HotelOrder hotelOrder1 = hotelOrder.selectById(payment.getOrderIds());
        cashChangeService.creditOrderCash(hotelOrder1.getShopId(), hotelOrder1.getId(), hotelOrder1.getTotalAmount(), OrderCashType.OUTACC_AMOUNT.getCode(), OrderCashTransSrc.PAYMENT.getCode(), null, ShopType.HOTEL.getCode());
        cashChangeService.creditPlatformBalance(hotelOrder1.getTotalAmount(), PlatformBalanceFlowTransSrc.ORDER_INCOME.getCode(), hotelOrder1.getId(), null);

        try {
            //增加房间的付款数
            recordService.payRoom(hotelOrder1.getRoomId(), hotelOrder1.getRoomNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void verifyPayment(cn.ibdsr.web.common.persistence.model.Payment payment, Map<String, String> notifyMap) {
        if (!StringUtils.equals(notifyMap.get("return_code"), Const.SUCCESS) || !StringUtils.equals(notifyMap.get("result_code"), Const.SUCCESS)) {
            throw new BussinessException(BizExceptionEnum.ORDER_PAY_FAIL);
        }
        BigDecimal total_fee = new BigDecimal(notifyMap.get("total_fee"));
        if (payment.getPayAmount().compareTo(total_fee) != 0) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        //判断订单是否已处理，如果已处理，则忽略，如果没处理，则处理订单
        if (PaymentStatus.SUCCESS.getCode() != payment.getStatus()) {
            updateOrder(payment, notifyMap);
        }
    }

    /**
     * 支付成功后，前端调用此接口，向微信查询支付结果，然后处理订单
     *
     * @param actualOutTradeNo 商户订单号
     * @return SuccessTip
     * @throws Exception Exception
     */
    @Override
    @Transactional
    public SuccessTip orderquery(String actualOutTradeNo) throws Exception {
        cn.ibdsr.web.common.persistence.model.Payment param = new cn.ibdsr.web.common.persistence.model.Payment();
        param.setActualOutTradeNo(actualOutTradeNo);
        cn.ibdsr.web.common.persistence.model.Payment payment = paymentMapper.selectOne(param);
        Wrapper<ShopOrder> shopOrderWrapper = new EntityWrapper<>();
        shopOrderWrapper.in("id", payment.getOrderIds());
        HotelOrder hotelOrder = hotelOrderMapper.selectById(payment.getOrderIds());
        if (!VerifyUtils.verifyUser(hotelOrder.getUserId())) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        Map<String, String> notifyMap = (new WXPayUtils()).orderquery(payment.getMerchantId(), payment.getActualOutTradeNo());
        verifyPayment(payment, notifyMap);
        return new SuccessTip();
    }

    @Override
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取微信回调返回的请求参数，并进行处理
        Map<String, String> notifyMap = PayUtils.getWXRequestParam(request);
        WXPayConfigImpl config = WXPayConfigImpl.getInstance();
        WXPay wxpay = new WXPay(config);
        if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {
            String actualOutTradeNo = notifyMap.get("out_trade_no");
            cn.ibdsr.web.common.persistence.model.Payment param = new cn.ibdsr.web.common.persistence.model.Payment();
            param.setActualOutTradeNo(actualOutTradeNo);
            cn.ibdsr.web.common.persistence.model.Payment payment = paymentMapper.selectOne(param);
            try {
                verifyPayment(payment, notifyMap);
                response.getWriter().write("SUCCESS");
                response.getWriter().close();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.getWriter().write("FAIL");
        response.getWriter().close();
    }

    @Override
    public void refund(String hotelOrderId) throws Exception {
        HotelOrder hotelOrder = hotelOrderMapper.selectById(hotelOrderId);
        if (hotelOrder == null) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        cn.ibdsr.web.common.persistence.model.Payment param = new cn.ibdsr.web.common.persistence.model.Payment();
        param.setOrderIds(hotelOrderId);
        cn.ibdsr.web.common.persistence.model.Payment pay = paymentMapper.selectOne(param);
        if (pay == null || pay.getStatus() != PaymentStatus.SUCCESS.getCode()) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        Long userId = ConstantFactory.me().getUserId();
        Date now = new Date();
        HotelOrderRefund hotelOrderRefund = new HotelOrderRefund();
        hotelOrderRefund.setShopId(hotelOrder.getShopId());
        hotelOrderRefund.setOrderId(hotelOrder.getId());
        hotelOrderRefund.setRoomId(hotelOrder.getRoomId());
        hotelOrderRefund.setRefundOrderNo(OrderUtils.getOrderNoByUUId(hotelOrder.getId()));
        hotelOrderRefund.setRefundAmount(pay.getPayAmount());
        hotelOrderRefund.setCreatedUser(userId);
        hotelOrderRefund.setCreatedTime(now);

        //申请退款
        Payment payment = new Payment();
        payment.setOutTradeCode(pay.getActualOutTradeNo());
        payment.setAmount(hotelOrderRefund.getRefundAmount());
        payment.setTotalAmount(hotelOrderRefund.getRefundAmount());
        payment.setOutRefundNo(hotelOrderRefund.getRefundOrderNo());
        PaymentResult result = (new WXPayUtils()).refund(payment);
        if (!StringUtils.equals(result.getCode(), PaymentReturnCode.SUCCESS.getCode())) {
            throw new BussinessException(BizExceptionEnum.REFUND_FAIL);
        }

        hotelOrderRefund.setOriginalRefundId(result.getOutBizNo());
        hotelOrderRefund.setOriginalString(result.getOriginalString());
        hotelOrderRefund.setExpectedTime(new Date());
        hotelOrderRefund.insert();

        //增加资金变动记录，用户售后退款：待到账金额-（shop_order_cash_flow）；平台余额-（platform_balance_flow）
        cashChangeService.debitOrderCash(hotelOrder.getShopId(), hotelOrder.getId(), hotelOrder.getTotalAmount(), OrderCashType.INCOME_AMOUNT.getCode(), OrderCashTransSrc.REFUND.getCode(), null, ShopType.HOTEL.getCode());
        cashChangeService.debitPlatformBalance(hotelOrder.getTotalAmount(), PlatformBalanceFlowTransSrc.GOODS_REFUND.getCode(), hotelOrder.getId(), null);
    }

}
