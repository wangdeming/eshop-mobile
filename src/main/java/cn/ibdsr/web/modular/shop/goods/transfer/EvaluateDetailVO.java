package cn.ibdsr.web.modular.shop.goods.transfer;

import java.util.List;
import java.util.Map;

/**
 * @Description 订单评价详情VO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-18 09:31:22    XuZhipeng               类说明
 *
 */
public class EvaluateDetailVO {

    /**
     * 评价商品信息集合
     */
    private List<Map<String, Object>> evalGoodsList;

    /**
     * 服务评分
     */
    private Integer serviceScore;

    /**
     * 物流评分
     */
    private Integer expressScore;

    public List<Map<String, Object>> getEvalGoodsList() {
        return evalGoodsList;
    }

    public void setEvalGoodsList(List<Map<String, Object>> evalGoodsList) {
        this.evalGoodsList = evalGoodsList;
    }

    public Integer getServiceScore() {
        return serviceScore;
    }

    public void setServiceScore(Integer serviceScore) {
        this.serviceScore = serviceScore;
    }

    public Integer getExpressScore() {
        return expressScore;
    }

    public void setExpressScore(Integer expressScore) {
        this.expressScore = expressScore;
    }
}
