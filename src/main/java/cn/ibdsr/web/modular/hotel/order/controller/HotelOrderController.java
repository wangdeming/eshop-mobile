package cn.ibdsr.web.modular.hotel.order.controller;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessMesTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.persistence.model.HotelOrderEvaluate;
import cn.ibdsr.web.modular.hotel.order.service.HotelOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/13 16:52
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/13 xujincai init
 */
@RestController
@RequestMapping(value = "hotel/order")
public class HotelOrderController {

    @Autowired
    private HotelOrderService hotelOrderService;

    /**
     * 分页查询订单列表
     *
     * @param offset offset
     * @param limit  limit
     * @param status 状态，1待付款，2待确认，3待使用，4已消费（待评价）
     * @return SuccessDataTip
     */
    @RequestMapping(value = "list")
    @WinXinLogin
    public SuccessDataTip list(@RequestParam int offset, @RequestParam int limit, Integer status) {
        return hotelOrderService.list(offset, limit, status);
    }

    /**
     * 创建订单
     *
     * @param roomId              房间ID
     * @param checkInDate         入住日期
     * @param checkOutDate        离店日期
     * @param roomNumber          所订房间数量
     * @param mobile              联系电话
     * @param orderUserListString 入住人List
     * @param payType             支付方式，微信支付方式值为1，支付宝支付方式值为2
     * @return SuccessDataTip
     * @throws Exception Exception
     */
    @RequestMapping(value = "createorder")
    @WinXinLogin
    public SuccessDataTip createOrder(@RequestParam Long roomId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate, @RequestParam Integer roomNumber, @RequestParam String mobile, @RequestParam String orderUserListString, @RequestParam Integer payType) {
        return hotelOrderService.createOrder(roomId, checkInDate, checkOutDate, roomNumber, mobile, orderUserListString, payType);
    }

    /**
     * 订单详情
     *
     * @param orderId 订单ID
     * @return SuccessDataTip
     */
    @RequestMapping(value = "detail")
    @WinXinLogin
    public SuccessDataTip detail(@RequestParam String orderId) {
        return hotelOrderService.detail(orderId);
    }

    /**
     * 订单取消
     *
     * @param orderId 订单ID
     * @return SuccessMesTip
     */
    @RequestMapping(value = "cancel")
    @WinXinLogin
    public SuccessMesTip cancel(String orderId) throws Exception {
        return hotelOrderService.cancel(orderId);
    }

    /**
     * 删除订单
     *
     * @param orderId 订单ID
     * @return SuccessMesTip
     */
    @RequestMapping(value = "delete")
    @WinXinLogin
    public SuccessMesTip delete(@RequestParam String orderId) {
        return hotelOrderService.delete(orderId);
    }

    /**
     * 订单评价
     *
     * @param hotelOrderEvaluate 评价信息
     * @return SuccessMesTip
     */
    @RequestMapping(value = "evaluate")
    @WinXinLogin
    public SuccessMesTip evaluate(HotelOrderEvaluate hotelOrderEvaluate) {
        return hotelOrderService.evaluate(hotelOrderEvaluate);
    }

}
