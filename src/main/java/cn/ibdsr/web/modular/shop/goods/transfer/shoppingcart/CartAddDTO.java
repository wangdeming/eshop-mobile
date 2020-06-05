package cn.ibdsr.web.modular.shop.goods.transfer.shoppingcart;

/**
 * @Description 新增商品到购物车DTO
 * @Version V1.0
 * @CreateDate 2019-03-21 15:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-21 15:31:22    XuZhipeng               类说明
 *
 */
public class CartAddDTO {

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 库存数量
     */
    private Integer stock;

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
