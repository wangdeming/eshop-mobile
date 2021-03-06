package cn.ibdsr.web.common.constant.state;

/**
 * @Description description
 * @Author xjc
 * @Date created in 2018/4/18 13:14
 * @Modifed by
 */
public enum RoomStatus {

    NO(0, "未上架"),
    YES(1, "已上架");

    int code;
    String message;

    RoomStatus(int code, String message) {
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
            for (RoomStatus ms : RoomStatus.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }

}
