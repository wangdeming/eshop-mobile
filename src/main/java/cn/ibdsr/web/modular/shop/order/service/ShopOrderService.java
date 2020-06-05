package cn.ibdsr.web.modular.shop.order.service;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.persistence.model.ShoppingCart;

/**
 * @Description: 特产店铺订单Service
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/3/19 13:31
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/3/19 xujincai init
 */
public interface ShopOrderService {
    SuccessDataTip address(Long addressId);

    SuccessDataTip insertOrderFromShoppingCart(Long addressId, String shoppingCartIds, int payTypeId, String wechatPayType) throws Exception;

    SuccessDataTip insertOrder(Long addressId, ShoppingCart shoppingCart, int payTypeId, String wechatPayType) throws Exception;

    SuccessDataTip orderDetailFromShoppingCart(String shoppingCartIds);

    SuccessDataTip orderDetail(Long shopId, ShoppingCart shoppingCart);
}
