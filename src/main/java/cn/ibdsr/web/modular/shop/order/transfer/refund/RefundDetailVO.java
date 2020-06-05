package cn.ibdsr.web.modular.shop.order.transfer.refund;

import java.util.List;

/**
 * @Description 退款详情信息VO
 * @Version V1.0
 * @CreateDate 2019-04-02 10:30:38
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-02 10:30:38     XuZhipeng            类说明
 */
public class RefundDetailVO {

    /**
     * 退款订单ID
     */
    private Long refundId;

    /**
     * 退款方式码（1-仅退款；2-退货退款；）
     */
    private Integer refundTypeCode;

    /**
     * 退款方式
     */
    private String refundType;

    /**
     * 退款原因
     */
    private String reason;

    /**
     * 退款金额
     */
    private String amount;

    /**
     * 快递运费（单位：分）
     */
    private String expressFee;

    /**
     * 凭证图片集合
     */
    private List<String> imgList;

    /**
     * 退款状态
     */
    private Integer refundStatus;

    /**
     * 退款订单号
     */
    private String refundOrderNo;

    /**
     * 退款说明
     */
    private String refundRemark;

    /**
     * 申请时间
     */
    private String createdTime;

    /**
     * 审核时间
     */
    private String reviewTime;

    /**
     * 审核说明
     */
    private String reviewRemark;

    /**
     * 商家电话
     */
    private String shopPhone;

    /**
     * 订单商品信息
     */
    private GoodsVO goods;

    /**
     * 退货物流信息
     */
    private RefundLogisticsVO logistics;

    /**
     * 截止时间
     */
    private String offTime;

    /**
     * 撤销原因
     */
    private String revokeReason;

    /**
     * 撤销时间
     */
    private String revokeTime;

    /**
     * 预计到账时间（退款时间）
     */
    private String expectedTime;

    /**
     * 申请次数
     */
    private Integer applyNum;

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }

    public Integer getRefundTypeCode() {
        return refundTypeCode;
    }

    public void setRefundTypeCode(Integer refundTypeCode) {
        this.refundTypeCode = refundTypeCode;
    }

    public String getRefundType() {
        return refundType;
    }

    public void setRefundType(String refundType) {
        this.refundType = refundType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
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

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getRefundOrderNo() {
        return refundOrderNo;
    }

    public void setRefundOrderNo(String refundOrderNo) {
        this.refundOrderNo = refundOrderNo;
    }

    public String getRefundRemark() {
        return refundRemark;
    }

    public void setRefundRemark(String refundRemark) {
        this.refundRemark = refundRemark;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewRemark() {
        return reviewRemark;
    }

    public void setReviewRemark(String reviewRemark) {
        this.reviewRemark = reviewRemark;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public GoodsVO getGoods() {
        return goods;
    }

    public void setGoods(GoodsVO goods) {
        this.goods = goods;
    }

    public RefundLogisticsVO getLogistics() {
        return logistics;
    }

    public void setLogistics(RefundLogisticsVO logistics) {
        this.logistics = logistics;
    }

    public String getOffTime() {
        return offTime;
    }

    public void setOffTime(String offTime) {
        this.offTime = offTime;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }

    public String getRevokeTime() {
        return revokeTime;
    }

    public void setRevokeTime(String revokeTime) {
        this.revokeTime = revokeTime;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public Integer getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(Integer applyNum) {
        this.applyNum = applyNum;
    }
}
