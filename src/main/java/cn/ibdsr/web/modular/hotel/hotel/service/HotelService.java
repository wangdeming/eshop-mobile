package cn.ibdsr.web.modular.hotel.hotel.service;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import com.alibaba.fastjson.JSONObject;

import java.time.LocalDate;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/9 13:41
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/9 xujincai init
 */
public interface HotelService {
    SuccessDataTip pageHotel(int offset, int limit, LocalDate checkInDate, LocalDate checkOutDate, Integer order, String key);

    SuccessDataTip hotel(int hotelId);

    JSONObject pageEvaluate(int offset, int limit, int hotelId);

    SuccessDataTip listRoom(int hotelId, LocalDate checkInDate, LocalDate checkOutDate, Integer order, Integer breakfast, Integer window, Integer canCancel);

    SuccessDataTip room(int hotelId, int roomId, LocalDate checkInDate, LocalDate checkOutDate);

    SuccessDataTip listTourist();
}
