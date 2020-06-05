package cn.ibdsr.web.modular.shop.order.service.impl;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessTip;
import cn.ibdsr.core.support.HttpKit;
import cn.ibdsr.core.util.DateUtil;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.constant.state.OrderCashTransSrc;
import cn.ibdsr.web.common.constant.state.OrderCashType;
import cn.ibdsr.web.common.constant.state.PaymentStatus;
import cn.ibdsr.web.common.constant.state.PlatformBalanceFlowTransSrc;
import cn.ibdsr.web.common.constant.state.ShopOrderStatus;
import cn.ibdsr.web.common.constant.state.ShopType;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.PaymentMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderGoodsMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderMapper;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.model.ShopOrder;
import cn.ibdsr.web.common.persistence.model.ShopOrderGoods;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.core.util.CommonUtils;
import cn.ibdsr.web.core.util.DateTimeUtils;
import cn.ibdsr.web.core.util.VerifyUtils;
import cn.ibdsr.web.modular.shop.cash.service.ICashChangeService;
import cn.ibdsr.web.modular.shop.order.payment.PayType;
import cn.ibdsr.web.modular.shop.order.payment.PayUtils;
import cn.ibdsr.web.modular.shop.order.payment.Payment;
import cn.ibdsr.web.modular.shop.order.payment.WXPayConfigImpl;
import cn.ibdsr.web.modular.shop.order.payment.WXPayUtils;
import cn.ibdsr.web.modular.shop.order.service.ShopPayService;
import cn.ibdsr.web.modular.shop.record.service.RecordService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.wxpay.sdk.WXPay;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信端：订单接口Service
 *
 * @author xjc
 * @Date 2019-01-14 16:51:46
 */
@Service
public class ShopPayServiceImpl implements ShopPayService {

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private ICashChangeService cashChangeService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private ShopOrderGoodsMapper shopOrderGoodsMapper;

    /**
     * 根据订单ID和状态查询订单
     *
     * @param shopOrderId 订单ID
     * @param status      订单状态
     * @return ShopOrder
     */
    private ShopOrder selectOrderByIdAndStatus(Long shopOrderId, Integer status) {
        if (shopOrderId == null) {
            throw new BussinessException(BizExceptionEnum.ORDER_ID_IS_NULL);
        }
        ShopOrder param = new ShopOrder();
        param.setId(shopOrderId);
        param.setStatus(status);
        param.setUserId(ConstantFactory.me().getUserId());
        param.setIsDeleted(IsDeleted.NORMAL.getCode());
        return shopOrderMapper.selectOne(param);
    }

    /**
     * 获取支付通道之前，对订单进行校验
     */
    private void verifyOrder(List<Long> shopOrderIdList) {
        if (shopOrderIdList.size() == 0) {
            throw new BussinessException(BizExceptionEnum.ORDER_ID_IS_NULL);
        }
        //校验订单是否存在以及订单是否超时
        for (Long shopOrderId : shopOrderIdList) {
            ShopOrder shopOrder = selectOrderByIdAndStatus(shopOrderId, ShopOrderStatus.WAIT_PAY.getCode());
            if (shopOrder == null) {
                throw new BussinessException(BizExceptionEnum.ORDER_JSON_IS_NULL);
            }
            //校验订单是否超时
            LocalDateTime createdTime = DateTimeUtils.toLocalDateTime(shopOrder.getCreatedTime());
            if (createdTime.plusSeconds(Const.SHOP_ORDER_OUT_TIME).isBefore(LocalDateTime.now())) {
                throw new BussinessException(BizExceptionEnum.ORDER_OUT_TIME);
            }
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
        payment.setNotifyUrl(Const.SHOP_WX_PAY_NOTIFY);
        User user = userMapper.selectById(ConstantFactory.me().getUserId());
        if (user == null) {
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER);
        }
        payment.setOpenid(user.getWxOpenId());
    }

