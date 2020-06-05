package cn.ibdsr.web.modular.shop.order.service.impl;

import cn.ibdsr.core.check.StaticCheck;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.*;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.*;
import cn.ibdsr.web.common.persistence.model.ShopOrder;
import cn.ibdsr.web.common.persistence.model.ShopOrderGoods;
import cn.ibdsr.web.common.persistence.model.ShopOrderRefund;
import cn.ibdsr.web.common.persistence.model.ShopOrderRefundLogistics;
import cn.ibdsr.web.core.mq.rabbitmq.MessageSenderTask;
import cn.ibdsr.web.core.util.AmountFormatUtil;
import cn.ibdsr.web.core.util.DateUtils;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.core.util.OrderUtils;
import cn.ibdsr.web.modular.shop.order.service.IOrderRefundService;
import cn.ibdsr.web.modular.shop.order.transfer.refund.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Description 订单商品退款Service
 * @Version V1.0
 * @CreateDate 2019-04-02 10:54:32
 * <p>
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-02 10:54:32    XuZhipeng               类说明
 */
@Service
public class OrderRefundServiceImpl implements IOrderRefundService {

    @Autowired
    private ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Autowired
    private ShopOrderRefundMapper shopOrderRefundMapper;

    @Autowired
    private ShopOrderRefundLogisticsMapper shopOrderRefundLogisticsMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private MessageSenderTask messageSenderTask;

