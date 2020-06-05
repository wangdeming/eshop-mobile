package cn.ibdsr.web.modular.shop.home.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.constant.factory.PageFactory;
import cn.ibdsr.web.modular.shop.home.dao.HomeDao;
import cn.ibdsr.web.modular.shop.home.service.IHomeService;
import cn.ibdsr.web.modular.shop.home.transfer.AdListVO;
import cn.ibdsr.web.modular.shop.home.transfer.GoodsCategoryVO;
import cn.ibdsr.web.modular.shop.home.transfer.GoodsListVO;
import cn.ibdsr.web.modular.shop.home.transfer.SearchHistoryDTO;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.commons.lang3.StringUtils;
import org.beetl.ext.fn.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Description 首页控制器
 * @Version V1.0
 * @CreateDate 2019/3/22 9:20
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/3/22      ZhuJingrui            类说明
 */
@Controller
@RequestMapping("/shop/home")
public class HomeController extends BaseController {

    @Autowired
    private IHomeService homeService;

    @Autowired
    private HomeDao homeDao;

    private String PREFIX = "/home/home/";

    /**
     * 跳转到首页首页
     */
    @RequestMapping("")
    public String index() {
        return PREFIX + "home.html";
    }


    /**
     * @Description 查找总销量前三的二级类目
     * @return 总销量前三的二级类目列表
     */
    @RequestMapping(value = "/secGoodsCategory")
    @ResponseBody
    public Object secGoodsCategory(){
        List<GoodsCategoryVO> result = homeService.listSecondCategory();
        return new SuccessDataTip(result);
    }

    /**
     * @Description 商品搜索时的热门推荐，获取商品数量最多的二级类目，数量限制为5项
     * @return 二级类目列表
     */
    @RequestMapping(value = "/hotRecommendInSearching")
    @ResponseBody
    public Object hotRecommendInSearching(){
        List<GoodsCategoryVO> result = homeService.hotRecommendInSearching();
        return new SuccessDataTip(result);
    }

    /**
     * @Description 查找所有一级类目
     * @return 所有一级类目列表
     */
    @RequestMapping(value = "/listFirstCategory")
    @ResponseBody
    public Object listFirstCategory(){
        List<GoodsCategoryVO> result = homeService.listFirstCategory();
        return new SuccessDataTip(result);
    }

    /**
     * @Description 根据pid查找一级类目下的所有二级类目
     * @param id 一级类目id
     * @return 二级类目列表
     */
    @RequestMapping(value = "/getSecCategoryById")
    @ResponseBody
    public Object getSecCategoryById(@RequestParam Long id){
        List<GoodsCategoryVO> result = homeService.getSecCategoryById(id);
        return new SuccessDataTip(result);
    }


    /**
     * @Description 在二级类目下搜索商品
     * @param keyword 搜索关键词
     * @param secCategoryId 二级类目id
     * @return 商品列表
     */
    @RequestMapping(value = "/queryGoodsInSecCategory")
    @ResponseBody
    @WinXinLogin
    public Object queryGoodsInSecCategory(String keyword, @RequestParam Long secCategoryId){
        Page<GoodsListVO> page = new PageFactory<GoodsListVO>().defaultPage();
        List<GoodsListVO> result = homeService.queryGoodsInSecCategory(page, keyword, secCategoryId);
        if (keyword != null && keyword.length() != 0){
            homeService.insertSearchHistory(keyword);
        }
        return new SuccessDataTip(result);
    }

    /**
     * @Description 搜索商品
     * @param keyword 搜索关键词
     * @return 商品列表
     */
    @RequestMapping(value = "/queryGoods")
    @ResponseBody
    @WinXinLogin
    public Object queryGoods(@RequestParam String keyword){
        Page<GoodsListVO> page = new PageFactory<GoodsListVO>().defaultPage();
        List<GoodsListVO> result = homeService.queryGoods(page, keyword);
        if (ToolUtil.isNotEmpty(keyword)){
            homeService.insertSearchHistory(keyword);
        }
        return new SuccessDataTip(result);
    }


    /**
     * @Description 首页推荐商品
     * @return 商品列表
     */
    @RequestMapping(value = "/listRecommendGoods")
    @ResponseBody
    public Object listRecommendGoods() {
        List<GoodsListVO> result = homeService.listRecommendGoods();
        return new SuccessDataTip(result);
    }

    /**
     * @Description 查询搜索历史记录
     * @return 搜索历史记录列表
     */
    @RequestMapping(value = "/searchHistoryList")
    @ResponseBody
    public Object searchHistoryList(){
        List<SearchHistoryDTO> result = homeService.searchHistoryList();
        return new SuccessDataTip(result);
    }

    /**
     * @Description 删除搜索历史记录
     * @return
     */
    @RequestMapping(value = "/delSearchHistory")
    @ResponseBody
    public Object delSearchHistory(){
        homeDao.delSearchHistory();
        return SUCCESS_TIP;
    }

    /**
     * @Description 广告位查询
     * @param adPosition 广告位位置的英文缩写（homePageAd-平台首页轮播广告；recommendShopAd-推荐店铺广告；
     *                   shopGoodsAd-特产首页轮播广告）
     * @return
     */
    @RequestMapping(value = "/adManager")
    @ResponseBody
    public Object adManager(String adPosition){
        List<AdListVO> result = homeService.adManager(adPosition);
        return new SuccessDataTip(result);
    }

}
