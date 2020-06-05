package cn.ibdsr.web.modular.shop.order.service.impl;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.CartGoodsType;
import cn.ibdsr.web.common.constant.state.DeliveryStatus;
import cn.ibdsr.web.common.constant.state.DeliveryType;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.constant.state.IsEvaluated;
import cn.ibdsr.web.common.constant.state.RefundStatus;
import cn.ibdsr.web.common.constant.state.RefundType;
import cn.ibdsr.web.common.constant.state.ServiceStatus;
import cn.ibdsr.web.common.constant.state.ShopOrderStatus;
import cn.ibdsr.web.common.constant.state.ShopStatus;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.GoodsEvaluateMapper;
import cn.ibdsr.web.common.persistence.dao.GoodsMapper;
import cn.ibdsr.web.common.persistence.dao.GoodsSkuMapper;
import cn.ibdsr.web.common.persistence.dao.ShopMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderGoodsMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderRefundLogisticsMapper;
import cn.ibdsr.web.common.persistence.dao.ShopOrderRefundMapper;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.model.Goods;
import cn.ibdsr.web.common.persistence.model.GoodsEvaluate;
import cn.ibdsr.web.common.persistence.model.GoodsSku;
import cn.ibdsr.web.common.persistence.model.Shop;
import cn.ibdsr.web.common.persistence.model.ShopOrder;
import cn.ibdsr.web.common.persistence.model.ShopOrderGoods;
import cn.ibdsr.web.common.persistence.model.ShopOrderRefund;
import cn.ibdsr.web.common.persistence.model.ShopOrderRefundLogistics;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.core.mq.rabbitmq.MessageSenderTask;
import cn.ibdsr.web.core.util.AmountFormatUtil;
import cn.ibdsr.web.core.util.DateUtils;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.shop.goods.service.impl.ShoppingCartServiceImpl;
import cn.ibdsr.web.modular.shop.order.dao.OrderDao;
import cn.ibdsr.web.modular.shop.order.service.IOrderService;
import cn.ibdsr.web.modular.shop.order.transfer.DeliveryInfoVO;
import cn.ibdsr.web.modular.shop.order.transfer.LogisticsInfoVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderDetailVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderGoodsVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderVO;
import cn.ibdsr.web.modular.shop.order.transfer.RefundOrderVO;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
  * @Description 订单ServiceImpl
  * @Version V1.0
  * @CreateDate 2019-03-20 08:56:31
  *
  * Date           Author               Description
  * ------------------------------------------------------
  * 2019/03/20    Wujiayun            类说明
  */
