package cn.ibdsr.web.modular.hotel.hotel.service.impl;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.constant.state.RoomBreakfast;
import cn.ibdsr.web.common.constant.state.RoomBroadband;
import cn.ibdsr.web.common.constant.state.RoomCanCancel;
import cn.ibdsr.web.common.constant.state.RoomStatus;
import cn.ibdsr.web.common.constant.state.RoomWindow;
import cn.ibdsr.web.common.constant.state.ShopStatus;
import cn.ibdsr.web.common.constant.state.ShopType;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.HotelOrderEvaluateMapper;
import cn.ibdsr.web.common.persistence.dao.HotelOrderMapper;
import cn.ibdsr.web.common.persistence.dao.RoomImgMapper;
import cn.ibdsr.web.common.persistence.dao.RoomIntroMapper;
import cn.ibdsr.web.common.persistence.dao.RoomMapper;
import cn.ibdsr.web.common.persistence.dao.RoomSettingMapper;
import cn.ibdsr.web.common.persistence.dao.ShopMapper;
import cn.ibdsr.web.common.persistence.dao.UserTouristMapper;
import cn.ibdsr.web.common.persistence.model.HotelOrder;
import cn.ibdsr.web.common.persistence.model.HotelOrderEvaluate;
import cn.ibdsr.web.common.persistence.model.Room;
import cn.ibdsr.web.common.persistence.model.RoomImg;
import cn.ibdsr.web.common.persistence.model.RoomIntro;
import cn.ibdsr.web.common.persistence.model.RoomSetting;
import cn.ibdsr.web.common.persistence.model.Shop;
import cn.ibdsr.web.common.persistence.model.UserTourist;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.hotel.hotel.service.HotelService;
import cn.ibdsr.web.modular.shop.record.service.RecordService;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/9 13:42
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/9 xujincai init
 */
