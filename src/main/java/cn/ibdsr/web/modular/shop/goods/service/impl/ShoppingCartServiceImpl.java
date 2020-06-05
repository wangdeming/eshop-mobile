package cn.ibdsr.web.modular.shop.goods.service.impl;

import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.GoodsStatus;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.GoodsMapper;
import cn.ibdsr.web.common.persistence.dao.GoodsSkuMapper;
import cn.ibdsr.web.common.persistence.dao.ShoppingCartMapper;
import cn.ibdsr.web.common.persistence.model.Goods;
import cn.ibdsr.web.common.persistence.model.GoodsSku;
import cn.ibdsr.web.common.persistence.model.ShoppingCart;
import cn.ibdsr.web.core.util.AmountFormatUtil;
import cn.ibdsr.web.core.util.GoodsSpecsUtils;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.shop.goods.dao.ShoppingCartDao;
import cn.ibdsr.web.modular.shop.goods.service.IShoppingCartService;
import cn.ibdsr.web.modular.shop.goods.transfer.shoppingcart.CartAddDTO;
import cn.ibdsr.web.modular.shop.goods.transfer.shoppingcart.CartGoodsVO;
import cn.ibdsr.web.modular.shop.goods.transfer.shoppingcart.CartVO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 购物车Service
 * @Version V1.0
 * @CreateDate 2019-03-20 11:25:36
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-20 11:25:36    XuZhipeng               类说明
 *
 */
@Service
public class ShoppingCartServiceImpl implements IShoppingCartService {

    @Autowired
    private ShoppingCartDao shoppingCartDao;

