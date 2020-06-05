package cn.ibdsr.web.modular.shop.order.controller;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.persistence.model.ShoppingCart;
import cn.ibdsr.web.modular.shop.order.service.ShopOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 特产店铺订单操作
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/3/19 13:29
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/3/19 xujincai init
 */
@RestController
@RequestMapping(value = "shop/order")
public class ShopOrderController {

    @Autowired
    private ShopOrderService shopOrderService;

    /**
     * 查询收获地址
     *
     * @param addressId 收获地址ID
     * @return SuccessDataTip
     */
    @RequestMapping(value = "address")
    @WinXinLogin
    public SuccessDataTip address(Long addressId) {
        return shopOrderService.address(addressId);
    }

    /**
     * 从购物车提交订单
     *
     * @param addressId       收获地址ID
     * @param shoppingCartIds 购物车主键ID
     * @param payType         支付方式，微信支付方式值为1，支付宝支付方式值为2
     * @param wechatPayType   支付渠道（此渠道不同于订单编号中的下单渠道），微信公众号支付值为JSAPI，微信APP支付值为APP，微信扫码支付为NATIVE，支付宝支付值为ALIPAY
     * @return SuccessDataTip
     * @throws Exception Exception
     */
    @RequestMapping(value = "insertorderfromshoppingcart")
    @WinXinLogin
    public SuccessDataTip insertOrderFromShoppingCart(Long addressId, String shoppingCartIds, int payType, String wechatPayType) throws Exception {
        return shopOrderService.insertOrderFromShoppingCart(addressId, shoppingCartIds, payType, wechatPayType);
    }

    /**
     * 立即购买提交订单
     *
     * @param addressId     收获地址ID
     * @param shoppingCart  购买商品封装
     * @param payType       支付方式，微信支付方式值为1，支付宝支付方式值为2
     * @param wechatPayType 支付渠道（此渠道不同于订单编号中的下单渠道），微信公众号支付值为JSAPI，微信APP支付值为APP，微信扫码支付为NATIVE，支付宝支付值为ALIPAY
     * @return SuccessDataTip
     * @throws Exception Exception
     */
    @RequestMapping(value = "insertorder")
    @WinXinLogin
    public SuccessDataTip insertOrder(Long addressId, ShoppingCart shoppingCart, int payType, String wechatPayType) throws Exception {
        return shopOrderService.insertOrder(addressId, shoppingCart, payType, wechatPayType);
    }

    /**
     * 获取购物车详情
     *
     * @return SuccessDataTip
     */
    @RequestMapping(value = "orderdetailfromshoppingcart")
    @WinXinLogin
    public SuccessDataTip orderDetailFromShoppingCart(String shoppingCartIds) {
        return shopOrderService.orderDetailFromShoppingCart(shoppingCartIds);
    }

    /**
     * 获取立即购买商品详情
     *
     * @return SuccessDataTip
     */
    @RequestMapping(value = "orderdetail")
    @WinXinLogin
    public SuccessDataTip orderDetail(Long shopId, ShoppingCart shoppingCart) {
        return shopOrderService.orderDetail(shopId, shoppingCart);
    }

}
