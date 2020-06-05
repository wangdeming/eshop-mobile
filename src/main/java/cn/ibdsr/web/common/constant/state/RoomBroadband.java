package cn.ibdsr.web.common.constant.state;

/**
 * @Description description
 * @Author xjc
 * @Date created in 2018/4/18 13:14
 * @Modifed by
 */
public enum RoomBroadband {

    NO(0, "无宽带"), BROADBAND(1, "有线宽带"), WIFI(2, "WIFI"), BROADBAND_WIFI(3, "WIFI、有线宽带");

    int code;
    String message;

    RoomBroadband(int code, String message) {
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
            for (RoomBroadband ms : RoomBroadband.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }

}
