package cn.ibdsr.web.modular.shop.goods.transfer;

/**
 * @Description 商品详情 评价信息VO
 * @Version V1.0
 * @CreateDate 2019-03-18 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-18 09:31:22    XuZhipeng               类说明
 *
 */
public class GoodsEvaluateVO {

    /**
     * 评价数
     */
    private Integer evalNum;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 星级评分
     */
    private Integer score;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 评价图片集合字符串
     */
    private String evalImgs;

    public Integer getEvalNum() {
        return evalNum;
    }

    public void setEvalNum(Integer evalNum) {
        this.evalNum = evalNum;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getEvalImgs() {
        return evalImgs;
    }

    public void setEvalImgs(String evalImgs) {
        this.evalImgs = evalImgs;
    }
}
