package cn.ibdsr.web.modular.hotel.order.service;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessMesTip;
import cn.ibdsr.web.common.persistence.model.HotelOrderEvaluate;

import java.time.LocalDate;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/13 16:53
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/13 xujincai init
 */
public interface HotelOrderService {
    SuccessDataTip list(int offset, int limit, Integer status);

    SuccessDataTip createOrder(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomNumber, String mobile, String orderUserListString, Integer payType);

    SuccessDataTip detail(String hotelOrderId);

    SuccessMesTip cancel(String hotelOrderId) throws Exception;

    SuccessMesTip delete(String hotelOrderId);

    SuccessMesTip evaluate(HotelOrderEvaluate hotelOrderEvaluate);
}
