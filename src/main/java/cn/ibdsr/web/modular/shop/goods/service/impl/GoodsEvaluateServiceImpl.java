package cn.ibdsr.web.modular.shop.goods.service.impl;

import cn.ibdsr.core.check.StaticCheck;
import cn.ibdsr.core.util.DateUtil;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.EvaluateType;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.constant.state.IsEvaluated;
import cn.ibdsr.web.common.constant.state.ShopOrderStatus;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.GoodsEvaluateMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderGoodsMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderMapper;
import cn.ibdsr.web.common.persistence.model.GoodsEvaluate;
import cn.ibdsr.web.common.persistence.model.ShopOrder;
import cn.ibdsr.web.common.persistence.model.ShopOrderGoods;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.shop.goods.dao.GoodsEvaluateDao;
import cn.ibdsr.web.modular.shop.goods.service.IGoodsEvaluateService;
import cn.ibdsr.web.modular.shop.goods.service.IGoodsService;
import cn.ibdsr.web.modular.shop.goods.transfer.*;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
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
@Service
public class GoodsEvaluateServiceImpl implements IGoodsEvaluateService {

    @Autowired
    private GoodsEvaluateDao goodsEvaluateDao;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private GoodsEvaluateMapper goodsEvaluateMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopOrderGoodsMapper shopOrderGoodsMapper;

    /**
     * 查询评价数量
     *
     * @param goodsId 商品ID
     * @return
     */
    @Override
    public EvaluateNumVO queryEvalNum(Long goodsId) {
        // 校验商品ID
        goodsService.checkGoodsId(goodsId);

        EvaluateNumVO evaluateNum = new EvaluateNumVO();

        // 查询总评价数
        Integer totalNum = goodsEvaluateMapper.selectCount(
                new EntityWrapper<GoodsEvaluate>()
                        .eq("goods_id", goodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode()));
        evaluateNum.setTotalNum(totalNum);                                      // 总评价数

        evaluateNum.setHasImgNum(goodsEvaluateDao.queryHasImgNum(goodsId));     // 有图评价数

        // 根据类型分组查询评价数（好评/差评）
        List<Map<String, Object>> list = goodsEvaluateDao.queryEvalNumGroupByType(goodsId);
        for (Map<String, Object> map : list) {
            Integer evalType = Integer.valueOf(map.get("type").toString());
            Integer num = Integer.valueOf(map.get("num").toString());
            if (EvaluateType.GOOD.getCode() == evalType) {
                evaluateNum.setGoodNum(num);                                    // 好评评价数
            }
            if (EvaluateType.BAD.getCode() == evalType) {
                evaluateNum.setBadNum(num);                                     // 差评评价数
            }
        }
        return evaluateNum;
    }

    /**
     * 根据商品ID分页获取商品评价列表
     *
     * @param page 分页信息
     * @param goodsId 商品ID
     * @param evalType 评价类型（1-好评；2-中评；3-差评；）
     * @param isHasImg 是否有图（1-是；）
     * @return
     */
    @Override
    public List<EvaluateListVO> listEvaluates(Page<EvaluateListVO> page, Long goodsId, Integer evalType, Integer isHasImg) {
        // 校验商品ID
        goodsService.checkGoodsId(goodsId);

        List<EvaluateListVO> evaluateList = goodsEvaluateDao.listEvaluates(page, goodsId, evalType, isHasImg);
        // 包装评价列表数据信息
        evaluateListWarpper(evaluateList);
        return evaluateList;
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
     * @return
     */
    @Transactional
    @Override
    public void evaluate(EvaluateDTO evaluateDTO) {
        // 校验DTO
        StaticCheck.check(evaluateDTO);

        // 判断该订单是否为待评价状态
        ShopOrder order = checkOrderId(evaluateDTO.getOrderId());
        if (ShopOrderStatus.RECEIVED.getCode() != order.getStatus() || IsEvaluated.NO.getCode() != order.getIsEvaluated().intValue()) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_CANNOT_EVALUATE);
        }