@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomImgMapper roomImgMapper;

    @Autowired
    private RoomIntroMapper roomIntroMapper;

    @Autowired
    private RoomSettingMapper roomSettingMapper;

    @Autowired
    private HotelOrderMapper hotelOrderMapper;

    @Autowired
    private HotelOrderEvaluateMapper hotelOrderEvaluateMapper;

    @Autowired
    private UserTouristMapper userTouristMapper;

    @Autowired
    private RecordService recordService;

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
    @Override
    public SuccessDataTip pageHotel(int offset, int limit, LocalDate checkInDate, LocalDate checkOutDate, Integer order, String key) {
        RowBounds rowBounds = new RowBounds(offset, limit);
        Wrapper<Shop> shopWrapper = new EntityWrapper<>();
        shopWrapper.setSqlSelect("id,cover,front_name,distance_lingshan," +
                "IFNULL((SELECT AVG(score) FROM hotel_order_evaluate WHERE shop_id=shop.id)," + Const.HOTEL_ORDER_EVALUATE_DEFAULT_SCORE + ") score," +
                "IFNULL((SELECT MIN(LEAST(rs.price,r.price)) FROM room_setting rs LEFT JOIN room r ON r.id=rs.room_id WHERE rs.shop_id=shop.id AND rs.date BETWEEN '" + checkInDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND '" + checkOutDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'),(SELECT MIN(price) FROM room WHERE shop_id=shop.id)) price")
                .eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("type", ShopType.HOTEL.getCode()).eq("status", ShopStatus.NORMAL.getCode())
                .exists("SELECT shop_id FROM room WHERE is_deleted = 0 AND `status` = 1 AND shop_id = shop.id");
        if (StringUtils.isNotBlank(key)) {
            shopWrapper.where("(front_name LIKE '%" + key + "%' OR address LIKE '%" + key + "%' " +
                    "OR (SELECT `name` FROM area WHERE id=province_id) LIKE '%" + key + "%' " +
                    "OR (SELECT `name` FROM area WHERE id=city_id) LIKE '%" + key + "%' " +
                    "OR (SELECT `name` FROM area WHERE id=district_id) LIKE '%" + key + "%' " +
                    "OR (SELECT `name` FROM area WHERE id=street_id) LIKE '%" + key + "%')");
        }
        if (order != null) {
            if (order == 1) {
                shopWrapper.orderBy("distance_lingshan", Boolean.TRUE);
            } else if (order == 2) {
                shopWrapper.orderBy("distance_lingshan", Boolean.FALSE);
            } else if (order == 3) {
                shopWrapper.orderBy("score", Boolean.TRUE);
            } else if (order == 4) {
                shopWrapper.orderBy("score", Boolean.FALSE);
            } else if (order == 5) {
                shopWrapper.orderBy("price", Boolean.TRUE);
            } else if (order == 6) {
                shopWrapper.orderBy("price", Boolean.FALSE);
            } else {
                shopWrapper.orderBy("distance_lingshan", Boolean.TRUE);
            }
        } else {
            shopWrapper.orderBy("distance_lingshan", Boolean.TRUE);
        }
        Integer total = shopMapper.selectCount(shopWrapper);
        List<Map<String, Object>> rows = shopMapper.selectMapsPage(rowBounds, shopWrapper);
        for (Map<String, Object> row : rows) {
            if (row.get("cover") != null && StringUtils.isNotBlank(row.get("cover").toString())) {
                row.put("cover", ImageUtil.PREFIX_IMAGE_URL + row.get("cover"));
            }
            row.put("score", new BigDecimal(row.get("score").toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
            if (row.get("distance_lingshan") != null) {
                if (new BigDecimal(row.get("distance_lingshan").toString()).compareTo(new BigDecimal(1000)) < 0) {
                    row.put("distance_lingshan", row.get("distance_lingshan") + "m");
                } else {
                    row.put("distance_lingshan", new BigDecimal(row.get("distance_lingshan").toString()).divide(new BigDecimal(1000), 1, BigDecimal.ROUND_HALF_UP).toPlainString() + "km");
                }
            }
            row.put("price", new BigDecimal(row.get("price").toString()).divide(new BigDecimal(100)));
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("total", total);
        returnJson.put("rows", rows);
        return new SuccessDataTip(returnJson);
    }

    /**
     * 查询酒店
     *
     * @param hotelId 酒店主键ID
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip hotel(int hotelId) {
        List<Shop> shopList = shopMapper.selectList(new EntityWrapper<Shop>()
                .setSqlSelect("id", "logo", "front_name", "office_phone", "office_telphone", "address", "longitude", "latitude", "distance_lingshan")
                .eq("is_deleted", IsDeleted.NORMAL.getCode())
                .eq("type", ShopType.HOTEL.getCode())
                .eq("status", ShopStatus.NORMAL.getCode())
                .eq("id", hotelId));
        JSONObject shop = new JSONObject();
        if (shopList.size() > 0) {
            shop = JSONObject.parseObject(JSONObject.toJSONString(shopList.get(0)));
            if (shop.get("distanceLingshan") != null) {
                if (shop.getBigDecimal("distanceLingshan").compareTo(new BigDecimal(1000)) < 0) {
                    shop.put("distanceLingshan", shop.get("distanceLingshan") + "m");
                } else {
                    shop.put("distanceLingshan", shop.getBigDecimal("distanceLingshan").divide(new BigDecimal(1000), 1, BigDecimal.ROUND_HALF_UP).toPlainString() + "km");
                }
            }
            if (StringUtils.isNotBlank(shop.getString("logo"))) {
                shop.put("logo", ImageUtil.PREFIX_IMAGE_URL + shop.getString("logo"));
            }
            Wrapper<HotelOrderEvaluate> hotelOrderEvaluateWrapper = new EntityWrapper<>();
            hotelOrderEvaluateWrapper.eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("shop_id", hotelId);
            Integer count = hotelOrderEvaluateMapper.selectCount(hotelOrderEvaluateWrapper);
            shop.put("evaluateCount", count);
            hotelOrderEvaluateWrapper.setSqlSelect("AVG(score) score");
            List<Map<String, Object>> hotelOrderEvaluateList = hotelOrderEvaluateMapper.selectMaps(hotelOrderEvaluateWrapper);
            if (hotelOrderEvaluateList.get(0) == null) {
                shop.put("score", Const.HOTEL_ORDER_EVALUATE_DEFAULT_SCORE);
            } else {
                shop.put("score", new BigDecimal(hotelOrderEvaluateList.get(0).get("score").toString()).setScale(1, BigDecimal.ROUND_HALF_UP));
            }
            shop.put("evaluateList", pageEvaluate(0, 3, hotelId).getJSONArray("rows"));
        }

        try {
            //增加访问记录和统计访问数据
            recordService.viewShop((long) hotelId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SuccessDataTip(shop);
    }

    @Override
    public JSONObject pageEvaluate(int offset, int limit, int hotelId) {
        RowBounds rowBounds = new RowBounds(offset, limit);
        Wrapper<HotelOrderEvaluate> hotelOrderEvaluateWrapper = new EntityWrapper<>();
        hotelOrderEvaluateWrapper.setSqlSelect("id", "order_id", "score", "content", "images", "DATE_FORMAT(created_time,'%Y-%m-%d') createdTime", "(SELECT nickname FROM `user` WHERE id=hotel_order_evaluate.user_id) nickname", "(SELECT CONCAT('" + ImageUtil.PREFIX_IMAGE_URL + "',avatar) FROM `user` WHERE id=hotel_order_evaluate.user_id) avatar").orderBy("created_time", Boolean.FALSE)
                .eq("is_deleted", IsDeleted.NORMAL.getCode())
                .eq("shop_id", hotelId)
                .orderBy("created_time", Boolean.FALSE);
        Integer total = hotelOrderEvaluateMapper.selectCount(hotelOrderEvaluateWrapper);
        List<Map<String, Object>> rows = hotelOrderEvaluateMapper.selectMapsPage(rowBounds, hotelOrderEvaluateWrapper);
        for (Map<String, Object> row : rows) {
            if (row.get("images") != null) {
                String[] images = StringUtils.split(row.get("images").toString(), ",");
                for (int i = 0; i < images.length; i++) {
                    images[i] = ImageUtil.PREFIX_IMAGE_URL + images[i];
                }
                row.put("images", images);
            }
            HotelOrder hotelOrder = hotelOrderMapper.selectById(row.get("order_id").toString());
            row.put("roomName", roomMapper.selectById(hotelOrder.getRoomId()).getName());
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("total", total);
        returnJson.put("rows", rows);
        return returnJson;
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
    @Override
    public SuccessDataTip listRoom(int hotelId, LocalDate checkInDate, LocalDate checkOutDate, Integer order, Integer breakfast, Integer window, Integer canCancel) {
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new BussinessException(BizExceptionEnum.HOTEL_CHEK_IN_DATE_BEFORE);
        }
        StringBuilder dates = new StringBuilder();
        for (int i = 0; i < checkOutDate.compareTo(checkInDate); i++) {
            dates.append(",").append(checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        EntityWrapper<Room> roomEntityWrapper = new EntityWrapper<>();
        roomEntityWrapper.setSqlSelect("id", "`name`", "main_img", "area", "window", "breakfast", "can_cancel",
                "(SELECT AVG(room_setting.price) FROM room_setting WHERE room_setting.is_deleted = 0 AND room_setting.`status` = 1 AND room_id = room.id AND date BETWEEN '" + checkInDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND '" + checkOutDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ORDER BY date) price")   //价格，单价为时间段内房间价格的均价（例：5.3号价格100,5月4号价格200,用户端单价显示为150），如有小数，则舍弃。
                .eq("is_deleted", IsDeleted.NORMAL.getCode())
                .eq("status", RoomStatus.YES.getCode())
                .eq("shop_id", hotelId)
                .where("INSTR((SELECT GROUP_CONCAT(date) FROM room_setting WHERE is_deleted = 0 AND `status` = 1 AND room_id = room.id ORDER BY date) , '" + StringUtils.substringAfter(dates.toString(), ",") + "')");
        if (window != null && window == 1) {
            roomEntityWrapper.eq("window", window);
        }
        if (canCancel != null && canCancel == 1) {
            roomEntityWrapper.eq("can_cancel", canCancel);
        }
        List<Room> list = roomMapper.selectList(roomEntityWrapper);
        List<JSONObject> roomList = new ArrayList<>();
        for (Room r : list) {
            JSONObject room = JSONObject.parseObject(JSONObject.toJSONString(r));
            room.put("price", r.getPrice().divide(BigDecimal.TEN.multiply(BigDecimal.TEN), 0, BigDecimal.ROUND_DOWN));
            if (StringUtils.isNotBlank(room.getString("mainImg"))) {
                room.put("mainImg", ImageUtil.PREFIX_IMAGE_URL + room.getString("mainImg"));
            }
            List<RoomImg> roomImgList = roomImgMapper.selectList(new EntityWrapper<RoomImg>()
                    .setSqlSelect("CONCAT('" + ImageUtil.PREFIX_IMAGE_URL + "',img) img")
                    .eq("is_deleted", IsDeleted.NORMAL.getCode())
                    .eq("room_id", r.getId())
                    .orderBy("ISNULL(sequence)", Boolean.TRUE)
                    .orderBy("sequence", Boolean.TRUE));
            room.put("roomImgList", JSONObject.parseArray(JSONObject.toJSONString(roomImgList)));
            room.put("windowName", RoomWindow.valueOf(r.getWindow()));
            room.put("breakfastName", RoomBreakfast.valueOf(r.getBreakfast()));
            room.put("canCancelName", RoomCanCancel.valueOf(r.getCanCancel()));
            //是否可订，1可订，0不可订
            int canOrder = 1;
            for (int i = 0; i < checkOutDate.compareTo(checkInDate); i++) {
                List<Map<String, Object>> list1 = roomSettingMapper.selectMaps(new EntityWrapper<RoomSetting>()
                        .setSqlSelect("(number-IFNULL((SELECT SUM(room_number) FROM hotel_order WHERE hotel_order.is_deleted=0 AND hotel_order.shop_id=room_setting.shop_id AND hotel_order.room_id=room_setting.room_id AND (hotel_order.status = 2 OR hotel_order.status = 3) AND hotel_order.check_in_date<='" + checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND hotel_order.check_out_date> '" + checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'),0)) number")
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .eq("shop_id", hotelId)
                        .eq("room_id", r.getId())
                        .eq("date", checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (list1.size() > 0) {
                    BigDecimal number = new BigDecimal(list1.get(0).get("number").toString());
                    if (number.compareTo(BigDecimal.ZERO) <= 0) {
                        canOrder = 0;
                    }
                }
            }
            room.put("canOrder", canOrder);
            if (order != null) {
                if (order.equals(1)) {//筛选可订房间
                    if (canOrder == 1) {
                        roomList.add(room);
                    }
                } else {//全部房间
                    roomList.add(room);
                }
            } else {//全部房间
                roomList.add(room);
            }
        }

        //按价格排序
        Collections.sort(roomList, Comparator.comparing(o -> o.getBigDecimal("price")));
        return new SuccessDataTip(roomList);
    }

    /**
     * 房间
     *
     * @param hotelId 酒店ID
     * @param roomId  房间ID
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip room(int hotelId, int roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new BussinessException(BizExceptionEnum.HOTEL_CHEK_IN_DATE_BEFORE);
        }
        List<Map<String, Object>> roomList = roomMapper.selectMaps(new EntityWrapper<Room>().setSqlSelect("id", "shop_id shopId", "name", "breakfast", "can_cancel canCancel", "broadband", "window", "area", "bed_width bedWidth", "person",
                "(SELECT AVG(room_setting.price) FROM room_setting WHERE room_setting.is_deleted = 0 AND room_setting.`status` = 1 AND room_id = room.id AND date BETWEEN '" + checkInDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND '" + checkOutDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ORDER BY date) price")   //价格，单价为时间段内房间价格的均价（例：5.3号价格100,5月4号价格200,用户端单价显示为150），如有小数，则舍弃。
                .eq("is_deleted", IsDeleted.NORMAL.getCode())
                .eq("status", RoomStatus.YES.getCode())
                .eq("shop_id", hotelId)
                .eq("id", roomId));
        if (roomList.size() > 0) {
            //是否可订，1可订，0不可订
            BigDecimal roomNumber = BigDecimal.ZERO;
            int canOrder = 1;
            for (int i = 0; i < checkOutDate.compareTo(checkInDate); i++) {
                List<Map<String, Object>> list1 = roomSettingMapper.selectMaps(new EntityWrapper<RoomSetting>()
                        .setSqlSelect("(number-IFNULL((SELECT SUM(room_number) FROM hotel_order WHERE hotel_order.is_deleted=0 AND hotel_order.shop_id=room_setting.shop_id AND hotel_order.room_id=room_setting.room_id AND (hotel_order.status = 2 OR hotel_order.status = 3) AND hotel_order.check_in_date<='" + checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND hotel_order.check_out_date> '" + checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'),0)) number")
                        .eq("is_deleted", IsDeleted.NORMAL.getCode())
                        .eq("shop_id", hotelId)
                        .eq("room_id", roomId)
                        .eq("date", checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (list1.size() > 0) {
                    BigDecimal number = new BigDecimal(list1.get(0).get("number").toString());
                    if (number.compareTo(BigDecimal.ZERO) <= 0) {
                        canOrder = 0;
                    } else {
                        //取时间段内房间可订数量的最小值
                        if (i == 0) {
                            roomNumber = number;
                        } else {
                            if (number.compareTo(roomNumber) < 0) {
                                roomNumber = number;
                            }
                        }
                    }
                }
            }
            if (canOrder == 0) {
                throw new BussinessException(BizExceptionEnum.HOTEL_ROOM_NOT_CAN_ORDER);
            }

            Map<String, Object> room = roomList.get(0);
            List<RoomImg> roomImgList = roomImgMapper.selectList(new EntityWrapper<RoomImg>()
                    .setSqlSelect("CONCAT('" + ImageUtil.PREFIX_IMAGE_URL + "',img) img")
                    .eq("is_deleted", IsDeleted.NORMAL.getCode())
                    .eq("room_id", roomId)
                    .orderBy("ISNULL(sequence)", Boolean.TRUE)
                    .orderBy("sequence", Boolean.TRUE));
            room.put("days", checkOutDate.compareTo(checkInDate));
            room.put("price", new BigDecimal(room.get("price").toString()).divide(BigDecimal.TEN.multiply(BigDecimal.TEN), 0, BigDecimal.ROUND_DOWN));
            room.put("roomNumber", roomNumber);
            room.put("roomImgList", JSONObject.parseArray(JSONObject.toJSONString(roomImgList)));
            room.put("windowName", RoomWindow.valueOf((int) room.get("window")));
            room.put("breakfastName", RoomBreakfast.valueOf((int) room.get("breakfast")));
            room.put("canCancelName", RoomCanCancel.valueOf((int) room.get("canCancel")));
            room.put("broadbandName", RoomBroadband.valueOf((int) room.get("broadband")));
            List<RoomIntro> roomIntroList = roomIntroMapper.selectList(new EntityWrapper<RoomIntro>().eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("room_id", roomId));
            if (roomIntroList.size() > 0) {
                room.put("intro", roomIntroList.get(0).getIntroContent());
            }
            Shop shop = shopMapper.selectById(hotelId);
            if (shop != null) {
                room.put("shopName", shop.getFrontName());
            }
            return new SuccessDataTip(room);
        }

        try {
            //增加访问记录和统计访问数据
            recordService.viewRoom((long) roomId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 查询用户关联的游客
     *
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip listTourist() {
        Long userId = ConstantFactory.me().getUserId();
        List<UserTourist> userTouristList = userTouristMapper.selectList(new EntityWrapper<UserTourist>().setSqlSelect("id", "name", "id_card_no").eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("user_id", userId));
        return new SuccessDataTip(JSONObject.parseArray(JSONObject.toJSONString(userTouristList)));
    }

}
