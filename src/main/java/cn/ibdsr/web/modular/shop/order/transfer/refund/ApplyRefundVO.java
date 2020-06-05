package cn.ibdsr.web.modular.shop.order.transfer.refund;

import java.util.List;
import java.util.Map;

/**
 * @Description 申请退款页面 商品信息VO
 * @Version V1.0
 * @CreateDate 2019-04-02 10:30:38
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-02 10:30:38     XuZhipeng            类说明
 */
public class ApplyRefundVO {
    /**
     * 订单商品ID
     */
    private Long orderGoodsId;

    /**
     * 是否发货
     */
    private Boolean isShipped;

    /**
     * 发货状态
     */
    private String deliveryStatus;

    /**
     * 可退金额（实际支付金额）
     */
    private String amount;

    /**
     * 快递运费（单位：分）
     */
    private String expressFee;

    /**
     * 用户联系手机
     */
    private String phone;

    /**
     * 退款原因
     */
    private List<Map<String, Object>> reasonList;

    /**
     * 订单商品信息
     */
    private GoodsVO goods;

    public Long getOrderGoodsId() {
        return orderGoodsId;
    }

    public void setOrderGoodsId(Long orderGoodsId) {
        this.orderGoodsId = orderGoodsId;
    }

    public Boolean getShipped() {
        return isShipped;
    }

    public void setShipped(Boolean shipped) {
        isShipped = shipped;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getExpressFee() {
        return expressFee;
    }

    public void setExpressFee(String expressFee) {
        this.expressFee = expressFee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Map<String, Object>> getReasonList() {
        return reasonList;
    }

    public void setReasonList(List<Map<String, Object>> reasonList) {
        this.reasonList = reasonList;
    }

    public GoodsVO getGoods() {
        return goods;
    }

    public void setGoods(GoodsVO goods) {
        this.goods = goods;
    }
}
