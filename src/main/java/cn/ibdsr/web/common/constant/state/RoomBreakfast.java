package cn.ibdsr.web.common.constant.state;

/**
 * @Description description
 * @Author xjc
 * @Date created in 2018/4/18 13:14
 * @Modifed by
 */
public enum RoomBreakfast {

    NO(0, "无早餐"), SINGLE(1, "单人早餐"), DOUBLE(2, "双人早餐");

    int code;
    String message;

    RoomBreakfast(int code, String message) {
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
            for (RoomBreakfast ms : RoomBreakfast.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }

}
