package cn.ibdsr.web.common.constant.state;

/**
 * @Description description
 * @Author xjc
 * @Date created in 2018/4/18 13:14
 * @Modifed by
 */
public enum RoomSettingStatus {

    CLOSE(0, "关"),
    OPEN(1, "开");

    int code;
    String message;

    RoomSettingStatus(int code, String message) {
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
            for (RoomSettingStatus ms : RoomSettingStatus.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }

}
