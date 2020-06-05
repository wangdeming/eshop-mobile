package cn.ibdsr.web.modular.shop.goods.transfer;

import java.util.List;

/**
 * @Description 商品详情VO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-18 09:31:22    XuZhipeng               类说明
 *
 */
public class GoodsDetailVO {

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品主图（选择规格时用）
     */
    private String goodsMainImg;

    /**
     * 商品图片集合
     */
    private List<String> goodsImgList;

    /**
     * 价格
     */
    private String price;

    /**
     * 划线价
     */
    private String referPrice;

    /**
     * 运费
     */
    private String expressFee;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 销量
     */
    private Integer saleNum;

    /**
     * 商品规格
     */
    private String specsList;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 商品介绍
     */
    private String goodsIntro;

    /**
     * 店铺信息对象
     */
    private ShopVO shop;

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsMainImg() {
        return goodsMainImg;
    }

    public void setGoodsMainImg(String goodsMainImg) {
        this.goodsMainImg = goodsMainImg;
    }

    public List<String> getGoodsImgList() {
        return goodsImgList;
    }

    public void setGoodsImgList(List<String> goodsImgList) {
        this.goodsImgList = goodsImgList;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getReferPrice() {
        return referPrice;
    }

    public void setReferPrice(String referPrice) {
        this.referPrice = referPrice;
    }

    public String getExpressFee() {
        return expressFee;
    }

    public void setExpressFee(String expressFee) {
        this.expressFee = expressFee;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }

    public String getSpecsList() {
        return specsList;
    }

    public void setSpecsList(String specsList) {
        this.specsList = specsList;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getGoodsIntro() {
        return goodsIntro;
    }

    public void setGoodsIntro(String goodsIntro) {
        this.goodsIntro = goodsIntro;
    }

    public ShopVO getShop() {
        return shop;
    }

    public void setShop(ShopVO shop) {
        this.shop = shop;
    }
}