    /**
     * 校验是否已存在售后服务，并返回订单商品信息
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    @Override
    public OrderGoodsDTO checkServiced(Long orderGoodsId) {
        // 校验订单商品ID并返回订单商品信息
        ShopOrderGoods orderGoods = checkOrderGoodsId(orderGoodsId);

        // 判断订单是否已支付
        ShopOrder order = shopOrderMapper.selectById(orderGoods.getOrderId());
        if (ShopOrderStatus.WAIT_PAY.getCode() == order.getStatus()
                || ShopOrderStatus.CANCEL.getCode() == order.getStatus()
                || ShopOrderStatus.CLOSED.getCode() == order.getStatus()) {
            throw new BussinessException(BizExceptionEnum.ORDER_IS_NOT_PAIED);
        }

        // 售后中和售后完成的订单商品无法再次进行退款申请
        if (ServiceStatus.REFUNDING.getCode() == orderGoods.getServiceStatus()
                || ServiceStatus.REFUNDED.getCode() == orderGoods.getServiceStatus()) {
            throw new BussinessException(BizExceptionEnum.ORDER_GOODS_CAN_NOT_REFUND);
        }

        // 查询售后信息，申请次数不得超过5次
        List<ShopOrderRefund> refundList = shopOrderRefundMapper.selectList(
                new EntityWrapper<ShopOrderRefund>()
                        .eq("order_goods_id", orderGoodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode()));
        if (null != refundList && Const.REFUND_MAX_COUNT_LIMIT <= refundList.size()) {
            throw new BussinessException(BizExceptionEnum.REFUND_COUNT_MORE_THAN_LIMIT);
        }

        // 售后信息存在待审核、审核通过、退款成功状态，无法再次申请退款
        for (ShopOrderRefund refund : refundList) {
            if (RefundStatus.WAIT_REVIEW.getCode() == refund.getStatus()
                    || RefundStatus.PASS.getCode() == refund.getStatus()
                    || RefundStatus.SUCCESS.getCode() == refund.getStatus()) {
                throw new BussinessException(BizExceptionEnum.ORDER_GOODS_CAN_NOT_REFUND);
            }
        }

        OrderGoodsDTO orderGoodsDTO = new OrderGoodsDTO();
        BeanUtils.copyProperties(orderGoods, orderGoodsDTO);
        orderGoodsDTO.setShopId(order.getShopId());                 // 店铺ID
        orderGoodsDTO.setOrderStatus(order.getStatus());            // 订单状态
        orderGoodsDTO.setApplyNum(refundList.size());               // 已申请次数
        orderGoodsDTO.setConsigneePhone(order.getConsigneePhone()); // 收件人手机号码
        return orderGoodsDTO;
    }

    /**
     * 申请退款页面获取商品信息
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    @Override
    public ApplyRefundVO getGoodsInfo4Refund(Long orderGoodsId) {
        // 校验是否已存在售后服务，且申请次数不得超过5次，返回订单商品信息
        OrderGoodsDTO orderGoodsDTO = checkServiced(orderGoodsId);

        ApplyRefundVO applyRefundVO = new ApplyRefundVO();
        applyRefundVO.setOrderGoodsId(orderGoodsId);                                                    // 订单商品ID

        // 查询订单商品信息
        GoodsVO goodsVO = new GoodsVO();
        goodsVO.setGoodsId(orderGoodsDTO.getGoodsId());                                                    // 商品ID
        goodsVO.setGoodsName(orderGoodsDTO.getGoodsName());                                                // 商品名称
        goodsVO.setGoodsImg(ImageUtil.setImageURL(orderGoodsDTO.getGoodsImg()));                           // 商品图片
        goodsVO.setGoodsSpecs(orderGoodsDTO.getGoodsSpecs());                                              // 商品规格
        goodsVO.setNum(orderGoodsDTO.getNums());                                                           // 购买数量
        applyRefundVO.setGoods(goodsVO);

        // 查询运费
        BigDecimal expressFee = getOrderExpressFee(orderGoodsDTO.getOrderId(), orderGoodsId);
        applyRefundVO.setExpressFee(AmountFormatUtil.amountFormat(expressFee));                             // 运费
        applyRefundVO.setAmount(AmountFormatUtil.amountFormat(orderGoodsDTO.getPrice().add(expressFee)));   // 可退金额
        applyRefundVO.setShipped(DeliveryStatus.DELIVERED.getCode() == orderGoodsDTO.getDeliveryStatus());  // 是否发货
        applyRefundVO.setDeliveryStatus(DeliveryStatus.valueOf(orderGoodsDTO.getDeliveryStatus()));         // 发货状态


        // 查询手机号码
        applyRefundVO.setPhone(orderGoodsDTO.getConsigneePhone());

        // 查询退款原因
        applyRefundVO.setReasonList(getRefundReasonList(applyRefundVO.getShipped()));
        return applyRefundVO;
    }

    /**
     * 申请退款
     *
     * @param applyRefundDTO 申请退款信息
     *                       orderGoodsId 订单商品ID
     *                       reasonId 退款原因
     *                       amount 退款金额
     *                       phone 联系手机
     *                       refundRemark 退款说明
     *                       imgList 凭证图片集合
     * @return
     */
    @Transactional
    @Override
    public void applyRefund(ApplyRefundDTO applyRefundDTO) {
        StaticCheck.check(applyRefundDTO);
        // 退款金额必须大于0
        if (BigDecimal.ZERO.compareTo(applyRefundDTO.getAmount()) >= 0) {
            throw new BussinessException(BizExceptionEnum.REFUND_AMOUNT_IS_EXCEED);
        }

        // 凭证图片不能多余5张
        if (null != applyRefundDTO.getImgList() && 5 < applyRefundDTO.getImgList().size()) {
            throw new BussinessException(BizExceptionEnum.IMG_LIST_SIZE_MAX_LIMIT);
        }

        // 校验是否已存在售后服务，且申请次数不得超过5次，返回订单商品信息
        OrderGoodsDTO orderGoodsDTO = checkServiced(applyRefundDTO.getOrderGoodsId());

        // 查询运费
        BigDecimal expressFee = getOrderExpressFee(orderGoodsDTO.getOrderId(), applyRefundDTO.getOrderGoodsId());

        // 判断退款金额是否小于 商品价格 + 运费
        if (orderGoodsDTO.getPrice().add(expressFee).compareTo(applyRefundDTO.getAmount()) < 0) {
            throw new BussinessException(BizExceptionEnum.REFUND_AMOUNT_IS_EXCEED);
        }

        // 退款商品数量校验
        if (null != applyRefundDTO.getGoodsNum() && orderGoodsDTO.getNums() < applyRefundDTO.getGoodsNum()) {
            throw new BussinessException(BizExceptionEnum.REFUND_GOODS_NUM_IS_ERROR);
        }
        // 售后商品数量
        int goodsNum = applyRefundDTO.getGoodsNum() == null ? orderGoodsDTO.getNums() : applyRefundDTO.getGoodsNum();

        // 1.新增退款记录
        ShopOrderRefund refund = new ShopOrderRefund();
        refund.setOrderGoodsId(applyRefundDTO.getOrderGoodsId());                               // 订单商品ID
        refund.setOrderId(orderGoodsDTO.getOrderId());                                          // 订单ID
        refund.setShopId(orderGoodsDTO.getShopId());                                            // 店铺ID
        refund.setRefundOrderNo(OrderUtils.getOrderNoByUUId(applyRefundDTO.getOrderGoodsId())); // 退款订单号
        // 退款类型（1-仅退款；2-退款退货；）
        int refundType = DeliveryStatus.WAIT_DELIVERY.getCode() == orderGoodsDTO.getDeliveryStatus()
                ? RefundType.AMOUNT.getCode() : RefundType.AMOUNT_GOODS.getCode();
        refund.setType(refundType);

        refund.setPhone(applyRefundDTO.getPhone());                                             // 手机号码
        refund.setGoodsNum(goodsNum);                                                           // 售后商品数量
        refund.setRefundAmount(applyRefundDTO.getAmount().multiply(BigDecimal.valueOf(100)));   // 退款金额（分）
        refund.setReasonId(applyRefundDTO.getReasonId());                                       // 退款原因
        refund.setExpressFee(expressFee);                                                       // 运费
        refund.setRefundRemark(applyRefundDTO.getRefundRemark());                               // 退款说明
        refund.setImgs(ImageUtil.cutImageListURL2Str(applyRefundDTO.getImgList()));             // 凭证图片
        refund.setStatus(RefundStatus.WAIT_REVIEW.getCode());                                   // 退款状态-待审核
        refund.setCreatedTime(new Date());
        // 登录用户ID
        Long userId = ConstantFactory.me().getUserId();

        refund.setCreatedUser(userId);
        refund.setIsDeleted(IsDeleted.NORMAL.getCode());
        refund.insert();

        // 2.判断该商品在订单中是否为最后一件售后商品，true-是：修改订单状态为退款中
        boolean isLastService = checkGoodsIsLastService(refund.getOrderId(), refund.getOrderGoodsId());
        if (isLastService) {
            ShopOrder order = new ShopOrder();
            order.setId(orderGoodsDTO.getOrderId());
            order.setStatus(ShopOrderStatus.SERVICE.getCode());
            order.setPreStatus(orderGoodsDTO.getOrderStatus()); // 记录申请退款前的订单状态，撤销时使用
            order.setModifiedTime(new Date());
            order.setModifiedUser(userId);
            order.updateById();
        }

        // 3.修改订单商品售后状态
        ShopOrderGoods orderGoods = new ShopOrderGoods();
        orderGoods.setId(applyRefundDTO.getOrderGoodsId());
        orderGoods.setServiceStatus(ServiceStatus.REFUNDING.getCode());
        orderGoods.setModifiedTime(new Date());
        orderGoods.setModifiedUser(userId);
        orderGoods.updateById();

        // 发送消息：售后订单7天内店铺未做处理，自动审核通过
        messageSenderTask.sendMsgOfReviewRefund(refund.getId());
    }