@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Autowired
    private GoodsEvaluateMapper goodsEvaluateMapper;

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    @Autowired
    private ShopOrderRefundMapper shopOrderRefundMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private ShopOrderRefundLogisticsMapper shopOrderRefundLogisticsMapper;

    @Autowired
    private MessageSenderTask messageSenderTask;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsSkuMapper goodsSkuMapper;

    /**
     * 获取订单列表
     *
     * @param orderStatus 查看订单的状态
     * @return
     */
    @Override
    public List<OrderVO> list(Page<OrderVO> page, int orderStatus){
        Long userId = ConstantFactory.me().getUserId();
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER);
        }
        User user = userMapper.selectById(userId);
        if (user == null){
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);
        }
        if (orderStatus != -1 && orderStatus != 1 && orderStatus != 2 &&
                orderStatus != 3 && orderStatus !=4 ){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_STATUS_IS_NOT_EXIST);
        }
        List<OrderVO> orderVOList = orderDao.list(page, orderStatus, userId);
        if (orderVOList == null || ToolUtil.isEmpty(orderVOList) ){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }
        for (OrderVO orderVO: orderVOList){
            orderVO.setShopLogo(ImageUtil.setImageURL(orderVO.getShopLogo()));
            orderVO.setOrderPrice(AmountFormatUtil.amountFormat(orderVO.getOrderPrice()));
            orderVO.setStatusName(ShopOrderStatus.valueOf(orderVO.getStatus()));
            orderVO.setExpressFee(AmountFormatUtil.amountFormat(orderVO.getExpressFee()));
            for (OrderGoodsVO orderGoodsVO: orderVO.getGoods()){
                orderGoodsVO.setGoodsImg(ImageUtil.setImageURL(orderGoodsVO.getGoodsImg()));
                orderGoodsVO.setUnitPrice(AmountFormatUtil.amountFormat(orderGoodsVO.getUnitPrice()));
                orderGoodsVO.setDeliveryStatusName(DeliveryStatus.valueOf(orderGoodsVO.getDeliveryStatus()));
                orderGoodsVO.setServiceStatusName(ServiceStatus.valueOf(orderGoodsVO.getServiceStatus()));
            }
        }
        return orderVOList;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    public void cancel(Long orderId){
        if (ToolUtil.isEmpty(orderId)){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ID_IS_NULL);
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }

        List<ShopOrderGoods> shopOrderGoodsList = shopOrderGoodsMapper.selectList(
                new EntityWrapper<ShopOrderGoods>()
                        .eq("order_id",orderId)
                        .eq("is_deleted",IsDeleted.NORMAL.getCode())
        );
        if (shopOrderGoodsList == null || ToolUtil.isEmpty(shopOrderGoodsList) ){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_IS_NOT_EXIST);
        }
        Long userId = ConstantFactory.me().getUserId();
        Date now = new Date();
        for (ShopOrderGoods r: shopOrderGoodsList){
            r.setDeliveryStatus(DeliveryStatus.NULL.getCode());
            r.updateById();

            //订单取消，库存返还
            if (r.getSkuId() == null) {
                //无规格商品
                Goods param = goodsMapper.selectById(r.getGoodsId());
                if (param != null) {
                    Goods goods = new Goods();
                    goods.setId(r.getGoodsId());
                    goods.setStock(param.getStock() + r.getNums());
                    goods.setModifiedUser(userId);
                    goods.setModifiedTime(now);
                    goods.updateById();
                }
            } else {
                //有规格商品
                GoodsSku param = goodsSkuMapper.selectById(r.getSkuId());
                if (param != null) {
                    GoodsSku goodsSku = new GoodsSku();
                    goodsSku.setId(r.getSkuId());
                    goodsSku.setStock(param.getStock() + r.getNums());
                    goodsSku.setModifiedUser(userId);
                    goodsSku.setModifiedTime(now);
                    goodsSku.updateById();
                }
            }
        }
        shopOrder.setCancelTime(new Date());
        shopOrder.setCancelRemark("买家主动取消");
        shopOrder.setModifiedUser(ConstantFactory.me().getUserId());
        shopOrder.setModifiedTime(new Date());
        shopOrder.setStatus(ShopOrderStatus.CANCEL.getCode());
        shopOrder.updateById();

    }
    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    public void confirm(Long orderId){
        if (ToolUtil.isEmpty(orderId)){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ID_IS_NULL);
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }
        List<ShopOrderGoods> shopOrderGoodsList = shopOrderGoodsMapper.selectList(
                new EntityWrapper<ShopOrderGoods>()
                        .eq("order_id",orderId)
                        .eq("is_deleted",IsDeleted.NORMAL.getCode())
        );
        if (shopOrderGoodsList == null || ToolUtil.isEmpty(shopOrderGoodsList) ){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_GOODS_IS_NOT_EXIST);
        }
        for (ShopOrderGoods r: shopOrderGoodsList){
            r.setReceiveTime(new Date());
            r.setDeliveryStatus(DeliveryStatus.RECEIVED.getCode());
            r.updateById();
        }
        shopOrder.setModifiedUser(ConstantFactory.me().getUserId());
        shopOrder.setModifiedTime(new Date());
        shopOrder.setStatus(ShopOrderStatus.RECEIVED.getCode());
        shopOrder.updateById();

        // 放入延时消息队列，处理订单7天内未发起售后，自动结算
        messageSenderTask.sendMsgOfSettleOrder(shopOrder.getId());
    }

    /**
     * 订单详情
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    public List<OrderDetailVO> orderDetailList(Long orderId){
        if (ToolUtil.isEmpty(orderId)){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ID_IS_NULL);
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }
        List<OrderDetailVO> orderDetailVOList = orderDao.orderDetailList(orderId);
        for (OrderDetailVO orderDetailVO: orderDetailVOList){
            orderDetailVO.setShopLogo(ImageUtil.setImageURL(orderDetailVO.getShopLogo()));
            orderDetailVO.setOrderPrice(AmountFormatUtil.amountFormat(orderDetailVO.getOrderPrice()));
            orderDetailVO.setDeliveryTypeName(DeliveryType.valueOf(orderDetailVO.getDeliveryType()));
            orderDetailVO.setStatusName(ShopOrderStatus.valueOf(orderDetailVO.getStatus()));
            orderDetailVO.setExpressFee(AmountFormatUtil.amountFormat(orderDetailVO.getExpressFee()));
            if (ShopOrderStatus.WAIT_DELIVERY.getCode() == orderDetailVO.getStatus() && !ToolUtil.isEmpty(orderDetailVO.getDeliveryTime())){
                orderDetailVO.setDeliveryTime(null);
            }
            if (ShopOrderStatus.WAIT_PAY.getCode() == orderDetailVO.getStatus()){
                //用户下单时间
                Date userOrderTime = shopOrder.getCreatedTime();
                //System.out.println(DateUtils.getFailTime(userOrderTime,Const.ORDER_OUT_TIME));
                orderDetailVO.setOffTime(DateUtils.getDateTimeDiff(new Date(), DateUtils.getFailTime(userOrderTime, Const.SHOP_ORDER_OUT_TIME)));
            }
            for (OrderGoodsVO orderGoodsVO: orderDetailVO.getGoods()){
                orderGoodsVO.setGoodsImg(ImageUtil.setImageURL(orderGoodsVO.getGoodsImg()));
                orderGoodsVO.setUnitPrice(AmountFormatUtil.amountFormat(orderGoodsVO.getUnitPrice()));
                orderGoodsVO.setDeliveryStatusName(DeliveryStatus.valueOf(orderGoodsVO.getDeliveryStatus()));
                orderGoodsVO.setServiceStatusName(ServiceStatus.valueOf(orderGoodsVO.getServiceStatus()));
            }

        }
        return orderDetailVOList;
    }
    /**
     * 查看物流
     *
     * @param orderId 订单ID
     * @return
     */
    @Override
    public List<Map<String, Object>> logisticsInfo(Long orderId){
        if (ToolUtil.isEmpty(orderId)){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ID_IS_NULL);
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }
        List<LogisticsInfoVO> logisticsInfoVOList = orderDao.logisticsInfo(orderId);

        for (LogisticsInfoVO logisticsInfoVO: logisticsInfoVOList){
            logisticsInfoVO.setGoodsImg(ImageUtil.setImageURL(logisticsInfoVO.getGoodsImg()));
            logisticsInfoVO.setDeliveryStatusName(DeliveryStatus.valueOf(logisticsInfoVO.getDeliveryStatus()));
            logisticsInfoVO.setServiceStatusName(ServiceStatus.valueOf(logisticsInfoVO.getServiceStatus()));
            logisticsInfoVO.setUnitPrice(AmountFormatUtil.amountFormat(logisticsInfoVO.getUnitPrice()));

        }
        List<Map<String, Object>> logisticsInfo = listConvert(logisticsInfoVOList);

        for (Map<String, Object> logistics:logisticsInfo){
            DeliveryInfoVO deliveryInfo = new DeliveryInfoVO();
            DeliveryInfoVO[] de = new DeliveryInfoVO[1];
            de[0] = deliveryInfo;
            logistics.put("deliveryInfo",de);


        }
        return logisticsInfo;
    }


    public <T> List<Map<String,Object>> listConvert(List<T> list){
        List<Map<String,Object>> list_map=new ArrayList<Map<String,Object>>();
        try {
            for (T t : list) {
                Field[] fields=t.getClass().getDeclaredFields();
                Map<String, Object> m = new HashMap<String, Object>();
                for(Field field:fields){
                    String keyName=field.getName();
                    PropertyDescriptor pd = new PropertyDescriptor(keyName,t.getClass());
                    Method getMethod = pd.getReadMethod();// 获得getter方法
                    Object o = getMethod.invoke(t);// 执行get方法返回一个Object
                    m.put(keyName, o);
                }
                list_map.add(m);
            }
            return list_map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 再来一单
     *
     * @param orderId 订单ID
     *
     */
    @Override
    public void addToCar(Long orderId){
        if (ToolUtil.isEmpty(orderId)){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ID_IS_NULL);
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }
        Shop shop = shopMapper.selectById(shopOrder.getShopId());
        if (shop.getStatus() == ShopStatus.OFFSHELF.getCode()){
            throw new BussinessException(BizExceptionEnum.SHOP_IS_CLOSE);
        }
        List<ShopOrderGoods> shopOrderGoodsList = shopOrderGoodsMapper.selectList(
                new EntityWrapper<ShopOrderGoods>()
                        .eq("order_id",orderId)
                        .eq("is_deleted",IsDeleted.NORMAL.getCode())
                        .setSqlSelect("goods_id","sku_id","nums")
        );
        for(ShopOrderGoods r: shopOrderGoodsList){
            //System.out.println(r.getSkuId());
            shoppingCartService.add(r.getGoodsId(),r.getSkuId(),r.getNums());
        }
    }

    /**
     * 统计订单数量
     *
     */
    @Override
    public Map<String,Object> num(){
        Long userId = ConstantFactory.me().getUserId();
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER);
        }
        User user = userMapper.selectById(userId);
        if (user == null){
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);
        }
        int waitPayOrder = shopOrders(ShopOrderStatus.WAIT_PAY.getCode(),userId).size();
        int waitDelivery = shopOrders(ShopOrderStatus.WAIT_DELIVERY.getCode(),userId).size();
        int waitReceive = shopOrders(ShopOrderStatus.WAIT_RECEIVE.getCode(),userId).size();
        int waitEvaluate = listWaitEvaluateOrders(userId).size();
        int closedOrder = shopOrders(ShopOrderStatus.CLOSED.getCode(), userId).size();
        int serviceOrder = shopOrders(ShopOrderStatus.SERVICE.getCode(),userId).size();
        int serviceClosedOrder = closedOrder + serviceOrder;

        Object cart = shopCartNum(CartGoodsType.NORMAL.getCode());
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("waitPay", waitPayOrder);
        returnMap.put("waitDelivery", waitDelivery);
        returnMap.put("waitReceive", waitReceive);
        returnMap.put("waitEvaluate", waitEvaluate);
        returnMap.put("cancelService", serviceClosedOrder);
        returnMap.put("shopCart",cart);

        return returnMap;
    }

    /**
     * 计算有效购物车商品数量
     *
     */
    private Object shopCartNum(Integer cartGoodsType){
        Map<String, Object> resultMap = shoppingCartService.listCarts(cartGoodsType);
        return resultMap.get("goodsTotal");
    }
    /**
     * 查看订单评价
     */
    @Override
    public SuccessDataTip evaluate(Long orderId){
        if (ToolUtil.isEmpty(orderId)){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ID_IS_NULL);
        }
        ShopOrder shopOrder = shopOrderMapper.selectById(orderId);
        if (shopOrder == null){
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_IS_NOT_EXIST);
        }
        Wrapper<ShopOrderGoods> shopOrderGoodsWrapper=new EntityWrapper<>();
        shopOrderGoodsWrapper.setSqlSelect("id orderGoodsId","goods_img goodsImg","goods_name goodsName","goods_specs goodsSpecs");
        shopOrderGoodsWrapper.eq("order_id",orderId);
        List<Map<String ,Object>> orderGoodsList=shopOrderGoodsMapper.selectMaps(shopOrderGoodsWrapper);
        for(Map<String ,Object> orderGoods:orderGoodsList) {
            orderGoods.put("goodsImg",ImageUtil.setImageURL(String.valueOf(orderGoods.get("goodsImg"))));
            Wrapper<GoodsEvaluate> goodsEvaluateWrapper= new EntityWrapper<>();
            goodsEvaluateWrapper.setSqlSelect("goods_score goodsScore", "content evaluateContent","imgs evaluateImgs","service_score serviceScore","express_score expressScore");
            goodsEvaluateWrapper.eq("order_goods_id", orderGoods.get("orderGoodsId"));
            List<Map<String, Object>> evaluateList = goodsEvaluateMapper.selectMaps(goodsEvaluateWrapper);
            for (Map<String ,Object> evaluate:evaluateList){
                evaluate.put("evaluateImgs",ImageUtil.setImageStrURL(String.valueOf(evaluate.get("evaluateImgs"))));
            }
            orderGoods.put("evaluateInfo",evaluateList);
        }
        return new SuccessDataTip(orderGoodsList);
    }

    private List<ShopOrder> shopOrders(int status,Long userId){
        List<ShopOrder> shopOrderList = shopOrderMapper.selectList(
                new EntityWrapper<ShopOrder>()
                        .eq("status",status)
                        .eq("user_id",userId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
        );
        return shopOrderList;
    }

    /**
     * 查询待评价的订单集合
     *
     * @param userId
     * @return
     */
    private List<ShopOrder> listWaitEvaluateOrders(Long userId) {
        List<ShopOrder> shopOrderList = shopOrderMapper.selectList(
                new EntityWrapper<ShopOrder>()
                        .eq("is_evaluated", IsEvaluated.NO.getCode())
                        .eq("status",ShopOrderStatus.RECEIVED.getCode())
                        .eq("user_id", userId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
        );
        return shopOrderList;
    }

   /**
     * 售后子订单
     */
    @Override
    public List<RefundOrderVO> serviceOrder(Page<RefundOrderVO> page){
        Long userId = ConstantFactory.me().getUserId();
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER);
        }
        User user = userMapper.selectById(userId);
        if (user == null){
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);
        }
        List<RefundOrderVO> List = orderDao.serviceOrderlist(page, userId);
        for (RefundOrderVO refundOrderVO:List){
            ShopOrderRefund refund = getRefundByOrderGoodsId(refundOrderVO.getOrderGoodsId());
            refundOrderVO.setRefundStatus(refund.getStatus());
            refundOrderVO.setRefundType(RefundType.valueOf(refund.getType()));
            refundOrderVO.setReviewRemark(refund.getReviewRemark());
            refundOrderVO.setRevokeReason(refund.getRevokeReason());
            Shop shop = shopMapper.selectById(refundOrderVO.getShopId());
            refundOrderVO.setShopLogo(ImageUtil.setImageURL(shop.getLogo()));
            refundOrderVO.setShopName(shop.getFrontName());
            ShopOrderGoods shopOrderGoods = shopOrderGoodsMapper.selectById(refundOrderVO.getOrderGoodsId());
            refundOrderVO.setGoodsId(shopOrderGoods.getGoodsId());
            refundOrderVO.setGoodsImg(ImageUtil.setImageURL(shopOrderGoods.getGoodsImg()));
            refundOrderVO.setGoodsName(shopOrderGoods.getGoodsName());
            refundOrderVO.setGoodsSpecs(shopOrderGoods.getGoodsSpecs());
            refundOrderVO.setNum(shopOrderGoods.getNums());

            // 定义计算截止时间相关参数
            Date userApplyTime = refund.getCreatedTime();           // 用户申请时间
            Date shopReviewTime = refund.getReviewTime();           // 店铺审核时间
            Date userDeliveryTime = null;                           // 用户发货时间

            // 退款类型为退货退款，查询退货物流信息
            if (RefundType.AMOUNT_GOODS.getCode() == refund.getType()) {
                ShopOrderRefundLogistics refundLogistics = new ShopOrderRefundLogistics();
                refundLogistics.setRefundId(refund.getId());
                refundLogistics.setIsDeleted(IsDeleted.NORMAL.getCode());
                refundLogistics = shopOrderRefundLogisticsMapper.selectOne(refundLogistics);
                if (null != refundLogistics) {
                    // 用户收货时间，计算截止时间使用
                    userDeliveryTime = refundLogistics.getDeliveryTime();
                    refundOrderVO.setLogisticsStatus(refundLogistics.getStatus());
                }
            }

            // 计算退款截止时间：x天x时x分
            refundOrderVO.setOffTime(caluateOffTime(refund.getStatus(), userApplyTime, shopReviewTime, userDeliveryTime));
        }
        return List;

    }

    /**
     * 根据订单商品ID查询退款订单信息
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    private ShopOrderRefund getRefundByOrderGoodsId(Long orderGoodsId) {
        List<ShopOrderRefund> orderRefundList = shopOrderRefundMapper.selectList(
                new EntityWrapper<ShopOrderRefund>()
                        .eq("order_goods_id", orderGoodsId)
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .orderBy("created_time", false)
                        .last("LIMIT 1"));
        if (null == orderRefundList || 0 == orderRefundList.size()) {
            throw new BussinessException(BizExceptionEnum.REFUND_ORDER_IS_NOT_EXIST);
        }
        return orderRefundList.get(0);
    }

    /**
     * 计算退款处理截止时间
     *
     * @param refundStatus 退款状态
     * @param userApplyTime 用户申请时间
     * @param shopReviewTime 店铺审核时间
     * @param userDeliveryTime 用户发货时间
     * @return
     */
    private String caluateOffTime(Integer refundStatus, Date userApplyTime, Date shopReviewTime, Date userDeliveryTime) {
        // 退款状态不为待审核和审核通过状态，不计算截止时间
        if (RefundStatus.WAIT_REVIEW.getCode() != refundStatus && RefundStatus.PASS.getCode() != refundStatus) {
            return null;
        }
        Date fetureTime = null;
        if (null != userDeliveryTime) {
            fetureTime = DateUtils.getFetureTime(userDeliveryTime, Const.HANDLE_DAY_LIMIT);
        } else if (null != shopReviewTime) {
            fetureTime = DateUtils.getFetureTime(shopReviewTime, Const.HANDLE_DAY_LIMIT);
        } else if (null != userApplyTime) {
            fetureTime = DateUtils.getFetureTime(userApplyTime, Const.HANDLE_DAY_LIMIT);
        }
        return DateUtils.getDateDiff(new Date(), fetureTime);
    }

}
