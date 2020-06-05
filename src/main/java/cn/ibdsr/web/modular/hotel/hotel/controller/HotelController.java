package cn.ibdsr.web.modular.hotel.hotel.controller;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.modular.hotel.hotel.service.HotelService;
import com.alibaba.fastjson.JSONObject;
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
 * @CreateDate: 2019/5/9 13:41
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/9 xujincai init
 */
@RestController
@RequestMapping(value = "hotel/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    /**
     * 分页查询酒店
     *
     * @param offset       offset
     * @param limit        limit
     * @param checkInDate  入住日期
     * @param checkOutDate 离店日期
     * @param order        排序，1距离升序，2距离降序，3评分升序，4评分降序，5价格升序，6价格降序
     * @param key          查询关键词
     * @return JSONObject
     */
    @RequestMapping(value = "pagehotel")
    @WinXinLogin
    public SuccessDataTip pageHotel(@RequestParam int offset, @RequestParam int limit, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate, Integer order, String key) {
        return hotelService.pageHotel(offset, limit, checkInDate, checkOutDate, order, key);
    }

    /**
     * 查询酒店
     *
     * @param hotelId 酒店主键ID
     * @return SuccessDataTip
     */
    @RequestMapping(value = "hotel")
    @WinXinLogin
    public SuccessDataTip hotel(@RequestParam int hotelId) {
        return hotelService.hotel(hotelId);
    }

    /**
     * 分页查询评价
     *
     * @param offset  offset
     * @param limit   limit
     * @param hotelId 酒店ID
     * @return JSONObject
     */
    @RequestMapping(value = "pageevaluate")
    @WinXinLogin
    public JSONObject pageEvaluate(@RequestParam int offset, @RequestParam int limit, @RequestParam int hotelId) {
        return hotelService.pageEvaluate(offset, limit, hotelId);
    }

    /**
     * 查询房间列表
     *
     * @param hotelId      酒店ID
     * @param checkInDate  入住时间
     * @param checkOutDate 离店时间
     * @param order        可订？0不可1可
     * @param breakfast    早餐？0无1有
     * @param window       窗？0无1有
     * @param canCancel    免费取消？0否1可
     * @return SuccessDataTip
     */
    @RequestMapping(value = "listroom")
    @WinXinLogin
    public SuccessDataTip listRoom(@RequestParam int hotelId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate, Integer order, Integer breakfast, Integer window, Integer canCancel) {
        return hotelService.listRoom(hotelId, checkInDate, checkOutDate, order, breakfast, window, canCancel);
    }

    /**
     * 房间
     *
     * @param hotelId 酒店ID
     * @param roomId  房间ID
     * @return SuccessDataTip
     */
    @RequestMapping(value = "room")
    @WinXinLogin
    public SuccessDataTip room(@RequestParam int hotelId, @RequestParam int roomId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate) {
        return hotelService.room(hotelId, roomId, checkInDate, checkOutDate);
    }

    /**
     * 查询用户关联的游客
     *
     * @return SuccessDataTip
     */
    @RequestMapping(value = "listtourist")
    @WinXinLogin
    public SuccessDataTip listTourist() {
        return hotelService.listTourist();
    }

}
