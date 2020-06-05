package cn.ibdsr.web.core.util;

import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.modular.shop.order.payment.PayType;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 校验工具
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/3/27 10:16
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/3/27 xujincai init
 */
public class VerifyUtils {

    /**
     * 为了数据安全性，校验用户，防止修改别人的数据
     *
     * @param userId
     * @return
     */
    public static boolean verifyUser(Long userId) {
        if (userId != null && userId.equals(ConstantFactory.me().getUserId())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 校验支付方式
     *
     * @param payTypeId 支付方式Code
     * @return boolean
     */
    public static boolean verifyPayType(int payTypeId) {
        PayType payType = PayType.valueOf(payTypeId);
        if (payType != null && payType.getIsOpen()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 校验微信支付渠道
     *
     * @param wechatPayType 微信支付支付渠道（此渠道不同于订单编号中的下单渠道），公众号支付值为H5，APP支付值为APP，网页端支付值为WEB
     * @return boolean
     */
    public static boolean verifyWechatPayType(String wechatPayType) {
        if (StringUtils.equals(Const.WECHAT_PAY_TYPE_JSAPI, wechatPayType)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

}
