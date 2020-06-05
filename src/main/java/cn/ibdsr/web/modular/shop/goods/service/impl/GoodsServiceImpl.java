package cn.ibdsr.web.modular.shop.goods.service.impl;

import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.constant.state.DeliveryType;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.*;
import cn.ibdsr.web.common.persistence.model.*;
import cn.ibdsr.web.core.util.AmountFormatUtil;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.shop.goods.dao.GoodsDao;
import cn.ibdsr.web.modular.shop.goods.service.IGoodsService;
import cn.ibdsr.web.modular.shop.goods.transfer.*;
import cn.ibdsr.web.modular.shop.record.service.RecordService;
import cn.ibdsr.web.modular.shop.shop.service.IShopService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 商品信息Service
 * @Version V1.0
 * @CreateDate 2019-03-15 16:16:47
 * <p>
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 16:16:47    XuZhipeng               类说明
 */
@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private IShopService shopService;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private ViewStatsMapper viewStatsMapper;

    @Autowired
    private GoodsIntroMapper goodsIntroMapper;

    @Autowired
    private GoodsEvaluateMapper goodsEvaluateMapper;

    @Autowired
    private GoodsImgMapper goodsImgMapper;

    @Autowired
    private GoodsSkuMapper goodsSkuMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private RecordService recordService;

    /**
     * 店铺主页 查询最新商品
     *
     * @param shopId 店铺ID
     * @return
     */
    @Override
    public List<GoodsListVO> listShopLatestGoods(Long shopId) {
        // 校验shopId
        shopService.checkShopId(shopId);

        List<GoodsListVO> goodsList = goodsDao.listShopLatestGoods(shopId);
        // 包装商品列表数据信息
        goodsListWarpper(goodsList);
        return goodsList;
    }

    /**
     * 店铺主页 根据店铺ID分页获取商品列表
     *
     * @param page   分页信息
     * @param shopId 店铺ID
     * @return
     */
    @Override
    public List<GoodsListVO> listShopGoods(Page<GoodsListVO> page, Long shopId) {
        // 校验shopId
        shopService.checkShopId(shopId);

        List<GoodsListVO> goodsList = goodsDao.listShopGoods(page, shopId);
        // 包装商品列表数据信息
        goodsListWarpper(goodsList);
        return goodsList;
    }

    /**
     * 查询商品详情
     *
     * @param goodsId 商品ID
     * @return
     */
    @Override
    public GoodsDetailVO detailGoods(Long goodsId) {
        // 校验商品ID
        Goods goods = checkGoodsId(goodsId);

        GoodsDetailVO goodsDetail = new GoodsDetailVO();
        goodsDetail.setGoodsId(goods.getId());                                              // 商品ID
        goodsDetail.setGoodsName(goods.getName());                                          // 商品名称
        goodsDetail.setGoodsMainImg(ImageUtil.setImageURL(goods.getMainImg()));             // 商品主图（选择规格时用）

        // 运费
        String expressFee = BigDecimal.ZERO.compareTo(goods.getExpressFee()) == 0
                ? "免运费" : AmountFormatUtil.amountFormat(goods.getExpressFee());
        goodsDetail.setExpressFee(expressFee);                                              // 运费

        goodsDetail.setStock(goods.getStock());                                             // 库存数量
        goodsDetail.setSaleNum(getGoodsSaleNum(goods.getId()));                             // 销量
        goodsDetail.setDeliveryType(DeliveryType.valueOf(goods.getDeliveryType()));         // 配送方式
        goodsDetail.setSpecsList(goods.getSpecsList());                                     // 商品规格

        goodsDetail.setGoodsIntro(queryGoodsIntro(goods.getId()));                          // 商品介绍详情

        // 设置商品图片集合
        goodsDetail.setGoodsImgList(queryGoodsImgList(goods.getId()));

        // 设置商品售价区间和划线价区间
        goodsDetail.setPrice(AmountFormatUtil.amountFormat(goods.getPrice()));              // 最低售价
        goodsDetail.setReferPrice(AmountFormatUtil.amountFormat(goods.getReferPrice()));    // 最低划线价
        setGoodsPriceRange(goodsDetail);

        // 查询店铺信息
        goodsDetail.setShop(getShopInfo(goods.getShopId()));

        try {
            //增加访问记录和统计访问数据
            recordService.viewGoods(goodsId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return goodsDetail;
    }

    /**
     * 查询规格商品信息
     *
     * @param goodsId 商品ID
     * @param specs   规格信息（格式为json字符串，例：{"包装":"1KG","口味":"香辣"}）
     * @return
     */
    @Override
    public GoodsSkuVO getGoodsSku(Long goodsId, String specs) {
        // 校验商品ID
        Goods goods = checkGoodsId(goodsId);
        // 校验商品规格是否为空
        if (ToolUtil.isEmpty(specs)) {
            throw new BussinessException(BizExceptionEnum.GOODS_SPECS_IS_NULL);
        }

        // 查询规格商品
        List<GoodsSku> skuList = goodsSkuMapper.selectList(
                new EntityWrapper<GoodsSku>()
                        .setSqlSelect("id, price, refer_price, stock, img")
                        .eq("goods_id", goodsId)
                        .eq("specs", specs)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .last("LIMIT 1"));
        if (null == skuList || 0 == skuList.size()) {
            throw new BussinessException(BizExceptionEnum.GOODS_SKU_IS_NOT_EXIST);
        }

        GoodsSku sku = skuList.get(0);
        GoodsSkuVO goodsSkuVO = new GoodsSkuVO();
        goodsSkuVO.setSkuId(sku.getId());                                               // 规格商品ID

        // 规格商品图片为空，则显示商品主图片
        String img = sku.getImg() == null ? goods.getMainImg() : sku.getImg();
        goodsSkuVO.setImg(ImageUtil.setImageURL(img));                                  // 规格商品图片

        goodsSkuVO.setPrice(AmountFormatUtil.amountFormat(sku.getPrice()));             // 售价
        goodsSkuVO.setReferPrice(AmountFormatUtil.amountFormat(sku.getReferPrice()));   // 划线价
        goodsSkuVO.setStock(sku.getStock());                                            // 库存数量
        return goodsSkuVO;
    }

    /**
     * 校验goodsId是否存在
     *
     * @param goodsId 商品ID
     * @return
     */
    @Override
    public Goods checkGoodsId(Long goodsId) {
        if (ToolUtil.isEmpty(goodsId)) {
            throw new BussinessException(BizExceptionEnum.GOODS_ID_IS_NULL);
        }

        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BussinessException(BizExceptionEnum.GOODS_IS_NOT_EXIST);
        }
        return goods;
    }

    /**
     * 查询所有规格商品对应的库存信息
     *
     * @param goodsId   商品ID
     * @return
     */
    @Override
    public Map<String, Object> getGoodsSkuNumMap(Long goodsId) {
        if (null == goodsId) {
            throw new BussinessException(BizExceptionEnum.GOODS_ID_IS_NULL);
        }
        Goods goods = checkGoodsId(goodsId);
        String specsList = goods.getSpecsList();

        Map<String, Object> resultMap = new HashMap<String, Object>();

        JSONObject jsonObject = JSONObject.parseObject(specsList);
        JSONObject specsObj;
        for (String key : jsonObject.keySet()) {
            List<String> specsValList = (List<String>) jsonObject.get(key);
            for (String specsVal : specsValList) {
                specsObj = new JSONObject();
                specsObj.put(key, specsVal);

                resultMap.put(specsObj.toString(), getGoodsSkuStock(goodsId, specsObj.toString()));
            }
        }
        return resultMap;
    }

    /**
     * 获取规格商品库存数量集合
     *
     * @param goodsId 商品ID
     * @param specs 规格参数
     * @return
     */
    @Override
    public List<Map<String, Object>> getGoodsSkuStockList(Long goodsId, String specs) {
        if (null == goodsId) {
            throw new BussinessException(BizExceptionEnum.GOODS_ID_IS_NULL);
        }
        // 校验商品规格是否为空
        if (ToolUtil.isEmpty(specs)) {
            throw new BussinessException(BizExceptionEnum.GOODS_SPECS_IS_NULL);
        }

        // 把规格对象前后{}去掉
        specs = specs.substring(1, specs.length() - 1);

        String[] split = specs.split(",");
        StringBuilder whereSql = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (i != 0) {
                whereSql.append("AND");
            }
            whereSql.append("specs LIKE '%" + split[i] + "%'");
        }

        // 查询规格商品
        List<Map<String, Object>> skuList = goodsSkuMapper.selectMaps(
                new EntityWrapper<GoodsSku>()
                        .setSqlSelect("specs, stock")
                        .eq("goods_id", goodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .where(whereSql.toString()));
        if (null == skuList || 0 == skuList.size()) {
            return null;
        }
        return skuList;
    }

    /**
     * 获取规格商品库存数量
     *
     * @param goodsId 商品ID
     * @param specs   规格参数
     * @return
     */
    private Integer getGoodsSkuStock(Long goodsId, String specs) {
        // 把规格对象前后{}去掉
        specs = specs.substring(1, specs.length() - 1);

        // 查询规格商品
        List<GoodsSku> skuList = goodsSkuMapper.selectList(
                new EntityWrapper<GoodsSku>()
                        .setSqlSelect("SUM(stock) AS stock")
                        .eq("goods_id", goodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .where("specs LIKE '%" + specs + "%'"));
        if (null == skuList || 0 == skuList.size()) {
            return 0;
        }
        return skuList.get(0).getStock();
    }

    /**
     * 包装商品列表数据信息（图片路径、价格、划线价）
     *
     * @param goodsList 商品列表
     */
    private void goodsListWarpper(List<GoodsListVO> goodsList) {
        for (GoodsListVO goodsVO : goodsList) {
            // 补全图片路径
            goodsVO.setImg(ImageUtil.setImageURL(goodsVO.getImg()));

            // 售价
            goodsVO.setPrice(AmountFormatUtil.amountFormat(goodsVO.getPrice()));

            // 划线价
            if (ToolUtil.isEmpty(goodsVO.getReferPrice()) || "0".equals(goodsVO.getReferPrice())) {
                goodsVO.setReferPrice("");
            } else {
                goodsVO.setReferPrice(AmountFormatUtil.amountFormat(goodsVO.getReferPrice()));
            }
        }
    }

    /**
     * 获取商品销量
     *
     * @param goodsId 商品ID
     * @return
     */
    private Integer getGoodsSaleNum(Long goodsId) {
        List<ViewStats> goodsStatsList = viewStatsMapper.selectList(
                new EntityWrapper<ViewStats>()
                        .setSqlSelect("sale_num")
                        .eq("goods_id", goodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .last("LIMIT 1"));
        if (null == goodsStatsList || 0 == goodsStatsList.size()) {
            return 0;
        }
        return goodsStatsList.get(0).getSaleNum();
    }

    /**
     * 查询商品介绍详情
     *
     * @param goodsId 商品ID
     * @return
     */
    private String queryGoodsIntro(Long goodsId) {
        List<GoodsIntro> goodsIntroList = goodsIntroMapper.selectList(
                new EntityWrapper<GoodsIntro>()
                        .setSqlSelect("intro_content")
                        .eq("goods_id", goodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .orderBy("created_time", false)
                        .last("LIMIT 1"));
        if (null == goodsIntroList || 0 == goodsIntroList.size()) {
            return "";
        }
        String content = goodsIntroList.get(0).getIntroContent();
        content = content
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&#39;", "'");
        return content;
    }

    /**
     * 查询商品图片集合
     *
     * @param goodsId 商品ID
     * @return
     */
    private List<String> queryGoodsImgList(Long goodsId) {
        List<String> goodsImgList = new ArrayList<String>();

        List<GoodsImg> goodsImgs = goodsImgMapper.selectList(
                new EntityWrapper<GoodsImg>()
                        .setSqlSelect("img")
                        .eq("goods_id", goodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .orderBy("sequence", true));
        for (GoodsImg img : goodsImgs) {
            goodsImgList.add(ImageUtil.setImageURL(img.getImg()));
        }
        return goodsImgList;
    }

    /**
     * 设置价格范围
     *
     * @param goodsDetail 商品详情
     */
    private void setGoodsPriceRange(GoodsDetailVO goodsDetail) {
        // 查询商品最高售价
        BigDecimal maxPrice = goodsDao.getMaxPrice(goodsDetail.getGoodsId());
        if (null != maxPrice && maxPrice.compareTo(new BigDecimal(goodsDetail.getPrice()).multiply(BigDecimal.valueOf(100))) > 0) {
            goodsDetail.setPrice(goodsDetail.getPrice() + "-" + AmountFormatUtil.amountFormat(maxPrice));
        }

        // 查询商品最高划线价
        BigDecimal maxReferPrice = goodsDao.getMaxReferPrice(goodsDetail.getGoodsId());
        if (null == maxReferPrice || BigDecimal.ZERO.compareTo(maxReferPrice) == 0) {
            goodsDetail.setReferPrice("");
        } else {
            String max = AmountFormatUtil.amountFormat(maxReferPrice);
            if (!max.equals(goodsDetail.getReferPrice())) {
                goodsDetail.setReferPrice(goodsDetail.getReferPrice() + "-" + AmountFormatUtil.amountFormat(maxReferPrice));
            }
        }
    }

    /**
     * 获取店铺信息
     *
     * @param shopId 店铺ID
     * @return
     */
    private ShopVO getShopInfo(Long shopId) {
        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            return null;
        }

        ShopVO shopVO = new ShopVO();
        shopVO.setShopId(shopId);
        shopVO.setShopName(shop.getFrontName());
        shopVO.setShopLogo(ImageUtil.setImageURL(shop.getLogo()));
        return shopVO;
    }

    /**
     * 获取商品评价信息
     *
     * @param goodsId 商品ID
     * @return
     */
    private GoodsEvaluateVO getGoodsEvaluate(Long goodsId) {
        // 查询最近的一条5星好评
        GoodsEvaluateVO goodsEvaluate = goodsDao.getEvaluateOfGoodsDetail(goodsId);
        if (goodsEvaluate != null) {
            // 拼接用户头像路径
            goodsEvaluate.setUserAvatar(ImageUtil.setImageURL(goodsEvaluate.getUserAvatar()));

            // 拼接评价图片路径
            goodsEvaluate.setEvalImgs(ImageUtil.setImageStrURL(goodsEvaluate.getEvalImgs()));

            // 查询评价数
            Integer evaluateCount = goodsEvaluateMapper.selectCount(
                    new EntityWrapper<GoodsEvaluate>()
                            .eq("goods_id", goodsId)
                            .eq("is_deleted", IsDeleted.NORMAL.getCode()));
            goodsEvaluate.setEvalNum(evaluateCount);
        }
        return goodsEvaluate;
    }
}
