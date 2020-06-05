package cn.ibdsr.web.modular.shop.goods.dao;

import cn.ibdsr.web.modular.shop.goods.transfer.GoodsEvaluateVO;
import cn.ibdsr.web.modular.shop.goods.transfer.GoodsListVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 商品信息Dao
 * @Version V1.0
 * @CreateDate 2019-03-15 16:16:47
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 16:16:47    XuZhipeng               类说明
 *
 */
public interface GoodsDao {

    /**
     * 店铺主页 查询最新商品
     *
     * @param shopId 店铺ID
     * @return
     */
    List<GoodsListVO> listShopLatestGoods(@Param("shopId") Long shopId);

    /**
     * 店铺主页 根据店铺ID分页获取商品列表
     *
     * @param page 分页信息
     * @param shopId 店铺ID
     * @return
     */
    List<GoodsListVO> listShopGoods(@Param("page") Page<GoodsListVO> page,
                                    @Param("shopId") Long shopId);

    /**
     * 获取商品最高售价
     *
     * @param goodsId 商品ID
     * @return
     */
    BigDecimal getMaxPrice(@Param("goodsId") Long goodsId);

    /**
     * 获取商品最高划线价
     *
     * @param goodsId 商品ID
     * @return
     */
    BigDecimal getMaxReferPrice(@Param("goodsId") Long goodsId);

    /**
     * 商品详情页面评价信息（最近时间评分最高的评价）
     *
     * @param goodsId 商品ID
     * @return
     */
    GoodsEvaluateVO getEvaluateOfGoodsDetail(@Param("goodsId") Long goodsId);
}
