package cn.ibdsr.web.common.persistence.model;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 特产商店、酒店、商品、房间浏览记录表数据统计表
 * </p>
 *
 * @author xxx
 * @since 2019-05-27
 */
@TableName("view_stats")
public class ViewStats extends Model<ViewStats> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
	@TableId(value="id", type= IdType.AUTO)
	private Long id;
	@TableField("shop_id")
	private Long shopId;
    /**
     * 商品ID
     */
	@TableField("goods_id")
	private Long goodsId;
    /**
     * 房间ID
     */
	@TableField("room_id")
	private Long roomId;
    /**
     * 浏览量
     */
	@TableField("view_num")
	private Integer viewNum;
    /**
     * 访客数
     */
	@TableField("visitor_num")
	private Integer visitorNum;
    /**
     * 付款数
     */
	@TableField("payment_num")
	private Integer paymentNum;
    /**
     * 销量
     */
	@TableField("sale_num")
	private Integer saleNum;
    /**
	 * 创建人
	 */
	@TableField("created_user")
	private Long createdUser;
	/**
	 * 创建时间（发货时间）
	 */
	@TableField("created_time")
	private Date createdTime;
	/**
	 * 修改人
	 */
	@TableField("modified_user")
	private Long modifiedUser;
	/**
	 * 修改时间
	 */
	@TableField("modified_time")
	private Date modifiedTime;
	/**
	 * 是否删除
	 */
	@TableField("is_deleted")
	private Integer isDeleted;


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

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public Integer getViewNum() {
		return viewNum;
	}

	public void setViewNum(Integer viewNum) {
		this.viewNum = viewNum;
	}

	public Integer getVisitorNum() {
		return visitorNum;
	}

	public void setVisitorNum(Integer visitorNum) {
		this.visitorNum = visitorNum;
	}

	public Integer getPaymentNum() {
		return paymentNum;
	}

	public void setPaymentNum(Integer paymentNum) {
		this.paymentNum = paymentNum;
	}

	public Integer getSaleNum() {
		return saleNum;
	}

	public void setSaleNum(Integer saleNum) {
		this.saleNum = saleNum;
	}

	public Long getCreatedUser() {
		return createdUser;
	}

	public void setCreatedUser(Long createdUser) {
		this.createdUser = createdUser;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Long getModifiedUser() {
		return modifiedUser;
	}

	public void setModifiedUser(Long modifiedUser) {
		this.modifiedUser = modifiedUser;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Integer getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Integer isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	protected Serializable pkVal() {
		return this.id;
	}

	@Override
	public String toString() {
		return "ViewStats{" +
			"id=" + id +
				", shopId=" + shopId +
			", goodsId=" + goodsId +
				", roomId=" + roomId +
			", viewNum=" + viewNum +
			", visitorNum=" + visitorNum +
			", paymentNum=" + paymentNum +
			", saleNum=" + saleNum +
				", createdUser=" + createdUser +
				", createdTime=" + createdTime +
				", modifiedUser=" + modifiedUser +
				", modifiedTime=" + modifiedTime +
			", isDeleted=" + isDeleted +
			"}";
	}
}
