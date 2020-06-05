package cn.ibdsr.web.modular.shop.home.transfer;

/**
 * @Description 商品列表VO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-18 09:31:22    ZhuJingrui               类说明
 *
 */
public class GoodsListVO {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品主图片
     */
    private String img;

    /**
     * 价格
     */
    private String price;

    /**
     * 划线价
     */
    private String referPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 浏览量
     */
    private Integer viewNum;

    /**
     * 销量
     */
    private Integer saleNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getViewNum() {
        return viewNum;
    }

    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
    }

    public Integer getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }
}
