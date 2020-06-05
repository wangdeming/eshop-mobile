package cn.ibdsr.web.modular.shop.goods.transfer.shoppingcart;

import cn.ibdsr.web.modular.shop.goods.transfer.ShopVO;

import java.util.List;

/**
 * @Description 购物车店铺商品列表VO
 * @Version V1.0
 * @CreateDate 2019-03-21 15:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-21 15:31:22    XuZhipeng               类说明
 *
 */
public class CartVO extends ShopVO {

    /**
     * 购物车商品信息列表
     */
    private List<CartGoodsVO> cartGoodsList;

    public List<CartGoodsVO> getCartGoodsList() {
        return cartGoodsList;
    }

    public void setCartGoodsList(List<CartGoodsVO> cartGoodsList) {
        this.cartGoodsList = cartGoodsList;
    }
}
