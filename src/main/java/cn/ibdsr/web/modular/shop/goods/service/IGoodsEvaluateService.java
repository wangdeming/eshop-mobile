package cn.ibdsr.web.modular.shop.goods.service;

import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateDTO;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateDetailVO;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateListVO;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateNumVO;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;
import java.util.Map;

/**
 * @Description 商品评价Service
 * @Version V1.0
 * @CreateDate 2019-03-15 16:16:47
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 16:16:47    XuZhipeng               类说明
 *
 */
public interface IGoodsEvaluateService {

    /**
     * 查询评价数量
     *
     * @param goodsId 商品ID
     * @return
     */
    EvaluateNumVO queryEvalNum(Long goodsId);

    /**
     * 根据商品ID分页获取商品评价列表
     *
     * @param page 分页信息
     * @param goodsId 商品ID
     * @param evalType 评价类型（1-好评；2-中评；3-差评；）
     * @param isHasImg 是否有图（1-有；0-无；）
     * @return
     */
    List<EvaluateListVO> listEvaluates(Page<EvaluateListVO> page, Long goodsId, Integer evalType, Integer isHasImg);

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
    void evaluate(EvaluateDTO evaluateDTO);

    /**
     * 获取订单评价页面订单商品信息
     *
     * @param orderId 订单ID
     * @return
     */
    List<Map<String, Object>> listOrderGoods4Eval(Long orderId);

    /**
     * 查询订单评价详情
     *
     * @param orderId 订单ID
     * @return
     */
    EvaluateDetailVO detailEvaluate(Long orderId);
}
