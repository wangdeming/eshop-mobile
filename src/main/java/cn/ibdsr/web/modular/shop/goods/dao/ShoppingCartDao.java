package cn.ibdsr.web.modular.shop.goods.dao;

import cn.ibdsr.web.modular.shop.goods.transfer.shoppingcart.CartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 购物车Dao
 * @Version V1.0
 * @CreateDate 2019-03-20 11:25:36
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-20 11:25:36    XuZhipeng               类说明
 *
 */
public interface ShoppingCartDao {

    /**
     * 查询购物车商品列表
     *
     * @param userId 用户ID
     * @param cartGoodsType 购物车商品类型（1-正常商品；2-失效商品；）
     * @return
     */
    List<CartVO> listCarts(@Param("userId") Long userId,
                           @Param("type") Integer cartGoodsType);

    /**
     * 批量删除用户购物车
     *
     * @param cartIds 购物车ID集合
     * @param userId 用户ID
     * @return
     */
    Integer batchDeleteCarts(@Param("cartIds") List<Long> cartIds,
                             @Param("userId") Long userId);

    /**
     * 查询已失效商品的购物车ID列表
     *
     * @param userId 用户ID
     * @return
     */
    List<Long> listExpireCartIds(@Param("userId") Long userId);
}
