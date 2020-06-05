package cn.ibdsr.web.modular.shop.goods.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.constant.state.CartGoodsType;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.modular.shop.goods.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description 购物车控制器
 * @Version V1.0
 * @CreateDate 2019-03-20 11:25:36
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-20 11:25:36    XuZhipeng               类说明
 *
 */
@RestController
@RequestMapping("shop/shoppingCart")
public class ShoppingCartController extends BaseController {

    @Autowired
    private IShoppingCartService shoppingCartService;

    final static Integer ADD_ONE = 1;

    final static Integer MINUS_ONE = -1;

    /**
     * 获取购物车正常商品列表
     */
    @RequestMapping(value = "listNormalGoods")
    @WinXinLogin
    public Object listNormalGoods() {
        Map<String, Object> result = shoppingCartService.listCarts(CartGoodsType.NORMAL.getCode());
        return new SuccessDataTip(result);
    }

    /**
     * 获取购物车失效商品列表
     */
    @RequestMapping(value = "listExpireGoods")
    @WinXinLogin
    public Object listExpireGoods() {
        Map<String, Object> result = shoppingCartService.listCarts(CartGoodsType.EXPIRE.getCode());
        return new SuccessDataTip(result);
    }


    /**
     * 新增商品到购物车
     *
     * @param goodsId 商品ID
     * @param skuId 规格商品ID
     * @param num 数量
     */
    @RequestMapping(value = "add")
    @WinXinLogin
    public Object add(Long goodsId, Long skuId, Integer num) {
        shoppingCartService.add(goodsId, skuId, num);
        return super.SUCCESS_TIP;
    }

    /**
     * 购物车指定商品数量+1
     *
     * @param cartId 购物车ID
     */
    @RequestMapping(value = "addOne")
    @WinXinLogin
    public Object addOne(Long cartId) {
        shoppingCartService.addOrMinus(cartId, ADD_ONE);
        return super.SUCCESS_TIP;
    }

    /**
     * 购物车指定商品数量-1
     *
     * @param cartId 购物车ID
     */
    @RequestMapping(value = "minusOne")
    @WinXinLogin
    public Object minusOne(Long cartId) {
        shoppingCartService.addOrMinus(cartId, MINUS_ONE);
        return super.SUCCESS_TIP;
    }

    /**
     * 修改指定商品购物车数量
     *
     * @param cartId 购物车ID
     * @param num 数量
     */
    @RequestMapping(value = "updateNum")
    @WinXinLogin
    public Object updateNum(Long cartId, Integer num) {
        shoppingCartService.updateNum(cartId, num);
        return super.SUCCESS_TIP;
    }

    /**
     * 删除购物车
     *
     * @param cartIds 购物车ID数组
     */
    @RequestMapping(value = "delete")
    @WinXinLogin
    public Object delete(Long[] cartIds) {
        if (null == cartIds || 0 == cartIds.length) {
            throw new BussinessException(BizExceptionEnum.SHOPPING_CART_ID_IS_NULL);
        }
        List<Long> cartIdList = Arrays.asList(cartIds);
        shoppingCartService.delete(cartIdList);
        return SUCCESS_TIP;
    }

    /**
     * 清空购物车失效商品
     */
    @RequestMapping(value = "clean")
    @WinXinLogin
    public Object clean() {
        shoppingCartService.clean();
        return SUCCESS_TIP;
    }

}
