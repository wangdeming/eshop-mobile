package cn.ibdsr.web.common.constant.state;

/**
 * @Description description
 * @Author xjc
 * @Date created in 2018/4/18 13:14
 * @Modifed by
 */
public enum RoomCanCancel {

    NO(0, "不可取消"), YES(1, "免费取消");

    int code;
    String message;

    RoomCanCancel(int code, String message) {
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
            for (RoomCanCancel ms : RoomCanCancel.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }

}
