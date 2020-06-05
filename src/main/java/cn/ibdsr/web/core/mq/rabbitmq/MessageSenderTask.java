package cn.ibdsr.web.core.mq.rabbitmq;


import cn.ibdsr.web.common.constant.state.DelayTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 消息发送服务
 * @Version V1.0
 * @CreateDate 2019-04-26 08:31:22
 * <p>
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-26 08:31:22    XuZhipeng               类说明
 */
@Component
public class MessageSenderTask {

    // TODO: 2019/5/21 处于开发测试阶段，消息队列统一使用5分钟消息队列，待测试最后阶段统一更换对应时间消息队列，再次测试。

    @Autowired
    private MessageSender messageSender;

    /**
     * 处理特产商家订单支付超时，1小时后自动取消
     *
     * @param shopOrderIdList 订单Id集合
     * @throws Exception
     */
    public void sendMsgOfCancelShopOrder(List<Long> shopOrderIdList) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("delayTask", DelayTask.SHOP_ORDER_CANCEL.getCode());
        paramMap.put("orderIdList", shopOrderIdList);
        //messageSender.sendMsgTTLOneHour("订单支付超时，自动取消", paramMap);
        messageSender.sendMsgTTLFiveMinute("订单支付超时，自动取消", paramMap);
    }

    /**
     * 处理订单7天内未发起售后，自动结算
     *
     * @param orderId 订单ID
     * @throws Exception
     */
    public void sendMsgOfSettleOrder(Long orderId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("delayTask", DelayTask.SHOP_ORDER_SETTLE.getCode());
        paramMap.put("orderId", orderId);
        //messageSender.sendMsgTTLSevenDay("订单完成，自动结算", paramMap);
        messageSender.sendMsgTTLFiveMinute("订单完成，自动结算", paramMap);
    }

    /**
     * 处理售后订单7天内店铺未做处理，自动审核通过
     *
     * @param refundId 退款ID
     * @throws Exception
     */
    public void sendMsgOfReviewRefund(Long refundId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("delayTask", DelayTask.SHOP_REFUND_PASS.getCode());
        paramMap.put("shopOrderRefundId", refundId);
        //messageSender.sendMsgTTLSevenDay("发起售后，自动审核", paramMap);
        messageSender.sendMsgTTLFiveMinute("发起售后，自动审核", paramMap);
    }

    /**
     * 用户填写退货物流后，商家需要在7天内，完成收货审核，否则系统帮助商家完成确认收获并退款。
     *
     * @param refundId                   退款ID
     * @param shopOrderRefundLogisticsId 退货物流ID
     */
    public void sendMsgOfReviewRefund(Long refundId, Long shopOrderRefundLogisticsId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("delayTask", DelayTask.SHOP_REFUND_PASS.getCode());
        paramMap.put("shopOrderRefundId", refundId);
        paramMap.put("shopOrderRefundLogisticsId", shopOrderRefundLogisticsId);
        //messageSender.sendMsgTTLSevenDay("用户填写退货物流后，自动审核", paramMap);
        messageSender.sendMsgTTLFiveMinute("用户填写退货物流后，自动审核", paramMap);
    }

    /**
     * 处理酒店订单支付超时，30分钟后自动取消
     *
     * @param orderId 酒店订单ID
     * @throws Exception
     */
    public void sendMsgOfCancelHotelOrder(String orderId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("delayTask", DelayTask.HOTEL_ORDER_CANCEL.getCode());
        paramMap.put("hotelOrderId", orderId);
        //messageSender.sendMsgTTLThirtyMinute(DelayTask.HOTEL_ORDER_CANCEL.getMessage(), paramMap);
        messageSender.sendMsgTTLFiveMinute(DelayTask.HOTEL_ORDER_CANCEL.getMessage(), paramMap);
    }

    /**
     * 处理酒店订单超时确认，30分钟后自动确认
     *
     * @param orderId 酒店订单ID
     * @throws Exception
     */
    public void sendMsgOfConfirmHotelOrder(String orderId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("delayTask", DelayTask.HOTEL_ORDER_CONFIRM.getCode());
        paramMap.put("hotelOrderId", orderId);
        //messageSender.sendMsgTTLThirtyMinute(DelayTask.HOTEL_ORDER_CONFIRM.getMessage(), paramMap);
        messageSender.sendMsgTTLFiveMinute(DelayTask.HOTEL_ORDER_CONFIRM.getMessage(), paramMap);
    }

}
