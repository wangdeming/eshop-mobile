package cn.ibdsr.web.modular.shop.order.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.constant.factory.PageFactory;
import cn.ibdsr.web.modular.shop.order.service.IOrderService;
import cn.ibdsr.web.modular.shop.order.transfer.OrderDetailVO;
import cn.ibdsr.web.modular.shop.order.transfer.OrderVO;
import cn.ibdsr.web.modular.shop.order.transfer.RefundOrderVO;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
  * @Description 订单控制器
  * @Version V1.0
  * @CreateDate 2019-03-20 08:56:31
  *
  * Date           Author               Description
  * ------------------------------------------------------
  * 2019/03/20    Wujiayun            类说明
  */
@RestController
@RequestMapping("shop/personal/order")
public class OrderController extends BaseController {

    private String PREFIX = "/shop/personal/order/";

    @Autowired
    private IOrderService iOrderService;

    /**
     * 获取订单列表
     *
     * @param orderStatus 查看订单的状态
     * @return
     */
    @RequestMapping(value = "list")
    @WinXinLogin
    public Object list(int orderStatus) {
        Page<OrderVO> page = new PageFactory<OrderVO>().defaultPage();
        List<OrderVO> orderList = iOrderService.list(page,orderStatus);
        page.setRecords(orderList);
        return new SuccessDataTip(super.packForBT(page));
    }


    /**
     * 确认收货
     *
     * @param orderId 订单ID
     * @return
     */
    @RequestMapping(value = "confirm")
    @WinXinLogin
    public Object confirm(Long orderId) {
        iOrderService.confirm(orderId);
        return SUCCESS_TIP;
    }

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @return
     */
    @RequestMapping(value = "cancel")
    @WinXinLogin
    public Object cancel(Long orderId) {
        iOrderService.cancel(orderId);
        return SUCCESS_TIP;
    }


    /**
     * 订单详情
     *
     * @param orderId 订单ID
     * @return
     */
    @RequestMapping(value = "detail")
    @WinXinLogin
    public Object detail(Long orderId) {
        List<OrderDetailVO> orderDetailList = iOrderService.orderDetailList(orderId);
        return new SuccessDataTip(orderDetailList);
    }

    /**
     * 查看物流
     *
     * @param orderId 订单ID
     * @return
     */
    @RequestMapping(value = "logistics")
    @WinXinLogin
    public Object logistics(Long orderId) {
        List<Map<String, Object>> logisticsInfo = iOrderService.logisticsInfo(orderId);
        return new SuccessDataTip(logisticsInfo);
    }

    /**
     * 再来一单
     *
     * @param orderId 订单ID
     *
     */
    @RequestMapping(value = "addToCar")
    @WinXinLogin
    public Object addToCar(Long orderId) {
        iOrderService.addToCar(orderId);
        return SUCCESS_TIP;
    }

    /**
     * 统计订单数量
     *
     */
    @RequestMapping(value = "num")
    @WinXinLogin
    public Object num() {
        Map<String, Object> orderNum = iOrderService.num();
        return new SuccessDataTip(orderNum);
    }

    /**
     * 查看订单评价
     */
    @WinXinLogin
    @RequestMapping(value = "evaluate")
    public SuccessDataTip evaluate(Long orderId) {
        return iOrderService.evaluate(orderId);
    }

   /**
     * 售后子订单
     */
   @RequestMapping(value = "serviceOrder")
   @WinXinLogin
   public SuccessDataTip serviceOrder() {
        Page<RefundOrderVO> page = new PageFactory<RefundOrderVO>().defaultPage();
        List<RefundOrderVO> serviceOrderList = iOrderService.serviceOrder(page);
        page.setRecords(serviceOrderList);
        return new SuccessDataTip(super.packForBT(page));
    }

}
