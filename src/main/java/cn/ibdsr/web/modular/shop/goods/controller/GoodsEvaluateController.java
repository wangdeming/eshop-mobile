package cn.ibdsr.web.modular.shop.goods.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.constant.factory.PageFactory;
import cn.ibdsr.web.modular.shop.goods.service.IGoodsEvaluateService;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateDTO;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateDetailVO;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateListVO;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateNumVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description 商品评价控制器
 * @Version V1.0
 * @CreateDate 2019-03-15 16:16:47
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 16:16:47    XuZhipeng               类说明
 *
 */
@RestController
@RequestMapping("/shop/goodsEvaluate")
public class GoodsEvaluateController extends BaseController {

    @Autowired
    private IGoodsEvaluateService goodsEvaluateService;

    /**
     * 查询评价数量
     *
     * @param goodsId 商品ID
     * @return
     */
    @RequestMapping(value = "/queryEvalNum")
    public Object queryEvalNum(Long goodsId) {
        EvaluateNumVO evalNum = goodsEvaluateService.queryEvalNum(goodsId);
        return new SuccessDataTip(evalNum);
    }

    /**
     * 根据商品ID分页获取商品评价列表
     *
     * @param goodsId 商品ID
     * @param evalType 评价类型（1-好评；2-中评；3-差评；）
     * @param isHasImg 是否有图（1-有；0-无；）
     * @return
     */
    @RequestMapping(value = "/listEvaluates")
    public Object listEvaluates(Long goodsId, Integer evalType, Integer isHasImg) {
        Page<EvaluateListVO> page = new PageFactory<EvaluateListVO>().defaultPage();
        List<EvaluateListVO> result = goodsEvaluateService.listEvaluates(page, goodsId, evalType, isHasImg);
        page.setRecords(result);
        return new SuccessDataTip(super.packForBT(page));
    }

    /**
     * 获取订单评价页面订单商品信息
     *
     * @param orderId 订单ID
     * @return
     */
    @WinXinLogin
    @RequestMapping(value = "/listOrderGoods4Eval")
    public Object listOrderGoods4Eval(Long orderId) {
        List<Map<String, Object>> result = goodsEvaluateService.listOrderGoods4Eval(orderId);
        return new SuccessDataTip(result);
    }

    /**
     * 评价商品
     *
     * @param evaluateDTO 评价信息
     *                    goodsId 商品ID
     *                    skuId 规格商品ID
     *                    goodsScore 商品评分
     *                    serviceScore 服务评分
     *                    expressScore 物流评分
     *                    content 评价内容
     *                    imgList 评价图片
     *
     * @return
     */
    @WinXinLogin
    @RequestMapping(value = "/evaluate")
    public Object evaluate(EvaluateDTO evaluateDTO) {
        goodsEvaluateService.evaluate(evaluateDTO);
        return SUCCESS_TIP;
    }

    /**
     * 查询订单评价详情
     *
     * @param orderId 订单ID
     * @return
     */
    @WinXinLogin
    @RequestMapping(value = "/detailEvaluate")
    public Object detailEvaluate(Long orderId) {
        EvaluateDetailVO evalDetail = goodsEvaluateService.detailEvaluate(orderId);
        return new SuccessDataTip(evalDetail);
    }

}
