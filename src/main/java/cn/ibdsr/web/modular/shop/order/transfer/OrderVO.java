package cn.ibdsr.web.modular.shop.order.transfer;

import java.util.List;


/**
 * @Description 我的订单 订单信息VO
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public class OrderVO{
    /**
     * 订单ID
     */
    private Long id;
    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String shopLogo;
    /**
     * 状态（1-待付款；2-待发货；3-待收货；4-已收货（待评价）；5-已取消；6-售后中；7-交易关闭；）
     */
    private Integer status;

    /**
     * 是否已评价
     */
    private Integer isEvaluated;

    private String statusName;
    /**
     * 订单价格（实际支付金额 = 商品总价 - 优惠券金额 + 快递运费，单位：分）
     */
    private String orderPrice;

    /**
     * 快递运费（单位：分）
     */
    private String expressFee;

    private List<OrderGoodsVO> goods;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getExpressFee() {
        return expressFee;
    }

    public void setExpressFee(String expressFee) {
        this.expressFee = expressFee;
    }

    public List<OrderGoodsVO> getGoods() {
        return goods;
    }

    public void setGoods(List<OrderGoodsVO> goods) {
        this.goods = goods;
    }


    public Integer getIsEvaluated() {
        return isEvaluated;
    }

    public void setIsEvaluated(Integer isEvaluated) {
        this.isEvaluated = isEvaluated;
    }
}