    /**
     * 退款详情
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    @Override
    public RefundDetailVO getRefundDetail(Long orderGoodsId) {
        // 校验退款订单ID
        checkOrderGoodsId(orderGoodsId);

        // 根据订单商品ID查询退款订单信息
        ShopOrderRefund refund = getRefundByOrderGoodsId(orderGoodsId);

        RefundDetailVO refundDetailVO = new RefundDetailVO();
        refundDetailVO.setRefundId(refund.getId());
        refundDetailVO.setRefundOrderNo(refund.getRefundOrderNo());
        refundDetailVO.setRefundRemark(refund.getRefundRemark());
        refundDetailVO.setCreatedTime(DateUtils.convertDateFormat(refund.getCreatedTime()));            // 申请时间
        refundDetailVO.setRefundTypeCode(refund.getType());                                             // 退款方式码（1-仅退款；2-退货退款；）
        refundDetailVO.setRefundType(RefundType.valueOf(refund.getType()));
        refundDetailVO.setAmount(AmountFormatUtil.amountFormat(refund.getRefundAmount()));
        refundDetailVO.setExpressFee(AmountFormatUtil.amountFormat(refund.getExpressFee()));
        refundDetailVO.setReason(ConstantFactory.me().getDictsByName("退款原因", refund.getReasonId()));
        refundDetailVO.setImgList(ImageUtil.setImageStrURL2List(refund.getImgs()));
        refundDetailVO.setRefundStatus(refund.getStatus());
        refundDetailVO.setReviewRemark(refund.getReviewRemark());
        refundDetailVO.setReviewTime(DateUtils.convertDateFormat(refund.getReviewTime()));              // 审核时间

        refundDetailVO.setRevokeReason(refund.getRevokeReason());
        refundDetailVO.setRevokeTime(DateUtils.convertDateFormat(refund.getRevokeTime()));              // 撤销时间
        refundDetailVO.setExpectedTime(DateUtils.convertDateFormat(refund.getExpectedTime()));          // 退款时间（预计到账时间）

        // 查询商家电话
        refundDetailVO.setShopPhone(shopMapper.selectById(refund.getShopId()).getOfficeTelphone());

        // 查询订单商品信息
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectById(refund.getOrderGoodsId());
        if (null == orderGoods) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_IS_NOT_EXIST);
        }
        GoodsVO goodsVO = new GoodsVO();
        goodsVO.setGoodsId(orderGoods.getGoodsId());                                                    // 商品ID
        goodsVO.setGoodsName(orderGoods.getGoodsName());                                                // 商品名称
        goodsVO.setGoodsImg(ImageUtil.setImageURL(orderGoods.getGoodsImg()));                           // 商品图片
        goodsVO.setGoodsSpecs(orderGoods.getGoodsSpecs());                                              // 商品规格
        goodsVO.setNum(orderGoods.getNums());                                                           // 购买数量
        refundDetailVO.setGoods(goodsVO);

        // 定义计算截止时间相关参数
        Date userApplyTime = refund.getCreatedTime();           // 用户申请时间
        Date shopReviewTime = refund.getReviewTime();           // 店铺审核时间
        Date userDeliveryTime = null;                           // 用户发货时间

        // 退款类型为退货退款，查询退货物流信息
        if (RefundType.AMOUNT_GOODS.getCode() == refund.getType()) {
            ShopOrderRefundLogistics refundLogistics = getLogisticsByRefundId(refund.getId());
            if (null != refundLogistics) {
                RefundLogisticsVO logisticsVO = new RefundLogisticsVO();
                BeanUtils.copyProperties(refundLogistics, logisticsVO);
                logisticsVO.setImgList(ImageUtil.setImageStrURL2List(refundLogistics.getImgs()));

                // 用户发货时间
                logisticsVO.setDeliveryTime(DateUtils.convertDateFormat(refundLogistics.getDeliveryTime()));

                // 店铺收货时间
                logisticsVO.setReceiveTime(DateUtils.convertDateFormat(refundLogistics.getReceiveTime()));

                // 拒收时间
                logisticsVO.setRejectTime(DateUtils.convertDateFormat(refundLogistics.getRejectTime()));

                refundDetailVO.setLogistics(logisticsVO);

                // 用户收货时间，计算截止时间使用
                userDeliveryTime = refundLogistics.getDeliveryTime();
            }
        }

        // 计算退款截止时间：x天x时x分
        refundDetailVO.setOffTime(caluateOffTime(refund.getStatus(), userApplyTime, shopReviewTime, userDeliveryTime));

        // 用户已申请次数
        refundDetailVO.setApplyNum(getApplyRefundNum(orderGoodsId));

        return refundDetailVO;
    }

    /**
     * 查询用户已申请次数
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    private Integer getApplyRefundNum(Long orderGoodsId) {
        // 查询售后信息，申请次数不得超过5次
        List<ShopOrderRefund> refundList = shopOrderRefundMapper.selectList(
                new EntityWrapper<ShopOrderRefund>()
                        .eq("order_goods_id", orderGoodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode()));
        return null != refundList ? refundList.size() : 0;
    }

    /**
     * 获取快递公司集合
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> listExpressCompanys() {
        List<Map<String, Object>> resultList = ConstantFactory.me().getSubListByName("快递公司");
        return resultList;
    }

    /**
     * 填写退货物流
     *
     * @param logisticsDTO 申请退款信息
     *                     refundId 退款订单ID
     *                     expressCompany 物流公司
     *                     expressNo 物流单号
     *                     expressRemark 物流说明
     *                     imgList 凭证图片集合
     */
    @Override
    public void addLogistics(LogisticsDTO logisticsDTO) {
        // 校验退款订单ID
        checkRefundId(logisticsDTO.getRefundId());

        // 查询退货物流信息
        ShopOrderRefundLogistics refundLogistics = getLogisticsByRefundId(logisticsDTO.getRefundId());
        if (null == refundLogistics) {
            throw new BussinessException(BizExceptionEnum.REFUND_LOGISTICS_IS_NOT_EXIST);
        }

        // 判断物流状态是否为待发货状态
        if (DeliveryStatus.WAIT_DELIVERY.getCode() != refundLogistics.getStatus()) {
            throw new BussinessException(BizExceptionEnum.REFUND_LOGISTICS_IS_NOT_WAIT_DELIVERY);
        }

        BeanUtils.copyProperties(logisticsDTO, refundLogistics);

        refundLogistics.setImgs(ImageUtil.cutImageListURL2Str(logisticsDTO.getImgList()));
        refundLogistics.setStatus(DeliveryStatus.DELIVERED.getCode());
        refundLogistics.setDeliveryTime(new Date());
        refundLogistics.setModifiedTime(new Date());
        refundLogistics.setModifiedUser(ConstantFactory.me().getUserId());
        refundLogistics.updateById();

        //添加消息队列，用户填写退货物流后，商家超过7天未审核，系统默认审核通过并退款
        messageSenderTask.sendMsgOfReviewRefund(refundLogistics.getRefundId(), refundLogistics.getId());
    }

