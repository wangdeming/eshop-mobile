package cn.ibdsr.web.modular.shop.order.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.modular.shop.order.service.ShopPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 微信端：支付控制器
 *
 * @author xjc
 * @Date 2019-01-14 16:49:34
 */
@RestController
@RequestMapping("shop/pay")
public class ShopPayController extends BaseController {

    @Autowired
    private ShopPayService wechatOrderService;

    /**
     * 单订单支付
     *
     * @param shopOrderId 订单主键ID
     * @return SuccessDataTip
     */
    @RequestMapping(value = "pay")
    @WinXinLogin
    public SuccessDataTip pay(Long shopOrderId, int payType, String wechatPayType) throws Exception {
        return wechatOrderService.pay(shopOrderId, payType, wechatPayType);
    }

    /**
     * 支付成功后，前端调用此接口，向微信查询支付结果，然后处理订单
     *
     * @param actualOutTradeNo 商户订单号
     * @return SuccessDataTip
     */
    @RequestMapping(value = "orderquery")
    @WinXinLogin
    public SuccessTip orderquery(String actualOutTradeNo) throws Exception {
        return wechatOrderService.orderquery(actualOutTradeNo);
    }

    /**
     * 微信公众号回调接口
     */
    @RequestMapping(value = "wechatnotify")
    public void transcallback() throws Exception {
        wechatOrderService.payNotify(getHttpServletRequest(), getHttpServletResponse());
    }

}