    @Autowired
    private GoodsSkuMapper goodsSkuMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 获取购物车列表
     *
     * @param cartGoodsType 购物车商品类型
     * @return
     */
    @Override
    public Map<String, Object> listCarts(Integer cartGoodsType) {
        Long userId = ConstantFactory.me().getUserId(); // 登录用户ID
        List<CartVO> cartList = shoppingCartDao.listCarts(userId, cartGoodsType);

        int goodsTotal = 0;                 // 商品数量
        List<CartGoodsVO> cartGoodsList;    // 购物车商品信息
        for (CartVO cartVO : cartList) {
            // 拼接店铺logo图片前缀
            cartVO.setShopLogo(ImageUtil.setImageURL(cartVO.getShopLogo()));

            cartGoodsList = cartVO.getCartGoodsList();
            for (CartGoodsVO cartGoods : cartGoodsList) {
                // 拼接商品图片前缀
                cartGoods.setGoodsImg(ImageUtil.setImageURL(cartGoods.getGoodsImg()));

                // 商品价格格式
                cartGoods.setPrice(AmountFormatUtil.amountFormat(cartGoods.getPrice()));

                // 商品规格信息json字符串的值转为","拼接
                if (ToolUtil.isNotEmpty(cartGoods.getSpecs())) {
                    cartGoods.setSpecs(GoodsSpecsUtils.joinGoodsSpecsValue(JSON.parseObject(cartGoods.getSpecs()), ","));
                }

                // 根据商品状态，库存数量和删除与否，获取商品状态信息
                cartGoods.setStatusName(
                        getGoodsStatusName(cartGoods.getStatus(), cartGoods.getStock(), cartGoods.getMainGoodsDel(), cartGoods.getSkuGoodsDel()));
            }
            // 累加商品数量
            goodsTotal += cartGoodsList.size();
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("cartList", cartList);
        resultMap.put("goodsTotal", goodsTotal);
        return resultMap;
    }

    /**
     * 新增商品到购物车
     *
     * @param goodsId 商品ID
     * @param skuId 规格商品ID
     * @param num 数量
     */
    @Override
    public void add(Long goodsId, Long skuId, Integer num) {
        if (null == goodsId) {
            throw new BussinessException(BizExceptionEnum.GOODS_ID_IS_NULL);
        }

        // 商品数量必须大于0
        if (null == num || 0 >= num) {
            throw new BussinessException(BizExceptionEnum.GOODS_NUM_ERROR);
        }

        // 登录用户ID
        Long userId = ConstantFactory.me().getUserId();

        // 查询用户购物车数量，添加不得超过20
        Integer cartCount = shoppingCartMapper.selectCount(new EntityWrapper<ShoppingCart>().eq("user_id", userId));
        if (20 <= cartCount) {
            throw new BussinessException(BizExceptionEnum.SHOPPING_CART_COUNT_IS_MAX_LIMIT);
        }

        // 查询该商品是否存在规格商品
        List<GoodsSku> goodsSkuList = goodsSkuMapper.selectList(
                new EntityWrapper<GoodsSku>()
                        .eq("goods_id", goodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode()));
        if (null != goodsSkuList && 0 < goodsSkuList.size()) {
            // 存在规格商品，skuId不能为空
            if (null == skuId) {
                throw new BussinessException(BizExceptionEnum.GOODS_SKU_ID_IS_NULL);
            }
        }

        // 校验商品状态和库存数量，并返回店铺ID和库存数量
        CartAddDTO cartAddDTO = checkGoodsAndStock(goodsId, skuId, num);

        // 根据goodsId和skuId获取购物车中商品信息
        ShoppingCart cart = getCartByGoodsIdAndSkuId(goodsId, skuId);
        if (null == cart) {
            /**
             * 商品不存在于购物车，进行新增操作
             */
            ShoppingCart newCart = new ShoppingCart();
            newCart.setUserId(userId);                          // 登录用户ID
            newCart.setSkuId(skuId);                            // 规格商品ID
            newCart.setGoodsId(goodsId);                        // 商品ID
            newCart.setShopId(cartAddDTO.getShopId());          // 店铺ID
            newCart.setNum(num);                                // 数量
            newCart.setCreatedTime(new Date());                 // 创建时间
            newCart.insert();
        } else {
            /**
             * 商品存在于购物车，则增加数量
             */
            // 判断库存数量是否大于等于购物车中数量 + 新增数量
            Integer cartNum = cart.getNum() + num;
            if (cartAddDTO.getStock() < cartNum) {
                throw new BussinessException(BizExceptionEnum.GOODS_SKU_STOCK_IS_NOT_ENOUGH);
            }
            cart.setNum(cartNum);
            cart.updateById();
        }
    }

    /**
     * 购物车指定商品数量增加或减少
     *
     * @param cartId 购物车ID
     * @param num 增加或减少数量（正数表示增加，负数表示减少）
     */
    @Override
    public void addOrMinus(Long cartId, Integer num) {
        // 数量为0，不做操作
        if (0 == num) {
            return ;
        }

        // 校验购物车ID
        ShoppingCart cart = checkCartId(cartId);

        // 变化后购物车中数量
        Integer cartNum = cart.getNum() + num;

        // 购物车数量小于等于0，直接删除该购物车商品
        if (0 >= cartNum) {
            cart.deleteById();
        }

        // 校验商品状态和库存数量
        checkGoodsAndStock(cart.getGoodsId(), cart.getSkuId(), cartNum);

        // 更新购物车商品数量
        cart.setNum(cartNum);
        cart.updateById();
    }

    /**
     * 修改指定购物车商品数量
     *
     * @param cartId 购物车商品ID
     * @param num 数量
     */
    @Override
    public void updateNum(Long cartId, Integer num) {
        // 商品数量必须大于0
        if (null == num || 0 >= num) {
            throw new BussinessException(BizExceptionEnum.GOODS_NUM_ERROR);
        }

        // 校验购物车ID
        ShoppingCart cart = checkCartId(cartId);

        // 校验商品状态和库存数量
        checkGoodsAndStock(cart.getGoodsId(), cart.getSkuId(), num);

        // 更新购物车商品数量
        cart.setNum(num);
        cart.updateById();
    }

    /**
     * 删除购物车商品@
     *
     * @param cartIdList 购物车商品ID集合
     */
    @Override
    public void delete(List<Long> cartIdList) {
        // 登录用户ID
        Long userId = ConstantFactory.me().getUserId();
        shoppingCartDao.batchDeleteCarts(cartIdList, userId);
    }

    /**
     * 清空购物车失效商品
     */
    @Override
    public void clean() {
        // 登录用户ID
        Long userId = ConstantFactory.me().getUserId();

        // 查询已失效商品的购物车ID列表
        List<Long> cartIdList = shoppingCartDao.listExpireCartIds(userId);

        // 批量删除
        delete(cartIdList);
    }

    /**
     * 根据商品状态和库存数量获取商品状态名称
     *
     * @param goodsStatus 商品状态（1-正常；2-下架；）
     * @param stock 库存数量
     * @param mainGoodsDel 主商品是否删除（0-否；1-是；）
     * @param skuGoodsDel 规格商品是否删除（0-否；1-是；）
     * @return
     */
    private String getGoodsStatusName(Integer goodsStatus, Integer stock, Integer mainGoodsDel, Integer skuGoodsDel) {
        if (0 >= stock) {
            return "商品已售罄";
        } else if (GoodsStatus.OFFSHELF.getCode() == goodsStatus
                || IsDeleted.DELETED.getCode() == mainGoodsDel
                || IsDeleted.DELETED.getCode() == skuGoodsDel) {
            return "商品未上架";
        }
        return "";
    }

    /**
     * 校验商品状态和库存数量，并返回店铺ID和库存数量
     *
     * @param goodsId 商品ID
     * @param skuId 规格商品ID
     * @param num 数量
     * @return
     */
    private CartAddDTO checkGoodsAndStock(Long goodsId, Long skuId, Integer num) {
        // 获取商品信息
        Goods goods = goodsMapper.selectById(goodsId);

        // 判断商品是否上架状态
        if (null == goods || GoodsStatus.NORMAL.getCode() != goods.getStatus()) {
            throw new BussinessException(BizExceptionEnum.GOODS_STATUS_IS_OFFSHELF);
        }

        // 校验数量
        Integer stock = goods.getStock();
        if (null != skuId) {
            // 存在规格商品，则库存数量为该规格商品库存数量
            GoodsSku goodsSku = new GoodsSku();
            goodsSku.setId(skuId);
            goodsSku.setGoodsId(goodsId);
            goodsSku.setIsDeleted(IsDeleted.NORMAL.getCode());
            goodsSku = goodsSkuMapper.selectOne(goodsSku);
            if (null == goodsSku) {
                throw new BussinessException(BizExceptionEnum.GOODS_SKU_IS_NOT_EXIST);
            }
            stock = goodsSku.getStock();
        }
        if (stock < num) {
            throw new BussinessException(BizExceptionEnum.GOODS_SKU_STOCK_IS_NOT_ENOUGH);
        }

        CartAddDTO cartAddDTO = new CartAddDTO();
        cartAddDTO.setShopId(goods.getShopId());
        cartAddDTO.setStock(stock);
        return cartAddDTO;
    }

    /**
     * 根据商品ID和规格商品ID获取购物车信息
     *
     * @param goodsId 商品ID
     * @param skuId 规格商品ID
     * @return
     */
    private ShoppingCart getCartByGoodsIdAndSkuId(Long goodsId, Long skuId) {
        Wrapper<ShoppingCart> query = new EntityWrapper<ShoppingCart>()
                .eq("goods_id", goodsId)
                .eq("user_id", ConstantFactory.me().getUserId());
        if (null != skuId) {
            query.eq("sku_id", skuId);
        }
        List<ShoppingCart> cartList = shoppingCartMapper.selectList(query);
        if (null == cartList || 0 == cartList.size()) {
            return null;
        }
        return cartList.get(0);
    }

    /**
     * 校验购物车ID
     *
     * @param cartId 购物车ID
     * @return
     */
    private ShoppingCart checkCartId(Long cartId) {
        if (ToolUtil.isEmpty(cartId)) {
            throw new BussinessException(BizExceptionEnum.SHOPPING_CART_ID_IS_NULL);
        }
        ShoppingCart cart = shoppingCartMapper.selectById(cartId);
        if (cart == null) {
            throw new BussinessException(BizExceptionEnum.SHOPPING_CART_IS_NOT_EXIST);
        }
        return cart;
    }
}