    /**
     * 用户撤销
     *
     * @param refundId 退款订单ID
     */
    @Transactional
    @Override
    public void revoke(Long refundId) {
        // 校验退款订单ID，并返回退款订单信息
        ShopOrderRefund refund = checkRefundId(refundId);
        if (RefundStatus.WAIT_REVIEW.getCode() != refund.getStatus() && RefundStatus.PASS.getCode() != refund.getStatus()) {
            throw new BussinessException(BizExceptionEnum.REFUND_STATUS_CAN_NOT_REVOKE);
        }

        // 查询用户是否已发货，用户已发货，该退款订单不能被撤销
        ShopOrderRefundLogistics refundLogistics = getLogisticsByRefundId(refund.getId());
        if (null != refundLogistics && DeliveryStatus.WAIT_DELIVERY.getCode() != refundLogistics.getStatus()) {
            throw new BussinessException(BizExceptionEnum.REFUND_HAS_LOGISTICS_CAN_NOT_REVOKE);
        }

        refund.setStatus(RefundStatus.REVOKE.getCode());
        refund.setRevokeTime(new Date());
        refund.setRevokeReason("买家主动撤销");
        refund.setModifiedTime(new Date());

        Long userId = ConstantFactory.me().getUserId();
        refund.setModifiedUser(userId);
        refund.updateById();

        // 修改订单商品售后状态
        ShopOrderGoods orderGoods = new ShopOrderGoods();
        orderGoods.setId(refund.getOrderGoodsId());
        orderGoods.setServiceStatus(ServiceStatus.NO_SERVICE.getCode());
        orderGoods.setModifiedTime(new Date());
        orderGoods.setModifiedUser(userId);
        orderGoods.updateById();

        // 不存在其他无售后的订单商品，修改订单状态
        boolean isLastService = checkGoodsIsLastService(refund.getOrderId(), refund.getOrderGoodsId());
        if (isLastService) {
            // 查询订单信息
            ShopOrder order = shopOrderMapper.selectById(refund.getOrderId());
            if (null != order) {
                order.setStatus(order.getPreStatus());
                order.setModifiedTime(new Date());
                order.setModifiedUser(userId);
                order.updateById();
            }
        }

        // 发送消息：订单7天内无申请售后，自动结算
        messageSenderTask.sendMsgOfSettleOrder(refund.getOrderId());
    }

