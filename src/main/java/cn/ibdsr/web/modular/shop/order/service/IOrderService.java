package cn.ibdsr.web.modular.shop.order.service;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.modular.shop.order.transfer.LogisticsInfoVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderDetailVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderVO;
import cn.ibdsr.web.modular.shop.order.transfer.RefundOrderVO;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;
import java.util.Map;

/**
  * @Description 订单Service
  * @Version V1.0
  * @CreateDate 2019-03-20 08:56:31
  *
  * Date           Author               Description
  * ------------------------------------------------------
  * 2019/03/20    Wujiayun            类说明
  */
public interface IOrderService {

 /**
  * 获取订单列表
  *
  * @param orderStatus 查看订单的状态
  * @return
  */
    List<OrderVO> list(Page<OrderVO> page, int orderStatus);
    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return
     */
    void cancel(Long orderId);

    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return
     */
    void confirm(Long orderId);
    /**
     * 订单详情
     *
     * @param orderId 订单ID
     * @return
     */
    List<OrderDetailVO> orderDetailList(Long orderId);

   /**
    * 查看物流
    *
    * @param orderId 订单ID
    * @return
    */
   List<Map<String, Object>> logisticsInfo(Long orderId);

   /**
    * 再来一单
    *
    * @param orderId 订单ID
    *
    */
    void addToCar(Long orderId);

    /**
     * 统计订单数量
     *
     */
    Map<String,Object> num();
    /**
     * 分页查看订单评价
     */
    SuccessDataTip evaluate(Long orderId);

    /**
     * 售后子订单
     */
    List<RefundOrderVO> serviceOrder(Page<RefundOrderVO> page);
}
