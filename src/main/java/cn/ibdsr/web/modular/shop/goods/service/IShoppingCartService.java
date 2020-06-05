package cn.ibdsr.web.modular.shop.goods.service;

import java.util.List;
import java.util.Map;

/**
 * @Description 购物车Service
 * @Version V1.0
 * @CreateDate 2019-03-20 11:25:36
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-20 11:25:36    XuZhipeng               类说明
 *
 */
public interface IShoppingCartService {

    /**
     * 获取购物车列表
     *
     * @param cartGoodsType 购物车商品类型
     * @return
     */
    Map<String, Object> listCarts(Integer cartGoodsType);

    /**
     * 新增商品到购物车
     *
     * @param goodsId 商品ID
     * @param skuId 规格商品ID
     * @param num 数量
     */
    void add(Long goodsId, Long skuId, Integer num);

    /**
     * 购物车指定商品数量增加或减少
     *
     * @param cartId 购物车ID
     * @param num 增加或减少数量（正数表示增加，负数表示减少）
     */
    void addOrMinus(Long cartId, Integer num);

    /**
     * 修改指定商品购物车数量
     *
     * @param cartId 购物车ID
     * @param num 数量
     */
    void updateNum(Long cartId, Integer num);

    /**
     * 删除购物车商品
     *
     * @param cartIdList 购物车商品ID集合
     */
    void delete(List<Long> cartIdList);

    /**
     * 清空购物车失效商品
     */
    void clean();
}