    /**
     * 校验订单商品ID
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    private ShopOrderGoods checkOrderGoodsId(Long orderGoodsId) {
        if (null == orderGoodsId) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_ID_IS_NULL);
        }
        // 查询订单商品信息
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectById(orderGoodsId);
        if (null == orderGoods) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_IS_NOT_EXIST);
        }
        return orderGoods;
    }

    /**
     * 获取订单运费
     *
     * @param orderId      订单ID
     * @param orderGoodsId 订单商品ID
     * @return
     */
    private BigDecimal getOrderExpressFee(Long orderId, Long orderGoodsId) {
        if (checkGoodsIsLastService(orderId, orderGoodsId)) {
            // 不存在其他无售后的订单商品，返回订单运费
            ShopOrder order = shopOrderMapper.selectById(orderId);
            return null == order ? BigDecimal.ZERO : order.getExpressFee();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 校验该商品在订单中是否为最后一件售后商品
     *
     * @param orderId      订单ID
     * @param orderGoodsId 订单商品ID
     * @return
     */
    private Boolean checkGoodsIsLastService(Long orderId, Long orderGoodsId) {
        List<ShopOrderGoods> orderGoodsList = shopOrderGoodsMapper.selectList(
                new EntityWrapper<ShopOrderGoods>()
                        .eq("order_id", orderId)
                        .eq("service_status", ServiceStatus.NO_SERVICE.getCode())
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .ne("id", orderGoodsId));
        if (null == orderGoodsList || 0 == orderGoodsList.size()) {
            // 不存在其他无售后的订单商品，返回true
            return true;
        }
        return false;
    }

    /**
     * 获取退款原因集合
     *
     * @param isShipped 是否已发货
     * @return
     */
    private List<Map<String, Object>> getRefundReasonList(boolean isShipped) {
        List<Map<String, Object>> reasonList = ConstantFactory.me().getSubListByName("退款原因");
        // 未发货商品，除去序号为4-7的原因
        if (!isShipped) {
            for (Iterator<Map<String, Object>> iterator = reasonList.iterator(); iterator.hasNext(); ) {
                Map<String, Object> map = iterator.next();
                int num = (int) map.get("num");
                if (3 < num && 8 > num) {
                    iterator.remove();
                }
            }
        }
        return reasonList;
    }

    /**
     * 根据订单商品ID查询退款订单信息
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    private ShopOrderRefund getRefundByOrderGoodsId(Long orderGoodsId) {
        List<ShopOrderRefund> orderRefundList = shopOrderRefundMapper.selectList(
                new EntityWrapper<ShopOrderRefund>()
                        .eq("order_goods_id", orderGoodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .orderBy("created_time", false)
                        .last("LIMIT 1"));
        if (null == orderRefundList || 0 == orderRefundList.size()) {
            throw new BussinessException(BizExceptionEnum.REFUND_ORDER_IS_NOT_EXIST);
        }
        return orderRefundList.get(0);
    }

    /**
     * 计算退款处理截止时间
     *
     * @param refundStatus     退款状态
     * @param userApplyTime    用户申请时间
     * @param shopReviewTime   店铺审核时间
     * @param userDeliveryTime 用户发货时间
     * @return
     */
    private String caluateOffTime(Integer refundStatus, Date userApplyTime, Date shopReviewTime, Date userDeliveryTime) {
        // 退款状态不为待审核和审核通过状态，不计算截止时间
        if (RefundStatus.WAIT_REVIEW.getCode() != refundStatus && RefundStatus.PASS.getCode() != refundStatus) {
            return null;
        }
        Date fetureTime = null;
        if (null != userDeliveryTime) {
            fetureTime = DateUtils.getFetureTime(userDeliveryTime, Const.HANDLE_DAY_LIMIT);
        } else if (null != shopReviewTime) {
            fetureTime = DateUtils.getFetureTime(shopReviewTime, Const.HANDLE_DAY_LIMIT);
        } else if (null != userApplyTime) {
            fetureTime = DateUtils.getFetureTime(userApplyTime, Const.HANDLE_DAY_LIMIT);
        }
        return DateUtils.getDateDiff(new Date(), fetureTime);
    }

    /**
     * 校验退款订单ID，并返回退款订单信息
     *
     * @param refundId 退款订单ID
     * @return
     */
    private ShopOrderRefund checkRefundId(Long refundId) {
        if (null == refundId) {
            throw new BussinessException(BizExceptionEnum.REFUND_ID_IS_NULL);
        }
        ShopOrderRefund refund = shopOrderRefundMapper.selectById(refundId);
        if (null == refund) {
            throw new BussinessException(BizExceptionEnum.REFUND_ORDER_IS_NOT_EXIST);
        }
        return refund;
    }

    /**
     * 根据退款订单ID查询退货物流信息
     *
     * @param refundId 退款订单ID
     * @return
     */
    private ShopOrderRefundLogistics getLogisticsByRefundId(Long refundId) {
        ShopOrderRefundLogistics refundLogistics = new ShopOrderRefundLogistics();
        refundLogistics.setRefundId(refundId);
        refundLogistics.setIsDeleted(IsDeleted.NORMAL.getCode());
        refundLogistics = shopOrderRefundLogisticsMapper.selectOne(refundLogistics);
        return refundLogistics;
    }
}
