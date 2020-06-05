package cn.ibdsr.web.modular.shop.order.service.impl;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.IsDefault;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.GoodsMapper;
import cn.ibdsr.web.common.persistence.dao.GoodsSkuMapper;
import cn.ibdsr.web.common.persistence.dao.ShopMapper;
import cn.ibdsr.web.common.persistence.dao.ShoppingCartMapper;
import cn.ibdsr.web.common.persistence.dao.UserDeliveryAddressMapper;
import cn.ibdsr.web.common.persistence.model.Goods;
import cn.ibdsr.web.common.persistence.model.GoodsSku;
import cn.ibdsr.web.common.persistence.model.Shop;
import cn.ibdsr.web.common.persistence.model.ShopOrder;
import cn.ibdsr.web.common.persistence.model.ShopOrderGoods;
import cn.ibdsr.web.common.persistence.model.ShoppingCart;
import cn.ibdsr.web.common.persistence.model.UserDeliveryAddress;
import cn.ibdsr.web.core.mq.rabbitmq.MessageSenderTask;
import cn.ibdsr.web.core.util.GoodsSpecsUtils;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.core.util.OrderUtils;
import cn.ibdsr.web.core.util.VerifyUtils;
import cn.ibdsr.web.modular.shop.order.service.ShopOrderService;
import cn.ibdsr.web.modular.shop.order.service.ShopPayService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/3/19 13:32
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/3/19 xujincai init
 */
@Service
public class ShopOrderServiceImpl implements ShopOrderService {

    @Autowired
    private UserDeliveryAddressMapper userDeliveryAddressMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsSkuMapper goodsSkuMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopPayService payOrderService;

    @Autowired
    private MessageSenderTask messageSenderTask;

    /**
     * 查询收获地址
     *
     * @param addressId 收获地址ID
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip address(Long addressId) {
        UserDeliveryAddress param = new UserDeliveryAddress();
        param.setUserId(ConstantFactory.me().getUserId());
        param.setIsDeleted(IsDeleted.NORMAL.getCode());
        if (addressId == null) {
            param.setIsDefault(IsDefault.Y.getCode());
        } else {
            param.setId(addressId);
        }
        UserDeliveryAddress address = userDeliveryAddressMapper.selectOne(param);
        return new SuccessDataTip(address);
    }

    /**
     * 从购物车提交订单
     *
     * @param addressId       收获地址ID
     * @param shoppingCartIds 购物车主键ID
     * @param payTypeId       支付方式，微信支付方式值为1，支付宝支付方式值为2
     * @param wechatPayType   支付渠道（此渠道不同于订单编号中的下单渠道），微信公众号支付值为JSAPI，微信APP支付值为APP，微信扫码支付为NATIVE，支付宝支付值为ALIPAY
     * @return SuccessDataTip
     * @throws Exception Exception
     */
    @Override
    @Transactional
    public SuccessDataTip insertOrderFromShoppingCart(Long addressId, String shoppingCartIds, int payTypeId, String wechatPayType) throws Exception {
        if (!VerifyUtils.verifyPayType(payTypeId)) {
            throw new BussinessException(BizExceptionEnum.PAY_TYPE_IS_NOT_EXIT);
        }
        if (!VerifyUtils.verifyWechatPayType(wechatPayType)) {
            throw new BussinessException(BizExceptionEnum.WECHAT_PAY_TYPE_IS_NOT_EXIT);
        }
        Long userId = ConstantFactory.me().getUserId();
        Wrapper<ShoppingCart> shoppingCartWrapper = new EntityWrapper<>();
        shoppingCartWrapper.setSqlSelect("DISTINCT shop_id,user_id");
        shoppingCartWrapper.eq("user_id", userId);
        shoppingCartWrapper.in("id", shoppingCartIds);
        List<ShoppingCart> shopList = shoppingCartMapper.selectList(shoppingCartWrapper);
        JSONObject result = new JSONObject();
        List<Long> shopOrderIdList = new ArrayList<>();
        if (shopList.size() > 0) {
            //订单按商家拆分
            for (ShoppingCart shop : shopList) {
                if (!VerifyUtils.verifyUser(shop.getUserId())) {
                    throw new BussinessException(BizExceptionEnum.DATA_ERROR);
                }
                //查询购物车中单个商家的商品
                shoppingCartWrapper = new EntityWrapper<>();
                shoppingCartWrapper.eq("user_id", userId);
                shoppingCartWrapper.eq("shop_id", shop.getShopId());
                shoppingCartWrapper.in("id", shoppingCartIds);
                List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(shoppingCartWrapper);
                UserDeliveryAddress address = userDeliveryAddressMapper.selectById(addressId);
                Long shopOrderId = createOrder(shop.getShopId(), shoppingCartList, address);
                shopOrderIdList.add(shopOrderId);
            }
            result = payOrderService.getChannel(shopOrderIdList, payTypeId, wechatPayType);
        }

        // 放入消息队列，处理订单支付超时，1小时后自动取消
        messageSenderTask.sendMsgOfCancelShopOrder(shopOrderIdList);

        return new SuccessDataTip(result);
    }

