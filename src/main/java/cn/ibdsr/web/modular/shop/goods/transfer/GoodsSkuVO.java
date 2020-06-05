package cn.ibdsr.web.modular.shop.goods.transfer;

/**
 * @Description 规格商品信息VO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-19 09:57:22    XuZhipeng               类说明
 *
 */
public class GoodsSkuVO {

    /**
     * 规格商品ID
     */
    private Long skuId;

    /**
     * 价格
     */
    private String price;

    /**
     * 划线价
     */
    private String referPrice;

    /**
     * 规格商品图片
     */
    private String img;

    /**
     * 库存数量
     */
    private Integer stock;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
