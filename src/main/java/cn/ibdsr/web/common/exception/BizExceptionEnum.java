package cn.ibdsr.web.common.exception;

/**
 * @author fengshuonan
 * @Description 所有业务异常的枚举
 * @date 2016年11月12日 下午5:04:51
 */
public enum BizExceptionEnum {

    DATA_ERROR(300, "数据错误"),
    SPRING_PROFILES_ACTIVE_ERROR(301, "环境异常"),

    /**
     * 字典
     */
    DICT_EXISTED(400, "字典已经存在"),
    ERROR_CREATE_DICT(500, "创建字典失败"),
    ERROR_WRAPPER_FIELD(500, "包装字典属性失败"),

    /**
     * 文件上传
     */
    FILE_READING_ERROR(400, "FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400, "FILE_NOT_FOUND!"),
    UPLOAD_ERROR(500, "上传图片出错"),
    IMAGE_FILE_TYPE_ERROR(400, "上传图片格式错误"),
    IMAGE_FILE_CONTENT_ERROR(400, "上传图片内容不正确"),
    IMAGE_UPLOAD_SIZE_TOO_BIG(400, "上传图片文件大小超过阈值"),

    /**
     * 权限和数据问题
     */
    DB_RESOURCE_NULL(400, "数据库中没有该资源"),
    NO_PERMITION(405, "权限异常"),
    REQUEST_INVALIDATE(400, "请求数据格式不正确"),
    INVALID_KAPTCHA(400, "验证码不正确"),
    CANT_DELETE_ADMIN(600, "不能删除超级管理员"),
    CANT_FREEZE_ADMIN(600, "不能冻结超级管理员"),
    CANT_CHANGE_ADMIN(600, "不能修改超级管理员角色"),

    /**
     * 账户问题
     */
    USER_ALREADY_REG(401, "该用户已经注册"),
    NO_THIS_USER(400, "没有此用户"),
    USER_NOT_EXISTED(400, "没有此用户"),
    ACCOUNT_FREEZED(401, "账号被冻结"),
    OLD_PWD_NOT_RIGHT(402, "原密码不正确"),
    TWO_PWD_NOT_MATCH(405, "两次输入密码不一致"),

    /**
     * 错误的请求
     */
    MENU_PCODE_COINCIDENCE(400, "菜单编号和副编号不能一致"),
    EXISTED_THE_MENU(400, "菜单编号重复，不能添加"),
    DICT_MUST_BE_NUMBER(400, "字典的值必须为数字"),
    REQUEST_NULL(400, "请求有错误"),
    SESSION_TIMEOUT(400, "会话超时"),
    SERVER_ERROR(500, "服务器异常"),

    /**
     * 支付
     */
    ORDER_JSON_IS_NULL(900, "订单不能为空"),
    ORDER_ID_IS_NULL(901, "订单ID不能为空"),
    ORDER_UNABLE_CANCEL(902, "订单不能取消"),
    ALIPAY_PAY_ERROR(903, "支付宝调用异常，请重试"),
    ORDER_ID_IS_NOT_EXIT(904, "该订单不存在"),
    PAY_TYPE_IS_NULL(905, "支付方式不能为空"),
    PAY_TYPE_IS_NOT_EXIT(906, "暂不支持该支付方式"),
    TRADE_CODE_IS_NULL(907, "支付号不能为空"),
    ORDER_JSON_IS_ERROR(908, "订单格式错误"),
    ORDER_IS_WAIT_ERROR(909, "订单必须是待支付状态"),
    ORDER_PAY_IS_NOT_OPEN(910, "暂未开通该支付方式"),
    ORDER_STATUS_ERROR(911, "订单状态异常"),
    ORDER_SREVICE_PRICE_IS_NULL(912, "价格不能为空或不合法"),
    ORDER_PAY_ERROR(913, "订单支付异常"),
    ORDER_OUT_TIME(914, "订单超时已关闭"),
    WECHAT_PAY_TYPE_IS_NOT_EXIT(915, "微信支付渠道错误"),
    ORDER_PAY_SUCCESS(916, "支付成功"),
    ORDER_PAY_FAIL(917, "支付失败"),
    ORDER_PAY_PAID(918, "订单已支付"),
    ORDER_NO_ORDER_CHANNEL_ERROR(919, "下单渠道值错误"),
    ORDER_NO_PAY_CHANNEL_ERROR(920, "支付渠道值错误"),
    ORDER_NO_BUSINESS_TYPE_ERROR(921, "业务类型值错误"),

    /**
     * 店铺
     */
    SHOP_IS_NOT_EXIST(1000, "店铺不存在"),
    SHOP_ID_IS_NULL(1001, "店铺ID不能为空"),
    SHOP_IS_CLOSE(1002, "店铺已打烊"),

    /**
     * 商品
     */
    GOODS_IS_NOT_EXIST(1100, "商品不存在"),
    GOODS_ID_IS_NULL(1101, "商品ID不能为空"),
    GOODS_SKU_IS_NOT_EXIST(1102, "规格商品不存在"),
    GOODS_SPECS_IS_NULL(1103, "商品规格不能为空"),
    GOODS_SKU_ID_IS_NULL(1104, "规格商品ID不能为空"),
    SHOP_ORDER_GOODS_EVALUATE_INFO_IS_NULL(1105, "订单商品评价信息不能为空"),
    SHOP_ORDER_CANNOT_EVALUATE(1106, "订单状态不是待评价状态，无法评价"),
    SHOP_ORDER_ID_IS_NULL(1107, "订单ID不能为空"),
    SHOP_ORDER_IS_NOT_EXIST(1108, "订单不存在"),
    SHOP_ORDER_GOODS_ID_IS_NULL(1109, "订单商品ID不能为空"),
    SHOP_ORDER_GOODS_IS_NOT_EXIST(1110, "订单商品不存在"),
    EVALUATE_FAIL(1111, "评价失败，请检查订单商品信息"),
    SHOP_ORDER_STATUS_IS_NOT_EXIST(1112, "不存在该订单状态"),

    /**
     * 收货地址
     */
    ADDRESS_ID_IS_NULL(1200, "收货地址ID不能为空"),
    ADDRESS_IS_NOT_EXIST(1201, "收货地址不存在"),
    ADDRESS_ARRIVAL_CAP(1202, "收货地址数量到达上限，不可增加"),

    /**
     * 地区区域
     */
    AREA_PID_IS_NULL(2200, "父地区ID不能为空"),
    /**
     * 游客
     */
    TOURIST_IS_NOT_EXIST(1400, "不存在游客，请添加"),
    TOURIST_ID_IS_NULL(1401, "游客ID不能为空"),
    TOURIST_ARRIVAL_CAP(1402, "游客数量到达上限，不可增加"),

    /**
     * 特产店铺订单
     */
    SHOP_ORDER_ADDRESS_MAX(1301, "收获地址已达最大限制15个，无法再增加。"),
    SHOP_ORDER_ADDRESS_NAME_LENGTH(1302, "姓名不能为空且不能超过最大字数限制15"),
    SHOP_ORDER_ADDRESS_PHONE_ZERO(1303, "手机号不能为空"),
    SHOP_ORDER_ADDRESS_PHONE_FORMAT_ERROR(1304, "手机号格式错误"),
    SHOP_ORDER_ADDRESS_PROVINCE(1305, "请选择省"),
    SHOP_ORDER_ADDRESS_CITY(1306, "请选择市"),
    SHOP_ORDER_ADDRESS_DISTRICT(1307, "请选择县或区"),
    SHOP_ORDER_ADDRESS_DETAIL_LENGTH_MIN(1308, "详细地址不能为空且字数不能少于5个"),
    SHOP_ORDER_ADDRESS_DETAIL_LENGTH_MAX(1309, "详细地址不能为空且字数不能大于200个"),
    SHOP_ORDER_GOODS_SKU_STOCK_NONE(1310, "库存不足"),


    /**
     * 购物车
     */
    GOODS_NUM_ERROR(1500, "请填写商品数量"),
    GOODS_SKU_STOCK_IS_NOT_ENOUGH(1501, "商品库存不足"),
    GOODS_STATUS_IS_OFFSHELF(1502, "商品已下架"),
    SHOPPING_CART_IS_NOT_EXIST(1503, "购物车商品不存在"),
    SHOPPING_CART_ID_IS_NULL(1504, "购物车ID不能为空"),
    SHOPPING_CART_COUNT_IS_MAX_LIMIT(1505, "购物车最多允许加入20种商品，超过上限无法继续添加"),

    /**
     * 退款/售后
     */
    ORDER_GOODS_CAN_NOT_REFUND(1601, "该订单商品售后已完成或在售后服务中，无法进行二次申请"),
    REFUND_COUNT_MORE_THAN_LIMIT(1602, "您已经发起了5次退款申请，暂时不能再发起申请"),
    REFUND_AMOUNT_IS_EXCEED(1603, "退款金额超出可退金额，请重新输入"),
    ORDER_IS_NOT_PAIED(1604, "订单未支付，无法申请退款"),
    REFUND_AMOUNT_IS_ERROR(1605, "退款金额必须大于0"),
    REFUND_ID_IS_NULL(1606, "退款订单ID不能为空"),
    REFUND_ORDER_IS_NOT_EXIST(1607, "退款订单不存在"),
    IMG_LIST_SIZE_MAX_LIMIT(1608, "凭证图片不能多余5张"),
    REFUND_LOGISTICS_IS_NOT_EXIST(1609, "退货物流信息不存在"),
    REFUND_LOGISTICS_IS_NOT_WAIT_DELIVERY(1610, "该退款订单不是待发货状态"),
    RERUND_PAY_IS_NOT_OPEN(1611, "暂为开通该退款方式"),
    REFUND_STATUS_CAN_NOT_REVOKE(1612, "该退款订单在当前状态下不能撤销"),
    REFUND_HAS_LOGISTICS_CAN_NOT_REVOKE(1613, "用户已发货，该退款订单不能被撤销"),
    REFUND_GOODS_NUM_IS_ERROR(1614, "售后商品数量超过购买商品数量"),
    REFUND_FAIL(1615, "退款失败"),

    HOTEL_CHEK_IN_DATE_BEFORE(1701, "入住日期须小于离店日期"),
    HOTEL_ORDER_NOT_CAN_DELETE(1702, "该订单不可删除"),
    HOTEL_ORDER_EVALUATE_REPEAT(1703, "订单不能重复评价"),
    HOTEL_ORDER_SCORE_NOTE_NULL(1704, "请选择评分"),
    HOTEL_ORDER_EVALUATE_IMAGE_MAX(1705, "评价图片最多上传9张"),
    HOTEL_ORDER_EVALUATE_CONTENT_MAX(1706, "评价内容最多300字"),
    HOTEL_ROOM_NOT_CAN_ORDER(1707, "该房间已售罄，不可预定。"),
    HOTEL_ROOM_SET_USER(1708, "请填写入住人"),
    HOTEL_ROOM_STATUS_0(1709, "房间已下架"),
    HOTEL_ORDER_CAN_NOT_CANCEL(1710, "订单不可取消"),
    HOTEL_ORDER_CAN_NOT_EVALUATE(1711, "订单已超过10天，已不可评价。"),
    HOTEL_ORDER_OUT_CANCEL_DATETIME(1712, "已超过免费取消时间，该订单不可取消。"),
    ;

    BizExceptionEnum(int code, String message) {
        this.friendlyCode = code;
        this.friendlyMsg = message;
    }

    BizExceptionEnum(int code, String message, String urlPath) {
        this.friendlyCode = code;
        this.friendlyMsg = message;
        this.urlPath = urlPath;
    }

    private int friendlyCode;

    private String friendlyMsg;

    private String urlPath;

    public int getCode() {
        return friendlyCode;
    }

    public void setCode(int code) {
        this.friendlyCode = code;
    }

    public String getMessage() {
        return friendlyMsg;
    }

    public void setMessage(String message) {
        this.friendlyMsg = message;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

}
