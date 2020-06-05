package cn.ibdsr.web.common.constant.state;

/**
 * @Description 评价类型枚举类
 * @Version V1.0
 * @CreateDate 2019-03-19 09:31:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-19 09:31:22    XuZhipeng               类说明
 *
 */
public enum EvaluateType {
    NULL(-1, ""), GOOD(1, "好评"), NORMAL(2, "中评"), BAD(3, "差评");

    int code;
    String message;

    EvaluateType(int code, String message) {
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
            for (EvaluateType ms : EvaluateType.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return "";
        }
    }

}