    /**
     * 立即购买提交订单
     *
     * @param addressId     收获地址ID
     * @param shoppingCart  购买商品封装
     * @param payTypeId     支付方式，微信支付方式值为1，支付宝支付方式值为2
     * @param wechatPayType 支付渠道（此渠道不同于订单编号中的下单渠道），微信公众号支付值为JSAPI，微信APP支付值为APP，微信扫码支付为NATIVE，支付宝支付值为ALIPAY
     * @return SuccessDataTip
     * @throws Exception Exception
     */
    @Override
    @Transactional
    public SuccessDataTip insertOrder(Long addressId, ShoppingCart shoppingCart, int payTypeId, String wechatPayType) throws Exception {
        if (shoppingCart.getShopId() == null || shoppingCart.getGoodsId() == null || shoppingCart.getNum() == null) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        if (!VerifyUtils.verifyPayType(payTypeId)) {
            throw new BussinessException(BizExceptionEnum.PAY_TYPE_IS_NOT_EXIT);
        }
        if (!VerifyUtils.verifyWechatPayType(wechatPayType)) {
            throw new BussinessException(BizExceptionEnum.WECHAT_PAY_TYPE_IS_NOT_EXIT);
        }
        UserDeliveryAddress address = userDeliveryAddressMapper.selectById(addressId);
        if (!VerifyUtils.verifyUser(address.getUserId())) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        shoppingCartList.add(shoppingCart);
        Long shopOrderId = createOrder(shoppingCart.getShopId(), shoppingCartList, address);
        List<Long> shopOrderIdList = new ArrayList<>();
        shopOrderIdList.add(shopOrderId);
        JSONObject result = payOrderService.getChannel(shopOrderIdList, payTypeId, wechatPayType);

        // 放入消息队列，处理订单支付超时，1小时后自动取消
        messageSenderTask.sendMsgOfCancelShopOrder(shopOrderIdList);

        return new SuccessDataTip(result);
    }

    /**
     * 订单创建，两种情况，
     * 1、从购物车进入创建订单并删除购物车数据，shoppingCartList中的ShoppingCart都是含有主键ID
     * 2、直接购买，创建订单shoppingCartList的size为1，且ShoppingCart不含有主键ID
     *
     * @param shopId           商店ID
     * @param shoppingCartList 购买商品List
     * @param address          收获地址
     */
    private Long createOrder(Long shopId, List<ShoppingCart> shoppingCartList, UserDeliveryAddress address) throws Exception {
        //校验数据的正确性，shoppingCartList的size大于1，说明是从购物车进入创建订单的，则list中的ShoppingCart不可以存在没有主键ID的
        if (shoppingCartList.size() > 1) {
            for (ShoppingCart shoppingCart : shoppingCartList) {
                if (shoppingCart.getId() == null) {
                    throw new BussinessException(BizExceptionEnum.DATA_ERROR);
                }
            }
        }
        Long userId = ConstantFactory.me().getUserId();
        Date now = new Date();
        //商品总价格
        BigDecimal goodsPrice = new BigDecimal(0);
        //订单运费
        BigDecimal expressFee = new BigDecimal(0);
        //订单价格，实际支付金额
        BigDecimal orderPrice;
        List<ShopOrderGoods> shopOrderGoodsList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            Goods goods = goodsMapper.selectById(shoppingCart.getGoodsId());
            GoodsSku goodsSku = goodsSkuMapper.selectById(shoppingCart.getSkuId());
            //校验库存是否足够
            ShopOrderGoods shopOrderGoods = new ShopOrderGoods();
            if (goodsSku != null) {//商品有规格
                if (shoppingCart.getNum() > goodsSku.getStock()) {
                    throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_SKU_STOCK_NONE);
                }
                shopOrderGoods.setSkuId(goodsSku.getId());
                JSONObject goodsSpecsJson = JSONObject.parseObject(goodsSku.getSpecs());
                shopOrderGoods.setGoodsSpecs(GoodsSpecsUtils.joinGoodsSpecsValue(goodsSpecsJson, ","));
                shopOrderGoods.setUnitPrice(goodsSku.getPrice());

                //扣除库存
                GoodsSku param = new GoodsSku();
                param.setId(goodsSku.getId());
                param.setStock(goodsSku.getStock() - shoppingCart.getNum());
                param.setModifiedUser(userId);
                param.setModifiedTime(now);
                param.updateById();
            } else {//商品无规格
                if (shoppingCart.getNum() > goods.getStock()) {
                    throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_SKU_STOCK_NONE);
                }
                shopOrderGoods.setUnitPrice(goods.getPrice());

