package cn.ibdsr.web.modular.shop.goods.dao;

import cn.ibdsr.web.common.persistence.model.GoodsEvaluate;
import cn.ibdsr.web.modular.shop.goods.transfer.EvaluateListVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 商品评价Dao
 * @Version V1.0
 * @CreateDate 2019-03-18 16:16:47
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 16:16:47    XuZhipeng               类说明
 *
 */
public interface GoodsEvaluateDao {

    /**
     * 查询有图的评价数量
     *
     * @param goodsId 商品ID
     * @return
     */
    Integer queryHasImgNum(@Param("goodsId") Long goodsId);

    /**
     * 查询好评的评价数量
     *
     * @param goodsId 商品ID
     * @return
     */
    List<Map<String, Object>> queryEvalNumGroupByType(@Param("goodsId") Long goodsId);

    /**
     * 分页查询评价列表
     *
     * @param page 分页信息
     * @param goodsId 商品ID
     * @param evalType 评价类型（1-好评；2-中评；3-差评；）
     * @param isHasImg 是否有图（1-是；）
     * @return
     */
    List<EvaluateListVO> listEvaluates(@Param("page") Page<EvaluateListVO> page,
                                       @Param("goodsId") Long goodsId,
                                       @Param("evalType") Integer evalType,
                                       @Param("isHasImg") Integer isHasImg);


    /**
     * 批量新增订单商品评价信息
     *
     * @param evaluateList 评价信息集合
     * @return
     */
    Integer batchInsertEvaluates(List<GoodsEvaluate> evaluateList);

    /**
     * 根据订单ID查询评价商品集合
     *
     * @param orderId 订单ID
     * @return
     */
    List<Map<String, Object>> listEvalGoods(@Param("orderId") Long orderId);
}