        // JSON字符串转list
        List<EvalOrderGoodsDTO> goodsEvalList = JSONObject.parseArray(evaluateDTO.getEvalOrderGoodsJsonStr(), EvalOrderGoodsDTO.class);
        if (goodsEvalList == null || goodsEvalList.size() == 0) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_EVALUATE_INFO_IS_NULL);
        }

        // 当前登录用户ID
        Long userId = ConstantFactory.me().getUserId();

        List<GoodsEvaluate> evaluateList = new ArrayList<>();
        GoodsEvaluate goodsEval;
        for (EvalOrderGoodsDTO evalDTO : goodsEvalList) {
            StaticCheck.check(evalDTO);

            // 校验orderGoodsId，并获取订单商品对象信息
            ShopOrderGoods orderGoods = checkOrderGoodsId(evalDTO.getOrderGoodsId());

            goodsEval = new GoodsEvaluate();
            goodsEval.setOrderGoodsId(evalDTO.getOrderGoodsId());                   // 订单商品ID
            goodsEval.setOrderId(order.getId());                                    // 订单ID
            goodsEval.setGoodsId(orderGoods.getGoodsId());                          // 商品ID
            goodsEval.setUserId(userId);                                            // 用户（评价人）ID
            goodsEval.setShopId(order.getShopId());                                 // 店铺ID
            goodsEval.setType(getEvalTypeByGoodsScore(evalDTO.getGoodsScore()));    // 评价类型（1-好评；2-中评；3-差评；）
            goodsEval.setContent(evalDTO.getContent());                             // 评价内容
            goodsEval.setImgs(ImageUtil.cutImageListURL2Str(evalDTO.getImgList())); // 评价图片
            goodsEval.setGoodsScore(evalDTO.getGoodsScore());                       // 商品评分
            goodsEval.setServiceScore(evaluateDTO.getServiceScore());               // 服务评分
            goodsEval.setExpressScore(evaluateDTO.getExpressScore());               // 物流评分
            goodsEval.setCreatedUser(userId);
            goodsEval.setCreatedTime(new Date());
            goodsEval.setIsDeleted(IsDeleted.NORMAL.getCode());
            evaluateList.add(goodsEval);
        }

        if (evaluateList == null || evaluateList.size() == 0) {
            throw new BussinessException(BizExceptionEnum.EVALUATE_FAIL);
        }
        // 批量新增评价信息
        goodsEvaluateDao.batchInsertEvaluates(evaluateList);

        // 修改订单为已评价
        order.setIsEvaluated(IsEvaluated.YES.getCode());
        order.setModifiedUser(userId);
        order.setModifiedTime(new Date());
        order.updateById();
    }

    /**
     * 获取订单评价页面订单商品信息
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    public List<Map<String, Object>> listOrderGoods4Eval(Long orderId) {
        // 校验orderId，并获取订单对象信息
        ShopOrder order = checkOrderId(orderId);
        // 判断该订单是否为待评价状态
        if (IsEvaluated.NO.getCode() != order.getIsEvaluated().intValue()) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_CANNOT_EVALUATE);
        }

        // 根据订单ID查询订单商品
        List<Map<String, Object>> orderGoodsList = shopOrderGoodsMapper.selectMaps(
                new EntityWrapper<ShopOrderGoods>()
                        .setSqlSelect("id AS orderGoodsId, goods_name AS goodsName, goods_img AS goodsImg, goods_specs AS goodsSpecs")
                        .eq("order_id", orderId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode()));
        for (Map<String, Object> map : orderGoodsList) {
            // 拼接图片前缀
            map.put("goodsImg", ImageUtil.setImageURL(String.valueOf(map.get("goodsImg"))));
        }
        return orderGoodsList;
    }

    /**
     * 查询订单评价详情
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    public EvaluateDetailVO detailEvaluate(Long orderId) {
        // 校验orderId
        checkOrderId(orderId);

        // 根据订单ID查询商品评价信息
        List<Map<String, Object>> evalGoodsList = goodsEvaluateDao.listEvalGoods(orderId);
        if (null == evalGoodsList || 0 == evalGoodsList.size()) {
            return null;
        }

        int serviceScore = Integer.valueOf(evalGoodsList.get(0).get("serviceScore").toString());
        int expressScore = Integer.valueOf(evalGoodsList.get(0).get("expressScore").toString());
        for (Map<String, Object> map : evalGoodsList) {
            map.put("goodsImg", ImageUtil.setImageURL(map.get("goodsImg").toString()));
            map.put("imgs", ImageUtil.setImageStrURL2List(map.get("imgs").toString()));
            map.remove("serviceScore");
            map.remove("expressScore");
        }
        EvaluateDetailVO evalDetailVO = new EvaluateDetailVO();
        evalDetailVO.setServiceScore(serviceScore);
        evalDetailVO.setExpressScore(expressScore);
        evalDetailVO.setEvalGoodsList(evalGoodsList);
        return evalDetailVO;
    }

    /**
     * 包装评价列表数据信息
     *
     * @param evaluateList 评价集合
     */
    private void evaluateListWarpper(List<EvaluateListVO> evaluateList) {
        for (EvaluateListVO eval : evaluateList) {
            // 拼接用户头像图片路径
            eval.setUserAvatar(ImageUtil.setImageURL(eval.getUserAvatar()));

            // 拼接评价图片路径
            eval.setEvalImgs(ImageUtil.setImageStrURL(eval.getEvalImgs()));

            // 时间截取年月日
            Date date = DateUtil.parse(eval.getCreatedTime(), "yyyy-MM-dd HH:mm:ss.SSS");
            eval.setCreatedTime(DateUtil.format(date, "yyyy-MM-dd"));
        }
    }

    /**
     * 校验订单ID
     *
     * @param orderId 订单ID
     * @return
     */
    private ShopOrder checkOrderId(Long orderId) {
        if (ToolUtil.isEmpty(orderId)) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ID_IS_NULL);
        }
        ShopOrder order = shopOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }
        return order;
    }

    /**
     * 校验订单商品ID
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    private ShopOrderGoods checkOrderGoodsId(Long orderGoodsId) {
        if (ToolUtil.isEmpty(orderGoodsId)) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_ID_IS_NULL);
        }
        ShopOrderGoods orderGoods = shopOrderGoodsMapper.selectById(orderGoodsId);
        if (orderGoods == null) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_IS_NOT_EXIST);
        }
        return orderGoods;
    }

    /**
     * 根据商品评分获取评价类型（好评/中评/差评）
     *
     * @param goodsScore 商品评分
     * @return
     */
    private Integer getEvalTypeByGoodsScore(Integer goodsScore) {
        if (goodsScore.intValue() == 5) {
            return EvaluateType.GOOD.getCode();
        } else if (goodsScore.intValue() <= 2) {
            return EvaluateType.BAD.getCode();
        } else {
            return EvaluateType.NORMAL.getCode();
        }
    }
}
