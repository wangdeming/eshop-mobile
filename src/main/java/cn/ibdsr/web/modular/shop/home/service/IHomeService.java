package cn.ibdsr.web.modular.shop.home.service;

import cn.ibdsr.web.modular.shop.home.transfer.AdListVO;
import cn.ibdsr.web.modular.shop.home.transfer.GoodsCategoryVO;
import cn.ibdsr.web.modular.shop.home.transfer.GoodsListVO;
import cn.ibdsr.web.modular.shop.home.transfer.SearchHistoryDTO;
import com.baomidou.mybatisplus.plugins.Page;

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
public interface IHomeService {

    /**
     * @Description 查找总销量前三的二级类目
     * @return 总销量前三的二级类目GoodsCategoryVO列表
     */
    List<GoodsCategoryVO> listSecondCategory();

    /**
     * @Description 商品搜索时的热门推荐，获取商品数量最多的二级类目，数量限制为5项
     * @return 二级类目列表
     */
    List<GoodsCategoryVO> hotRecommendInSearching();

    /**
     * @Description 查找所有一级类目
     * @return 所有一级类目列表
     */
    List<GoodsCategoryVO> listFirstCategory();

    /**
     * @Description 根据pid查找一级类目下的所有二级类目
     * @param id 一级类目id
     * @return 二级类目列表
     */
    List<GoodsCategoryVO> getSecCategoryById(Long id);

    /**
     * @Description 在二级类目下搜索商品
     * @param page 分页
     * @param keyword 搜索关键词
     * @param secCategoryId 二级类目id
     * @return 商品列表
     */
    List<GoodsListVO> queryGoodsInSecCategory(Page<GoodsListVO> page, String keyword, Long secCategoryId);

    /**
     * @Description 首页推荐商品
     * @return 商品列表
     */
    List<GoodsListVO> listRecommendGoods();

    /**
     * @Description 搜索商品
     * @param page 分页
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    List<GoodsListVO> queryGoods(Page<GoodsListVO> page, String keyword);

    /**
     * @Description 插入查询记录
     * @param keyword 搜索关键词
     * @return
     */
    void insertSearchHistory(String keyword);

    /**
     * @Description 查询搜索历史记录
     * @return 搜索历史记录列表
     */
    List<SearchHistoryDTO> searchHistoryList();

    /**
     * @Description 广告位查询
     * @param adPosition 广告位位置的英文缩写（homePageAd-平台首页轮播广告；recommendShopAd-推荐店铺广告；
     *                   shopGoodsAd-特产首页轮播广告）
     * @return
     */
    List<AdListVO> adManager(String adPosition);

}
