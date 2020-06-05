package cn.ibdsr.web.modular.shop.goods.transfer;

/**
 * @Description 商品详情 评价数量VO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-18 09:31:22    XuZhipeng               类说明
 *
 */
public class EvaluateNumVO {

    /**
     * 总评价数
     */
    private Integer totalNum;

    /**
     * 有图数
     */
    private Integer hasImgNum;

    /**
     * 好评数
     */
    private Integer goodNum = 0;

    /**
     * 差评评数
     */
    private Integer badNum = 0;

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getHasImgNum() {
        return hasImgNum;
    }

    public void setHasImgNum(Integer hasImgNum) {
        this.hasImgNum = hasImgNum;
    }

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public Integer getBadNum() {
        return badNum;
    }

    public void setBadNum(Integer badNum) {
        this.badNum = badNum;
    }
}
