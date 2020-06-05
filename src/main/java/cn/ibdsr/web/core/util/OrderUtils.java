package cn.ibdsr.web.core.util;

import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * @Description: 订单工具类
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/3/20 9:56
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/3/20 xujincai init
 */
public class OrderUtils {

    /**
     * 订单号规则（19位）：下单渠道1位+支付渠道1位+业务类型1位+时间信息10位+用户ID6位
     * 定义：
     * 下单渠道：微信公众号渠道（1），支持后续扩展最多10个
     * 支付渠道：微信支付（1），支持后续支持扩展最多是个
     * 业务类型：特产（1），酒店（2），门票（3），支持后续扩展
     * 时间信息：10位，代表下单时间
     * 用户ID：6位，代表下单用户ID
     *
     * @param orderChannel 下单渠道
     * @param payChannel   支付渠道
     * @param businessType 业务类型
     * @param userId       用户ID
     * @return 订单号
     */
    public static String genOrderNo(int orderChannel, int payChannel, int businessType, long userId) {
        if (Const.ORDER_NO_ORDER_CHANNEL_1 != orderChannel) {
            throw new BussinessException(BizExceptionEnum.ORDER_NO_ORDER_CHANNEL_ERROR);
        }
        if (Const.ORDER_NO_PAY_CHANNEL_1 != payChannel) {
            throw new BussinessException(BizExceptionEnum.ORDER_NO_PAY_CHANNEL_ERROR);
        }
        if (Const.ORDER_NO_BUSINESS_TYPE_1 != businessType && Const.ORDER_NO_BUSINESS_TYPE_2 != businessType && Const.ORDER_NO_BUSINESS_TYPE_3 != businessType) {
            throw new BussinessException(BizExceptionEnum.ORDER_NO_BUSINESS_TYPE_ERROR);
        }
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String userId1 = StringUtils.leftPad(String.valueOf(userId), 6, "0");
        String[] strings = {String.valueOf(orderChannel), String.valueOf(payChannel), String.valueOf(businessType), timestamp, userId1};
        return StringUtils.join(strings, "");
    }

    /**
     * 高并发唯一订单号
     *
     * @param prefix 订单号前缀
     * @return
     */
    public static String getOrderNoByUUId(Object prefix) {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        String time = format.format(date);
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {// 有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // 11 代表长度为11
        // d 代表参数为正数型
        return prefix + time + String.format("%011d", hashCodeV);
    }
}
