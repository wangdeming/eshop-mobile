package cn.ibdsr.web.modular.shop.order.dao;

import cn.ibdsr.web.modular.shop.order.transfer.LogisticsInfoVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderDetailVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderVO;
import cn.ibdsr.web.modular.shop.order.transfer.RefundOrderVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
  * @Description 订单Dao
  * @Version V1.0
  * @CreateDate 2019-03-20 08:56:31
  *
  * Date           Author               Description
  * ------------------------------------------------------
  * 2019/03/20    Wujiayun            类说明
  */
public interface OrderDao {


    /**
     * 获取订单列表
     *
     * @param orderStatus 查看订单的状态
     * @param userId 用户ID
     * @return
     */
     List<OrderVO> list(@Param("page") Page<OrderVO> page, @Param("orderStatus")int orderStatus, @Param("userId")Long userId);

    /**
     * 订单详情
     *
     * @param orderId 订单ID
     * @return
     */
     List<OrderDetailVO> orderDetailList(@Param("orderId")Long orderId);

    /**
     * 查看物流
     *
     * @param orderId 订单ID
     * @return
     */
     List<LogisticsInfoVO> logisticsInfo(@Param("orderId")Long orderId);

    /**
     * 售后子订单
     */
    List<RefundOrderVO> serviceOrderlist(@Param("page")Page<RefundOrderVO> page, @Param("userId")Long userId);
}
