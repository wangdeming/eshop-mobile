package cn.ibdsr.web.modular.shop.goods.transfer;

import cn.ibdsr.core.base.dto.BaseDTO;
import cn.ibdsr.core.check.Verfication;

import java.util.List;

/**
 * @Description 订单规格商品评价DTO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-19 15:31:22    XuZhipeng               类说明
 *
 */
public class EvalOrderGoodsDTO extends BaseDTO {

    /**
     * 订单商品ID
     */
    @Verfication(name = "订单商品ID", notNull = true)
    private Long orderGoodsId;

    /**
     * 商品评分
     */
    @Verfication(name = "商品评分", notNull = true, min = 1, max = 5)
    private Integer goodsScore;

    /**
     * 评价内容
     */
    @Verfication(name = "评价内容", maxlength = 200)
    private String content;

    /**
     * 评价图片
     */
    private List<String> imgList;

    public Long getOrderGoodsId() {
        return orderGoodsId;
    }

    public void setOrderGoodsId(Long orderGoodsId) {
        this.orderGoodsId = orderGoodsId;
    }

    public Integer getGoodsScore() {
        return goodsScore;
    }

    public void setGoodsScore(Integer goodsScore) {
        this.goodsScore = goodsScore;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
