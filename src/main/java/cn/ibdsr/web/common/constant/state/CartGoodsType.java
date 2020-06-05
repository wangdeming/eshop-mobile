package cn.ibdsr.web.common.constant.state;

/**
 * 购物车商品状态
 *
 * @author XuZhipeng
 * @Date 2019-03-22 09:54:13
 */
public enum CartGoodsType {

    NORMAL(1, "正常"), EXPIRE(2, "失效");

    int code;
    String message;

    CartGoodsType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String valueOf(Integer value) {
        if (value == null) {
            return "";
        } else {
            for (CartGoodsType ms : CartGoodsType.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }
}
