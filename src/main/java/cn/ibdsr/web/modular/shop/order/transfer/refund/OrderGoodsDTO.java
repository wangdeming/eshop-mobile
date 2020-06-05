package cn.ibdsr.web.modular.shop.order.transfer.refund;

import cn.ibdsr.web.common.persistence.model.ShopOrderGoods;

/**
 * @Description 申请退款订单商品信息DTO
 * @Version V1.0
 * @CreateDate 2019-04-02 10:30:38
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-02 10:30:38     XuZhipeng            类说明
 */
public class OrderGoodsDTO extends ShopOrderGoods {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 申请次数
     */
    private Integer applyNum;

    /**
     * 收件人手机号
     */
    private String consigneePhone;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(Integer applyNum) {
        this.applyNum = applyNum;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }
}
