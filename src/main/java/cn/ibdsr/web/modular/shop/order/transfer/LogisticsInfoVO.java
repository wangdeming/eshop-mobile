package cn.ibdsr.web.modular.shop.order.transfer;

import java.util.List;

/**
 * @Description 我的订单 物流信息VO
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public class LogisticsInfoVO {

    /**
     * 主键ID
     */
    private Long id;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 规格商品图片
     */
    private String goodsImg;
    /**
     * 规格信息（例：2KG,微辣）
     */
    private String goodsSpecs;
    /**
     * 商品出售单价（单位：分）
     */
    private String unitPrice;
    /**
     * 购买数量
     */
    private Integer nums;
    /**
     * 订单商品发货状态（1-待发货；2-已发货；3-已收货；）
     */
    private Integer deliveryStatus;

    private String deliveryStatusName;

    /**
     * 售后状态（1-无售后；2-退款中；3-已退款；4-退款失败（商家拒绝）；）
     */
    private Integer serviceStatus;

    private String serviceStatusName;
    /**
     * 快递公司
     */
    private String expressCompany;
    /**
     * 快递单号
     */
    private String expressNo;

    //private List<DeliveryInfoVO> deliveryInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getGoodsSpecs() {
        return goodsSpecs;
    }

    public void setGoodsSpecs(String goodsSpecs) {
        this.goodsSpecs = goodsSpecs;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryStatusName() {
        return deliveryStatusName;
    }

    public void setDeliveryStatusName(String deliveryStatusName) {
        this.deliveryStatusName = deliveryStatusName;
    }
    public Integer getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(Integer serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceStatusName() {
        return serviceStatusName;
    }

    public void setServiceStatusName(String serviceStatusName) {
        this.serviceStatusName = serviceStatusName;
    }

    public String getExpressCompany() {
        return expressCompany;
    }

    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany;
    }

    public String getExpressNo() {
        return expressNo;
    }

    public void setExpressNo(String expressNo) {
        this.expressNo = expressNo;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    /*public List<DeliveryInfoVO> getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(List<DeliveryInfoVO> deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }*/
}
