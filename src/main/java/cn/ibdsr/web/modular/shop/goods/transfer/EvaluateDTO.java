package cn.ibdsr.web.modular.shop.goods.transfer;

import cn.ibdsr.core.base.dto.BaseDTO;
import cn.ibdsr.core.check.Verfication;

/**
 * @Description 商品评价DTO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-19 15:31:22    XuZhipeng               类说明
 *
 */
public class EvaluateDTO extends BaseDTO {

    /**
     * 订单ID
     */
    @Verfication(name = "订单ID", notNull = true)
    private Long orderId;

    /**
     * 订单的单个商品评价信息JSON字符串
     */
    @Verfication(name = "订单的单个商品评价信息JSON字符串", notNull = true)
    private String evalOrderGoodsJsonStr;

    /**
     * 服务评分
     */
    @Verfication(name = "服务评分", notNull = true, min = 1, max = 5)
    private Integer serviceScore;

    /**
     * 物流评分
     */
    @Verfication(name = "物流评分", notNull = true, min = 1, max = 5)
    private Integer expressScore;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getEvalOrderGoodsJsonStr() {
        return evalOrderGoodsJsonStr;
    }

    public void setEvalOrderGoodsJsonStr(String evalOrderGoodsJsonStr) {
        this.evalOrderGoodsJsonStr = evalOrderGoodsJsonStr;
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
