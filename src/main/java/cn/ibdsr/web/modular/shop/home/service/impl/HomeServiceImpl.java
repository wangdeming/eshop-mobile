package cn.ibdsr.web.modular.shop.home.service.impl;

import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.persistence.dao.GoodsCategoryMapper;
import cn.ibdsr.web.common.persistence.model.SearchHistory;
import cn.ibdsr.web.core.util.AmountFormatUtil;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.shop.home.dao.HomeDao;
import cn.ibdsr.web.modular.shop.home.service.IHomeService;
import cn.ibdsr.web.modular.shop.home.transfer.AdListVO;
import cn.ibdsr.web.modular.shop.home.transfer.GoodsCategoryVO;
import cn.ibdsr.web.modular.shop.home.transfer.GoodsListVO;
import cn.ibdsr.web.modular.shop.home.transfer.SearchHistoryDTO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description 首页Service
 * @Version V1.0
 * @CreateDate 2019/3/22 9:20
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/3/22      ZhuJingrui            类说明
 */
@Service
public class HomeServiceImpl implements IHomeService {

    @Autowired
    private HomeDao homeDao;

    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    /**
     * @Description 查找总销量前三的二级类目
     * @return 总销量前三的二级类目GoodsCategoryVO列表
     */
    @Override
    public List<GoodsCategoryVO> listSecondCategory() {
        List<GoodsCategoryVO> goodsCategoryList = homeDao.listSecCategory();
        listGoodsCategoryWarpper(goodsCategoryList);
        return goodsCategoryList;
    }

    /**
     * @Description 商品搜索时的热门推荐，获取商品数量最多的二级类目，数量限制为5项
     * @return 二级类目列表
     */
    @Override
    public List<GoodsCategoryVO> hotRecommendInSearching(){
        List<GoodsCategoryVO> goodsCategoryList = homeDao.hotRecommendInSearching();
        listGoodsCategoryWarpper(goodsCategoryList);
        return goodsCategoryList;
    }

    /**
     * @Description 查找所有一级类目
     * @return 所有一级类目列表
     */
    @Override
    public List<GoodsCategoryVO> listFirstCategory() {
        return homeDao.listFirstCategory();
    }

    /**
     * @Description 根据pid查找一级类目下的所有二级类目
     * @param id 一级类目id
     * @return 二级类目列表
     */
    @Override
    public List<GoodsCategoryVO> getSecCategoryById(Long id) {
        List<GoodsCategoryVO> secGoodsCategoryList = homeDao.getSecCategoryById(id);
        listGoodsCategoryWarpper(secGoodsCategoryList);
        return secGoodsCategoryList;
    }

    /**
     * 包装类目列表数据信息（图片路径）
     *
     * @param categoryList 类目列表
     */
    private void listGoodsCategoryWarpper(List<GoodsCategoryVO> categoryList) {
        for (GoodsCategoryVO goodsCategoryVO : categoryList) {
            // 补全图片路径
            goodsCategoryVO.setIconImg(ImageUtil.setImageURL(goodsCategoryVO.getIconImg()));
        }
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
            goodsVO.setReferPrice(AmountFormatUtil.amountFormat(goodsVO.getReferPrice()));
        }
    }

    /**
     * @Description 在二级类目下搜索商品
     * @param page 分页
     * @param keyword 搜索关键词
     * @param secCategoryId 二级类目id
     * @return 商品列表
     */
    @Override
    public List<GoodsListVO> queryGoodsInSecCategory(Page<GoodsListVO> page, String keyword, Long secCategoryId) {
        List<GoodsListVO> goodsList = homeDao.queryGoodsInSecCategory(page, keyword, secCategoryId, page.getOrderByField(), page.isAsc());
        goodsListWarpper(goodsList);
        return goodsList;
    }

    /**
     * @Description 首页推荐商品
     * @return 商品列表
     */
    @Override
    public List<GoodsListVO> listRecommendGoods() {
        List<GoodsListVO> goodsList = homeDao.listRecommendGoods();
        goodsListWarpper(goodsList);
        return goodsList;
    }

    /**
     * @Description 搜索商品
     * @param page 分页
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    @Override
    public List<GoodsListVO> queryGoods(Page<GoodsListVO> page, String keyword) {
        List<GoodsListVO> goodsList = homeDao.queryGoods(page, keyword, page.getOrderByField(), page.isAsc());
        goodsListWarpper(goodsList);
        return goodsList;
    }

    /**
     * @Description 插入查询记录
     * @param keyword 搜索关键词
     * @return
     */
    @Override
    public void insertSearchHistory(String keyword) {
        Long userId = ConstantFactory.me().getUserId();
        Date now = new Date();

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setCreatedTime(now);
        searchHistory.setType(1); // 搜索类型（1-商品；2-店铺；）
        searchHistory.setKeyword(keyword);
        searchHistory.setCreatedUser(userId);
        searchHistory.setUserId(userId);
        searchHistory.setIsDeleted(0);
        searchHistory.insert();
    }

    /**
     * @Description 查询搜索历史记录
     * @return 搜索历史记录列表
     */
    public List<SearchHistoryDTO> searchHistoryList(){
        Long userId = ConstantFactory.me().getUserId();
        return homeDao.searchHistoryList(userId);
    }

    /**
     * @Description 广告位查询
     * @param adPosition 广告位位置的英文缩写（homePageAd-平台首页轮播广告；recommendShopAd-推荐店铺广告；
     *                   shopGoodsAd-特产首页轮播广告）
     * @return
     */
    @Override
    public List<AdListVO> adManager(String adPosition) {
        List<AdListVO> adList = homeDao.adManager(adPosition);
        for (AdListVO adListVO : adList){
            adListVO.setImg(ImageUtil.setImageURL(adListVO.getImg()));
        }
        return adList;
    }
}
