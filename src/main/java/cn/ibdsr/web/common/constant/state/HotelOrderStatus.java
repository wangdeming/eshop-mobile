package cn.ibdsr.web.common.constant.state;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/14 9:39
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/14 xujincai init
 */
public enum HotelOrderStatus {

    WAIT_PAY(1, "待付款"),
    WAIT_CONFIRM(2, "支付成功，等待确认"),
    WAIT_USE(3, "待使用"),
    CONSUMED(4, "已消费"),
    CANCELLED(5, "已取消"),
    Finished(6, "已完成"),
    ;

    int code;
    String message;

    HotelOrderStatus(int code, String message) {
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
            for (HotelOrderStatus ms : HotelOrderStatus.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }

}
