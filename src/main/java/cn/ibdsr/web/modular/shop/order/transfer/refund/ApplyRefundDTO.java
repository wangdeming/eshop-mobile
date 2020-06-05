package cn.ibdsr.web.modular.shop.order.transfer.refund;

import cn.ibdsr.core.base.dto.BaseDTO;
import cn.ibdsr.core.check.CheckUtil;
import cn.ibdsr.core.check.Verfication;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 申请退款DTO
 * @Version V1.0
 * @CreateDate 2019-04-02 10:30:38
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-02 10:30:38     XuZhipeng            类说明
 */
public class ApplyRefundDTO extends BaseDTO {

    /**
     * 订单商品ID
     */
    @Verfication(name = "订单商品ID", notNull = true)
    private Long orderGoodsId;

    /**
     * 退款原因
     */
    @Verfication(name = "退款原因", notNull = true, min = 1, max = 8)
    private Integer reasonId;

    /**
     * 退款金额
     */
    @Verfication(name = "退款金额", notNull = true)
    private BigDecimal amount;

    /**
     * 商品数量
     */
    private Integer goodsNum;

    /**
     * 用户联系手机
     */
    @Verfication(name = "手机号码", notNull = true, regx = {CheckUtil.MOBILE_PHONE, "格式不正确"})
    private String phone;

    /**
     * 退款说明
     */
    @Verfication(name = "退款说明", maxlength = 200)
    private String refundRemark;

    /**
     * 凭证图片集合
     */
    private List<String> imgList;

    public Long getOrderGoodsId() {
        return orderGoodsId;
    }

    public void setOrderGoodsId(Long orderGoodsId) {
        this.orderGoodsId = orderGoodsId;
    }

    public Integer getReasonId() {
        return reasonId;
    }

    public void setReasonId(Integer reasonId) {
        this.reasonId = reasonId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRefundRemark() {
        return refundRemark;
    }

    public void setRefundRemark(String refundRemark) {
        this.refundRemark = refundRemark;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
