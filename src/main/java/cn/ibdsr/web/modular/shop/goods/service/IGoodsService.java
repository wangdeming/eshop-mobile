package cn.ibdsr.web.modular.shop.goods.service;

import cn.ibdsr.web.common.persistence.model.Goods;
import cn.ibdsr.web.modular.shop.goods.transfer.GoodsDetailVO;
import cn.ibdsr.web.modular.shop.goods.transfer.GoodsListVO;
import cn.ibdsr.web.modular.shop.goods.transfer.GoodsSkuVO;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;
import java.util.Map;

/**
 * @Description 商品信息Service
 * @Version V1.0
 * @CreateDate 2019-03-15 16:16:47
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 16:16:47    XuZhipeng               类说明
 *
 */
public interface IGoodsService {

    /**
     * 店铺主页 查询最新商品
     *
     * @param shopId 店铺ID
     * @return
     */
    List<GoodsListVO> listShopLatestGoods(Long shopId);

    /**
     * 店铺主页 根据店铺ID分页获取商品列表
     *
     * @param page 分页信息
     * @param shopId 店铺ID
     * @return
     */
    List<GoodsListVO> listShopGoods(Page<GoodsListVO> page, Long shopId);

    /**
     * 查询商品详情
     *
     * @param goodsId 商品ID
     * @return
     */
    GoodsDetailVO detailGoods(Long goodsId);

    /**
     * 查询规格商品信息
     *
     * @param goodsId 商品ID
     * @param specs 规格信息（格式为json字符串，例：{"包装":"1KG","口味":"香辣"}）
     * @return
     */
    GoodsSkuVO getGoodsSku(Long goodsId, String specs);

    /**
     * 校验goodsId是否存在
     *
     * @param goodsId 商品ID
     * @return
     */
    Goods checkGoodsId(Long goodsId);

    /**
     * 查询所有规格商品对应的库存信息
     *
     * @param goodsId 商品ID
     * @return
     */
    Map<String, Object> getGoodsSkuNumMap(Long goodsId);

    /**
     * 获取规格商品库存数量集合
     *
     * @param goodsId 商品ID
     * @param specs 规格参数
     * @return
     */
    List<Map<String, Object>> getGoodsSkuStockList(Long goodsId, String specs);
}
