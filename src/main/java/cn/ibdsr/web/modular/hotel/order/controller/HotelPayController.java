package cn.ibdsr.web.modular.hotel.order.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.modular.hotel.order.service.HotelPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/15 14:12
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/15 xujincai init
 */
@RestController
@RequestMapping("hotel/pay")
public class HotelPayController extends BaseController {

    @Autowired
    private HotelPayService hotelPayService;

    /**
     * 订单发起支付
     *
     * @param orderId  订单ID
     * @param payType       支付方式，微信支付方式值为1，支付宝支付方式值为2
     * @param wechatPayType 支付渠道（此渠道不同于订单编号中的下单渠道），微信公众号支付值为JSAPI，微信APP支付值为APP，微信扫码支付为NATIVE，支付宝支付值为ALIPAY
     * @return SuccessDataTip
     * @throws Exception Exception
     */
    @RequestMapping(value = "pay")
    @WinXinLogin
    public SuccessDataTip pay(@RequestParam String orderId, @RequestParam Integer payType, @RequestParam String wechatPayType) throws Exception {
        return hotelPayService.pay(orderId, payType, wechatPayType);
    }

    /**
     * 支付成功后，前端调用此接口，向微信查询支付结果，然后处理订单
     *
     * @param actualOutTradeNo 商户订单号
     * @return SuccessTip
     * @throws Exception Exception
     */
    @RequestMapping(value = "orderquery")
    @WinXinLogin
    public SuccessTip orderquery(String actualOutTradeNo) throws Exception {
        return hotelPayService.orderquery(actualOutTradeNo);
    }

    /**
     * 微信公众号回调接口
     *
     * @throws Exception Exception
     */
    @RequestMapping(value = "wechatnotify")
    public void payNotify() throws Exception {
        hotelPayService.payNotify(getHttpServletRequest(), getHttpServletResponse());
    }

}