    /**
     * 单订单支付
     *
     * @param shopOrderId 订单主键ID
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip pay(Long shopOrderId, int payType, String wechatPayType) throws Exception {
        if (!VerifyUtils.verifyPayType(payType)) {
            throw new BussinessException(BizExceptionEnum.PAY_TYPE_IS_NOT_EXIT);
        }
        if (!VerifyUtils.verifyWechatPayType(wechatPayType)) {
            throw new BussinessException(BizExceptionEnum.WECHAT_PAY_TYPE_IS_NOT_EXIT);
        }
        if (shopOrderId == null) {
            throw new BussinessException(BizExceptionEnum.ORDER_ID_IS_NULL);
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(shopOrderId);
        if (shopOrder == null) {
            throw new BussinessException(BizExceptionEnum.ORDER_JSON_IS_NULL);
        }
        if (shopOrder.getStatus() != ShopOrderStatus.WAIT_PAY.getCode()) {
            throw new BussinessException(BizExceptionEnum.ORDER_PAY_ERROR);
        }
        List<Long> shopOrderIdList = new ArrayList<>();
        shopOrderIdList.add(shopOrderId);
        JSONObject result = getChannel(shopOrderIdList, payType, wechatPayType);
        return new SuccessDataTip(result);
    }

    /**
     * 根据支付方式，返回支付需要的信息
     */
    @Override
    @Transactional
    public JSONObject getChannel(List<Long> shopOrderIdList, Integer payTypeId, String wechatPayType) throws Exception {
        if (!VerifyUtils.verifyWechatPayType(wechatPayType)) {
            throw new BussinessException(BizExceptionEnum.WECHAT_PAY_TYPE_IS_NOT_EXIT);
        }
        //将订单ID进行排序
        Collections.sort(shopOrderIdList);
        String orderIds = StringUtils.join(shopOrderIdList, ",");
        verifyOrder(shopOrderIdList);
        Wrapper<ShopOrder> shopOrderWrapper = new EntityWrapper<>();
        shopOrderWrapper.eq("status", ShopOrderStatus.WAIT_PAY.getCode()).eq("is_deleted", IsDeleted.NORMAL.getCode()).in("id", shopOrderIdList);
        List<ShopOrder> shopOrderList = shopOrderMapper.selectList(shopOrderWrapper);
        PayType payType = PayType.valueOf(payTypeId);
        //计算支付金额
        BigDecimal amount = new BigDecimal(0);
        for (ShopOrder shopOrder : shopOrderList) {
            if (!VerifyUtils.verifyUser(shopOrder.getUserId())) {
                throw new BussinessException(BizExceptionEnum.DATA_ERROR);
            }
            amount = amount.add(shopOrder.getOrderPrice());
        }
        cn.ibdsr.web.common.persistence.model.Payment param = new cn.ibdsr.web.common.persistence.model.Payment();
        param.setOrderIds(orderIds);
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
        paymentSetInfo(payment, payTypeId, amount, actualOutTradeNo);
        Object result = payType.getChannel(payment);
        JSONObject resultJson = JSONObject.parseObject(JSONObject.toJSONString(result));
        resultJson.put("actualOutTradeNo", actualOutTradeNo);
        /**插入订单支付信息*/
        if (payment1 == null) {//支付数据不存在，生成outTradeNo
            cn.ibdsr.web.common.persistence.model.Payment payment2 = new cn.ibdsr.web.common.persistence.model.Payment();
            payment2.setType(Const.ORDER_NO_BUSINESS_TYPE_1);
            payment2.setOrderIds(orderIds);
            payment2.setMerchantId(ConstantFactory.me().getWXpayProperties().getMchid());
            payment2.setOutTradeNo(outTradeNo);
            payment2.setActualOutTradeNo(actualOutTradeNo);
            payment2.setPayAmount(amount);
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
     * 支付成功后，前端调用此接口，向微信查询支付结果，然后处理订单
     *
     * @param actualOutTradeNo 商户订单号
     * @return SuccessDataTip
     */
    @Override
    @Transactional
    public SuccessTip orderquery(String actualOutTradeNo) throws Exception {
        cn.ibdsr.web.common.persistence.model.Payment param = new cn.ibdsr.web.common.persistence.model.Payment();
        param.setActualOutTradeNo(actualOutTradeNo);
        cn.ibdsr.web.common.persistence.model.Payment payment = paymentMapper.selectOne(param);
        Wrapper<ShopOrder> shopOrderWrapper = new EntityWrapper<>();
        shopOrderWrapper.in("id", payment.getOrderIds());
        List<ShopOrder> shopOrderList = shopOrderMapper.selectList(shopOrderWrapper);
        for (ShopOrder shopOrder : shopOrderList) {
            if (!VerifyUtils.verifyUser(shopOrder.getUserId())) {
                throw new BussinessException(BizExceptionEnum.DATA_ERROR);
            }
        }
        Map<String, String> notifyMap = (new WXPayUtils()).orderquery(payment.getMerchantId(), payment.getActualOutTradeNo());
        verifyPayment(payment, notifyMap);
        return new SuccessTip();
    }

    /**
     * 支付完，支付宝异步通知,修改订单状态，以及其他信息
     */
    @Override
    @Transactional
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

    private synchronized void verifyPayment(cn.ibdsr.web.common.persistence.model.Payment payment, Map<String, String> notifyMap) {
        BigDecimal total_fee = new BigDecimal(notifyMap.get("total_fee"));
        if (payment.getPayAmount().compareTo(total_fee) != 0) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        //判断订单是否已处理，如果已处理，则忽略，如果没处理，则处理订单
        if (PaymentStatus.SUCCESS.getCode() != payment.getStatus()) {
            updateOrder(payment, notifyMap);
        }
    }

    private void updateOrder(cn.ibdsr.web.common.persistence.model.Payment payment, Map<String, String> notifyMap) {
        Date now = new Date();
        String[] orderIds = StringUtils.split(payment.getOrderIds(), ",");
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
        for (String orderId : orderIds) {
            ShopOrder shopOrder = new ShopOrder();
            shopOrder.setId(Long.valueOf(orderId));
            shopOrder.setStatus(ShopOrderStatus.WAIT_DELIVERY.getCode());
            shopOrder.setPaymentTime(now);
            shopOrder.setModifiedUser(ConstantFactory.me().getUserId());
            shopOrder.setModifiedTime(now);
            shopOrder.updateById();

            //增加资金变动记录，用户已付款：待出账金额+（shop_order_cash_flow）；平台余额+（platform_balance_flow）
            ShopOrder shopOrder1 = shopOrderMapper.selectById(orderId);
            cashChangeService.creditOrderCash(shopOrder1.getShopId(), orderId, shopOrder1.getOrderPrice(), OrderCashType.OUTACC_AMOUNT.getCode(), OrderCashTransSrc.PAYMENT.getCode(), null, ShopType.STORE.getCode());
            cashChangeService.creditPlatformBalance(shopOrder1.getOrderPrice(), PlatformBalanceFlowTransSrc.ORDER_INCOME.getCode(), orderId, null);

            try {
                //增加商品的付款数
                List<ShopOrderGoods> shopOrderGoodsList = shopOrderGoodsMapper.selectList(new EntityWrapper<ShopOrderGoods>().eq("order_id", orderId));
                for (ShopOrderGoods shopOrderGoods : shopOrderGoodsList) {
                    recordService.payGoods(shopOrderGoods.getGoodsId(), shopOrderGoods.getNums());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
