package cn.ibdsr.web.modular.shop.order.transfer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description 我的订单 订单详情VO
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public class OrderDetailVO extends OrderVO{

    /**
     * 收货人姓名
     */
    private String consigneeName;
    /**
     * 收货人手机号
     */
    private String consigneePhone;
    /**
     * 收货地址：省
     */
    private String province;
    /**
     * 收货地址：市
     */
    private String city;
    /**
     * 收货地址：区
     */
    private String district;
    /**
     * 收货地址
     */
    private String address;

    /**
     * 配送方式（1-快递发货；）
     */
    private Integer deliveryType;

    private String deliveryTypeName;
    /**
     * 物流信息
     */
    //private String logisticsInfo;
    /**
     * 运输时间
     */
    private Date transportTime;
    /**
     * 运输状态
     */
    private Integer transportType;
    /**
     * 配送内容详情
     */
    private String transportContent;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 发货时间
     */
    private Date deliveryTime;
    /**
     * 取消（关闭）时间
     */
    private Date cancelTime;
    /**
     * 创建时间（下单时间）
     */
    private Date createdTime;
    /**
     * 收货时间
     */
    private Date receiveTime;

    /**
     * 截止时间
     */
    private String offTime;


    public Integer getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryTypeName() {
        return deliveryTypeName;
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        this.deliveryTypeName = deliveryTypeName;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Date getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getTransportTime() {
        return transportTime;
    }

    public void setTransportTime(Date transportTime) {
        this.transportTime = transportTime;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getTransportContent() {
        return transportContent;
    }

    public void setTransportContent(String transportContent) {
        this.transportContent = transportContent;
    }

    public String getOffTime() {
        return offTime;
    }

    public void setOffTime(String offTime) {
        this.offTime = offTime;
    }
}
