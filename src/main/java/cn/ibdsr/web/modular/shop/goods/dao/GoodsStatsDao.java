package cn.ibdsr.web.modular.shop.goods.dao;

/**
 * @Description 商品相关统计信息Dao
 * @Version V1.0
 * @CreateDate 2019-03-22 16:16:47
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-22 16:16:47    XuZhipeng               类说明
 *
 */
public interface GoodsStatsDao {

    /**
     * 查询商品浏览两
     *
     * @param goodsId 商品ID
     * @return
     */
    Integer queryGoodsViewNum(Long goodsId);

    /**
     * 查询商品访客量
     *
     * @param goodsId 商品ID
     * @return
     */
    Integer queryGoodsVisitorNum(Long goodsId);
}
