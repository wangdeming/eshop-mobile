package cn.ibdsr.web.modular.hotel.order.service;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessTip;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/15 14:15
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/15 xujincai init
 */
public interface HotelPayService {
    SuccessDataTip pay(String hotelOrderId, Integer payType, String wechatPayType) throws Exception;

    SuccessTip orderquery(String actualOutTradeNo) throws Exception;

    void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception;

    void refund(String hotelOrderId) throws Exception;
}
