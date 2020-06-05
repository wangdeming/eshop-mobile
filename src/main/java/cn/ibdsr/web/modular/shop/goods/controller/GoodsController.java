package cn.ibdsr.web.modular.shop.goods.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.constant.factory.PageFactory;
import cn.ibdsr.web.modular.shop.goods.service.IGoodsService;
import cn.ibdsr.web.modular.shop.goods.transfer.GoodsDetailVO;
import cn.ibdsr.web.modular.shop.goods.transfer.GoodsListVO;
import cn.ibdsr.web.modular.shop.goods.transfer.GoodsSkuVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description 商品信息控制器
 * @Version V1.0
 * @CreateDate 2019-03-15 16:16:47
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 16:16:47    XuZhipeng               类说明
 *
 */
@RestController
@RequestMapping("shop/goods")
public class GoodsController extends BaseController {

    @Autowired
    private IGoodsService goodsService;

    /**
     * 店铺主页 查询最新商品
     *
     * @param shopId 店铺ID
     * @return
     */
    @RequestMapping(value = "/listShopLatestGoods")
    public Object listShopLatestGoods(Long shopId) {
        List<GoodsListVO> goodsList = goodsService.listShopLatestGoods(shopId);
        return new SuccessDataTip(goodsList);
    }

    /**
     * 店铺主页 根据店铺ID分页获取商品列表
     *
     * @param shopId 店铺ID
     * @return
     */
    @RequestMapping(value = "/listShopGoods")
    public Object listShopGoods(Long shopId) {
        Page<GoodsListVO> page = new PageFactory<GoodsListVO>().defaultPage();
        List<GoodsListVO> result = goodsService.listShopGoods(page, shopId);
        page.setRecords(result);
        return new SuccessDataTip(super.packForBT(page));
    }

    /**
     * 查询商品详情
     *
     * @param goodsId 商品ID
     * @return
     */
    @RequestMapping(value = "/detailGoods")
    public Object detailGoods(Long goodsId) {
        GoodsDetailVO result = goodsService.detailGoods(goodsId);
        return new SuccessDataTip(result);
    }

    /**
     * 查询规格商品信息
     *
     * @param goodsId 商品ID
     * @param specs 规格信息（格式为json字符串，例：{"包装":"1KG","口味":"香辣"}）
     * @return
     */
    @RequestMapping(value = "/getGoodsSku")
    public Object getGoodsSku(Long goodsId, String specs) {
        GoodsSkuVO result = goodsService.getGoodsSku(goodsId, specs);
        return new SuccessDataTip(result);
    }

    /**
     * 查询所有规格商品对应的库存信息
     *
     * @param goodsId 商品ID
     * @return
     */
    @RequestMapping(value = "/getGoodsSkuNumMap")
    public Object getGoodsSkuNumMap(Long goodsId) {
        Map<String, Object> result = goodsService.getGoodsSkuNumMap(goodsId);
        return new SuccessDataTip(result);
    }

    /**
     * 获取规格商品库存数量集合
     *
     * @param goodsId 商品ID
     * @param specs 选中后的规格对象，如：{"尺寸":"2寸"}
     * @return
     */
    @RequestMapping(value = "/getGoodsSkuStockList")
    public Object getGoodsSkuStockList(Long goodsId, String specs) {
        List<Map<String, Object>> result = goodsService.getGoodsSkuStockList(goodsId, specs);
        return new SuccessDataTip(result);
    }
}
