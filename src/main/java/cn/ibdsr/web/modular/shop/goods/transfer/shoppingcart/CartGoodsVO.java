package cn.ibdsr.web.modular.shop.goods.transfer.shoppingcart;

/**
 * @Description 购物车商品信息VO
 * @Version V1.0
 * @CreateDate 2019-03-21 15:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-21 15:31:22    XuZhipeng               类说明
 *
 */
public class CartGoodsVO {

    /**
     * 购物车ID
     */
    private Long cartId;

    /**
     * 购物车商品数量
     */
    private Integer num;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 规格商品ID
     */
    private Long skuId;

    /**
     * 规格信息
     */
    private String specs;

    /**
     * 商品图片
     */
    private String goodsImg;

    /**
     * 出售价格
     */
    private String price;

    /**
     * 商品库存数量
     */
    private Integer stock;

    /**
     * 商品状态
     */
    private Integer status;

    /**
     * 商品状态名称
     */
    private String statusName;

    /**
     * 被删除商品，在购物车是算下架商品
     */
    private Integer mainGoodsDel;

    /**
     * 被删除的规格商品，在购物车是算下架商品
     */
    private Integer skuGoodsDel;

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

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

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
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

    public Integer getMainGoodsDel() {
        return mainGoodsDel;
    }

    public void setMainGoodsDel(Integer mainGoodsDel) {
        this.mainGoodsDel = mainGoodsDel;
    }

    public Integer getSkuGoodsDel() {
        return skuGoodsDel;
    }

    public void setSkuGoodsDel(Integer skuGoodsDel) {
        this.skuGoodsDel = skuGoodsDel;
    }
}