                //扣除库存
                Goods param = new Goods();
                param.setId(goods.getId());
                param.setStock(goods.getStock() - shoppingCart.getNum());
                param.setModifiedUser(userId);
                param.setModifiedTime(now);
                param.updateById();
            }
            shopOrderGoods.setGoodsId(goods.getId());
            shopOrderGoods.setGoodsName(goods.getName());
            shopOrderGoods.setGoodsImg(goods.getMainImg());
            shopOrderGoods.setNums(shoppingCart.getNum());
            shopOrderGoods.setCreatedUser(userId);
            shopOrderGoods.setCreatedTime(now);
            shopOrderGoods.setPrice(shopOrderGoods.getUnitPrice().multiply(new BigDecimal(shopOrderGoods.getNums())));
            shopOrderGoodsList.add(shopOrderGoods);

            //计算订单的商品的总价格、订单运费
            goodsPrice = goodsPrice.add(shopOrderGoods.getPrice());
            if (expressFee.compareTo(goods.getExpressFee()) < 0) {
                expressFee = goods.getExpressFee();
            }
        }
        orderPrice = goodsPrice.add(expressFee);

        ShopOrder shopOrder = new ShopOrder();
        shopOrder.setOrderNo(OrderUtils.genOrderNo(Const.ORDER_NO_ORDER_CHANNEL_1, Const.ORDER_NO_PAY_CHANNEL_1, Const.ORDER_NO_BUSINESS_TYPE_1, userId));
        shopOrder.setShopId(shopId);
        shopOrder.setUserId(userId);
        shopOrder.setOrderPrice(orderPrice);
        shopOrder.setGoodsPrice(goodsPrice);
        shopOrder.setExpressFee(expressFee);
        shopOrder.setConsigneeName(address.getConsigneeName());
        shopOrder.setConsigneePhone(address.getConsigneePhone());
        shopOrder.setProvince(address.getProvince());
        shopOrder.setCity(address.getCity());
        shopOrder.setDistrict(address.getDistrict());
        shopOrder.setAddress(address.getAddress());
        shopOrder.setCreatedUser(userId);
        //订单插入
        shopOrder.setCreatedTime(now);
        shopOrder.insert();

        //扣除库存

        for (ShopOrderGoods shopOrderGoods : shopOrderGoodsList) {
            shopOrderGoods.setOrderId(shopOrder.getId());
            //订单商品插入
            shopOrderGoods.insert();
        }
        for (ShoppingCart shoppingCart : shoppingCartList) {
            if (shoppingCart.getId() != null) {
                shoppingCart.deleteById();
            }
        }
        return shopOrder.getId();
    }

    /**
     * 获取购物车详情
     *
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip orderDetailFromShoppingCart(String shoppingCartIds) {
        Long userId = ConstantFactory.me().getUserId();
        Wrapper<ShoppingCart> shoppingCartWrapper = new EntityWrapper<>();
        shoppingCartWrapper.setSqlSelect("DISTINCT shop_id,user_id").eq("user_id", userId).in("id", StringUtils.split(shoppingCartIds, ","));
        List<ShoppingCart> shopList = shoppingCartMapper.selectList(shoppingCartWrapper);
        List<JSONObject> orderList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shopList) {
            if (!VerifyUtils.verifyUser(shoppingCart.getUserId())) {
                throw new BussinessException(BizExceptionEnum.DATA_ERROR);
            }
            shoppingCartWrapper = new EntityWrapper<>();
            shoppingCartWrapper.eq("user_id", userId).eq("shop_id", shoppingCart.getShopId()).in("id", StringUtils.split(shoppingCartIds, ","));
            List<ShoppingCart> shoppingCartList = shoppingCartMapper.selectList(shoppingCartWrapper);
            JSONObject order = orderDetailByShopId(shoppingCart.getShopId(), shoppingCartList);
            orderList.add(order);
        }
        return new SuccessDataTip(orderList);
    }

    /**
     * 获取立即购买商品详情
     *
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip orderDetail(Long shopId, ShoppingCart shoppingCart) {
        if (shoppingCart.getShopId() == null || shoppingCart.getGoodsId() == null || shoppingCart.getNum() == null) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        if (shoppingCart.getSkuId() != null) {
            GoodsSku goodsSku = goodsSkuMapper.selectById(shoppingCart.getSkuId());
            if (shoppingCart.getNum() > goodsSku.getStock()) {
                throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_SKU_STOCK_NONE);
            }
        }
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        shoppingCartList.add(shoppingCart);
        JSONObject order = orderDetailByShopId(shopId, shoppingCartList);
        List<JSONObject> orderList = new ArrayList<>();
        orderList.add(order);
        return new SuccessDataTip(orderList);
    }

    private JSONObject orderDetailByShopId(Long shopId, List<ShoppingCart> shoppingCartList) {
        Wrapper<Shop> shopWrapper = new EntityWrapper<>();
        shopWrapper.setSqlSelect("id", "front_name name", "CONCAT('" + ImageUtil.PREFIX_IMAGE_URL + "',logo) logo");
        shopWrapper.eq("id", shopId);
        List<Shop> shopList = shopMapper.selectList(shopWrapper);
        List<JSONObject> goodsList = new ArrayList<>();
        //商品总价格
        BigDecimal price = new BigDecimal(0);
        //订单运费
        BigDecimal expressFee = new BigDecimal(0);
        int nums = 0;
        for (ShoppingCart shoppingCart : shoppingCartList) {
            JSONObject goodsJson = new JSONObject();
            Goods goods = goodsMapper.selectById(shoppingCart.getGoodsId());
            goodsJson.put("shoppingCartId", shoppingCart.getId());
            goodsJson.put("name", goods.getName());
            goodsJson.put("nums", shoppingCart.getNum());

            if (shoppingCart.getSkuId() != null) {//商品有多规格时
                GoodsSku goodsSku = goodsSkuMapper.selectById(shoppingCart.getSkuId());
                goodsJson.put("image", StringUtils.isNotBlank(goodsSku.getImg()) ? ImageUtil.PREFIX_IMAGE_URL + goodsSku.getImg() : ImageUtil.PREFIX_IMAGE_URL + goods.getMainImg());
                goodsJson.put("price", goodsSku.getPrice().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
                JSONObject goodsSpecsJson = JSONObject.parseObject(goodsSku.getSpecs());
                goodsJson.put("sku", GoodsSpecsUtils.joinGoodsSpecsValue(goodsSpecsJson, ","));
                price = price.add(goodsSku.getPrice().multiply(new BigDecimal(shoppingCart.getNum())));
            } else {//商品没有规格时
                goodsJson.put("image", ImageUtil.PREFIX_IMAGE_URL + goods.getMainImg());
                goodsJson.put("price", goods.getPrice().divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
                price = price.add(goods.getPrice().multiply(new BigDecimal(shoppingCart.getNum())));
            }

            goodsList.add(goodsJson);
            nums = nums + shoppingCart.getNum();
            if (expressFee.compareTo(goods.getExpressFee()) < 0) {
                expressFee = goods.getExpressFee();
            }
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("shop", JSONObject.parseObject(JSONObject.toJSONString(shopList.get(0))));
        returnJson.put("goodsList", goodsList);
        returnJson.put("price", price.add(expressFee).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        returnJson.put("expressFee", expressFee.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
        returnJson.put("nums", nums);
        return returnJson;
    }

}
