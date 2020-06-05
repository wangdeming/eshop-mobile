package cn.ibdsr.web.common.constant;

/**
 * 系统常量
 *
 * @author fengshuonan
 * @date 2017年2月12日 下午9:42:53
 */
public interface Const {

    /**
     * 系统默认的管理员密码
     */
    String DEFAULT_PASSWORD = "111111";

    /**
     * 管理员角色的名字
     */
    String ADMIN_NAME = "administrator";

    /**
     * 管理员id
     */
    Integer ADMIN_ID = 1;

    /**
     * 超级管理员角色id
     */
    Integer ADMIN_ROLE_ID = 1;

    /**
     * 接口文档的菜单名
     */
    String API_MENU_NAME = "接口文档";

    /**
     * 订单编号允许的下单渠道值
     * 下单渠道：微信公众号渠道（1）
     */
    int ORDER_NO_ORDER_CHANNEL_1 = 1;
    /**
     * 订单编号允许的支付渠道值
     * 支付渠道：微信支付（1）
     */
    int ORDER_NO_PAY_CHANNEL_1 = 1;
    /**
     * 订单编号允许的业务类型值
     * 业务类型：特产（1），酒店（2），门票（3）
     */
    int ORDER_NO_BUSINESS_TYPE_1 = 1;
    int ORDER_NO_BUSINESS_TYPE_2 = 2;
    int ORDER_NO_BUSINESS_TYPE_3 = 3;

    /**
     * 下单支付显示的在subject的字
     */
    String APP_NAME = "灵山公众号";

    String SUCCESS = "SUCCESS";
    String FAIL = "FAIL";

    /**
     * 微信下单支付回调地址
     */
    String SHOP_WX_PAY_NOTIFY = "/shop/pay/wechatnotify";
    String HOTEL_WX_PAY_NOTIFY = "/hotel/pay/wechatnotify";

    /**
     * 订单超时时间
     */
    int SHOP_ORDER_OUT_TIME = 3600;
    int HOTEL_ORDER_OUT_TIME = 1800;
    /**
     * 酒店订单超过天数不可评价
     */
    int HOTEL_ORDER_EVALUATE_OUT_DAY = 10;

    /**
     * 支付渠道（此渠道不同于订单编号中的下单渠道），微信公众号支付值为JSAPI，微信APP支付值为APP，微信扫码支付为NATIVE，支付宝支付值为ALIPAY
     */
    String WECHAT_PAY_TYPE_JSAPI = "JSAPI";
    String WECHAT_PAY_TYPE_NATIVE = "NATIVE";
    String WECHAT_PAY_TYPE_WEB = "APP";
    String WECHAT_PAY_TYPE_ALIPAY = "ALIPAY";

    /**
     * 图片上传最大限制3M
     */
    Long IMAGE_SIZE_MAX_LIMIT = 3 * 1024 * 1024L;

    /**
     * 申请退款做多次数限制
     */
    int REFUND_MAX_COUNT_LIMIT = 5;

    /**
     * 处理期限天数
     */
    int HANDLE_DAY_LIMIT = 7;

    /**
     * 微信登录过期时间，29天，以秒为单位
     */
    int WECHAT_LOGIN_DATETIME_MAX = 2505600;

    /**
     * 酒店订单评价默认分数
     */
    int HOTEL_ORDER_EVALUATE_DEFAULT_SCORE = 5;
}
