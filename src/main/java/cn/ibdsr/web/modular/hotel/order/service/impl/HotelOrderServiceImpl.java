package cn.ibdsr.web.modular.hotel.order.service.impl;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessMesTip;
import cn.ibdsr.core.check.CheckUtil;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.HotelOrderStatus;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.constant.state.RoomBreakfast;
import cn.ibdsr.web.common.constant.state.RoomCanCancel;
import cn.ibdsr.web.common.constant.state.RoomStatus;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.HotelOrderEvaluateMapper;
import cn.ibdsr.web.common.persistence.dao.HotelOrderMapper;
import cn.ibdsr.web.common.persistence.dao.HotelOrderUserMapper;
import cn.ibdsr.web.common.persistence.dao.RoomMapper;
import cn.ibdsr.web.common.persistence.dao.RoomSettingMapper;
import cn.ibdsr.web.common.persistence.dao.ShopMapper;
import cn.ibdsr.web.common.persistence.model.HotelOrder;
import cn.ibdsr.web.common.persistence.model.HotelOrderEvaluate;
import cn.ibdsr.web.common.persistence.model.HotelOrderUser;
import cn.ibdsr.web.common.persistence.model.Room;
import cn.ibdsr.web.common.persistence.model.RoomSetting;
import cn.ibdsr.web.common.persistence.model.Shop;
import cn.ibdsr.web.core.mq.rabbitmq.MessageSenderTask;
import cn.ibdsr.web.core.util.CommonUtils;
import cn.ibdsr.web.core.util.DateTimeUtils;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.core.util.OrderUtils;
import cn.ibdsr.web.core.util.VerifyUtils;
import cn.ibdsr.web.modular.hotel.order.dao.HotelOrderDao;
import cn.ibdsr.web.modular.hotel.order.service.HotelOrderService;
import cn.ibdsr.web.modular.hotel.order.service.HotelPayService;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
@Service
public class HotelOrderServiceImpl implements HotelOrderService {

    @Autowired
    private HotelOrderMapper hotelOrderMapper;

    @Autowired
    private HotelOrderDao hotelOrderDao;

    @Autowired
    private HotelOrderUserMapper hotelOrderUserMapper;

    @Autowired
    private HotelOrderEvaluateMapper hotelOrderEvaluateMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomSettingMapper roomSettingMapper;

    @Autowired
    private HotelPayService hotelPayService;

    @Autowired
    private MessageSenderTask messageSenderTask;

    /**
     * 分页查询订单列表
     *
     * @param offset offset
     * @param limit  limit
     * @param status 状态，1待付款，2待确认，3待使用，4已消费（待评价）
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip list(int offset, int limit, Integer status) {
        Long userId = ConstantFactory.me().getUserId();
        EntityWrapper<HotelOrder> hotelOrderEntityWrapper = new EntityWrapper<>();
        hotelOrderEntityWrapper.setSqlSelect("id", "order_no", "shop_id", "user_id", "room_id", "room_number", "total_amount", "check_in_date", "check_out_date", "can_cancel", "status", "confirm_in_datetime", "created_time")
                .eq("is_deleted", IsDeleted.NORMAL.getCode())
                .eq("user_id", userId);
        if (status != null) {
            if (status == HotelOrderStatus.WAIT_PAY.getCode() || status == HotelOrderStatus.WAIT_CONFIRM.getCode() || status == HotelOrderStatus.WAIT_USE.getCode() || status == HotelOrderStatus.CONSUMED.getCode()) {
                hotelOrderEntityWrapper.eq("status", status);
            }
        }
        RowBounds rowBounds = new RowBounds(offset, limit);
        Integer total = hotelOrderMapper.selectCount(hotelOrderEntityWrapper);
        List<HotelOrder> list = hotelOrderMapper.selectPage(rowBounds, hotelOrderEntityWrapper);
        List<JSONObject> hotelOrderList = JSONObject.parseArray(JSONObject.toJSONString(list), JSONObject.class);
        for (JSONObject hotelOrder : hotelOrderList) {
            hotelOrder.put("checkInDate", DateTimeUtils.toLocalDateTime(hotelOrder.getDate("checkInDate")).format(DateTimeFormatter.ofPattern("M月d日")));
            hotelOrder.put("checkOutDate", DateTimeUtils.toLocalDateTime(hotelOrder.getDate("checkOutDate")).format(DateTimeFormatter.ofPattern("M月d日")));
            hotelOrder.put("statusName", HotelOrderStatus.valueOf(hotelOrder.getIntValue("status")));
            if (hotelOrder.getIntValue("status") == HotelOrderStatus.WAIT_PAY.getCode()) {
                long minute = LocalDateTime.now().until(DateTimeUtils.toLocalDateTime(hotelOrder.getDate("createdTime")).plusSeconds(Const.HOTEL_ORDER_OUT_TIME), ChronoUnit.MINUTES);
                long second = LocalDateTime.now().plusMinutes(minute).until(DateTimeUtils.toLocalDateTime(hotelOrder.getDate("createdTime")).plusSeconds(Const.HOTEL_ORDER_OUT_TIME), ChronoUnit.SECONDS);
                hotelOrder.put("surplusTime", StringUtils.leftPad(String.valueOf(minute), 2, String.valueOf(0)) + ":" + StringUtils.leftPad(String.valueOf(second), 2, String.valueOf(0)));
            }
            Shop shop = shopMapper.selectById(hotelOrder.getLong("shopId"));
            if (shop != null) {
                if (StringUtils.isNotBlank(shop.getLogo())) {
                    hotelOrder.put("hotelLogo", ImageUtil.PREFIX_IMAGE_URL + shop.getLogo());
                }
                if (StringUtils.isNotBlank(shop.getCover())) {
                    hotelOrder.put("hotelCover", ImageUtil.PREFIX_IMAGE_URL + shop.getCover());
                }
                hotelOrder.put("hotelName", shop.getFrontName());
            }
            Room room = roomMapper.selectById(hotelOrder.getLong("roomId"));
            if (room != null) {
                hotelOrder.put("roomName", room.getName());
            }
            hotelOrder.put("totalAmount", hotelOrder.getBigDecimal("totalAmount").divide(BigDecimal.TEN.multiply(BigDecimal.TEN), 2).stripTrailingZeros().toPlainString());
            if (hotelOrder.getIntValue("status") == HotelOrderStatus.CONSUMED.getCode() || hotelOrder.getIntValue("status") == HotelOrderStatus.Finished.getCode()) {
                List<HotelOrderEvaluate> hotelOrderEvaluateList = hotelOrderEvaluateMapper.selectList(new EntityWrapper<HotelOrderEvaluate>().eq("order_id", hotelOrder.get("id")));
                if (hotelOrderEvaluateList.size() > 0) {
                    hotelOrder.put("canEvaluate", 0);
                } else {
                    if (DateTimeUtils.toLocalDateTime(hotelOrder.getDate("confirmInDatetime")).plusDays(Const.HOTEL_ORDER_EVALUATE_OUT_DAY).isBefore(LocalDateTime.now())) {
                        hotelOrder.put("canEvaluate", 0);//订单消费后超过10天订单不可评价
                    } else {
                        hotelOrder.put("canEvaluate", 1);
                    }
                }
            } else {
                hotelOrder.put("canEvaluate", 0);
            }
            //剔除多余字段
            hotelOrder.remove("confirmInDatetime");
            hotelOrder.remove("createdTime");
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("total", total);
        returnJson.put("hotelOrderList", hotelOrderList);
        return new SuccessDataTip(returnJson);
    }

    private void verify(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomNumber, String mobile, String orderUserListString) {
        Room room = roomMapper.selectById(roomId);
        if (room == null || room.getIsDeleted() == IsDeleted.DELETED.getCode()) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        if (room.getStatus() == RoomStatus.NO.getCode()) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ROOM_STATUS_0);
        }
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new BussinessException(BizExceptionEnum.HOTEL_CHEK_IN_DATE_BEFORE);
        }
        if (!CheckUtil.isMobile(mobile)) {
            throw new BussinessException(BizExceptionEnum.SHOP_ORDER_ADDRESS_PHONE_FORMAT_ERROR);
        }
        List<HotelOrderUser> orderUserList = JSONObject.parseArray(orderUserListString, HotelOrderUser.class);
        if (orderUserList.size() == 0) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ROOM_SET_USER);
        }
        int canOrder = 1;
        for (int i = 0; i < checkOutDate.compareTo(checkInDate); i++) {
            List<Map<String, Object>> list1 = roomSettingMapper.selectMaps(new EntityWrapper<RoomSetting>()
                    .setSqlSelect("(number-IFNULL((SELECT SUM(room_number) FROM hotel_order WHERE hotel_order.is_deleted=0 AND hotel_order.shop_id=room_setting.shop_id AND hotel_order.room_id=room_setting.room_id AND (hotel_order.status = 2 OR hotel_order.status = 3) AND hotel_order.check_in_date<='" + checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND hotel_order.check_out_date> '" + checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'),0)) number")
                    .eq("is_deleted", IsDeleted.NORMAL.getCode())
                    .eq("room_id", roomId)
                    .eq("date", checkInDate.plusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            if (list1.size() > 0) {
                BigDecimal number = new BigDecimal(list1.get(0).get("number").toString());
                if (number.compareTo(BigDecimal.ZERO) <= 0) {
                    canOrder = 0;
                }
            }
        }
        if (canOrder == 0) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ROOM_NOT_CAN_ORDER);
        }
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
     */
    @Override
    @Transactional
    public SuccessDataTip createOrder(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Integer roomNumber, String mobile, String orderUserListString, Integer payType) {
        if (!VerifyUtils.verifyPayType(payType)) {
            throw new BussinessException(BizExceptionEnum.PAY_TYPE_IS_NOT_EXIT);
        }
        verify(roomId, checkInDate, checkOutDate, roomNumber, mobile, orderUserListString);
        Date now = new Date();
        Long userId = ConstantFactory.me().getUserId();
        List<Map<String, Object>> roomList = roomMapper.selectMaps(new EntityWrapper<Room>().setSqlSelect("id", "shop_id shopId", "can_cancel canCancel", "name",
                "(SELECT AVG(room_setting.price) FROM room_setting WHERE room_setting.is_deleted = 0 AND room_setting.`status` = 1 AND room_id = room.id AND date BETWEEN '" + checkInDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' AND '" + checkOutDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "' ORDER BY date) price")   //价格，单价为时间段内房间价格的均价（例：5.3号价格100,5月4号价格200,用户端单价显示为150），如有小数，则舍弃。
                .eq("id", roomId));
        Map<String, Object> room = roomList.get(0);
        HotelOrder hotelOrder = new HotelOrder();
        hotelOrder.setId(CommonUtils.getUUID32());
        hotelOrder.setOrderNo(OrderUtils.genOrderNo(Const.ORDER_NO_ORDER_CHANNEL_1, payType, Const.ORDER_NO_BUSINESS_TYPE_2, userId));
        hotelOrder.setShopId((Long) room.get("shopId"));
        hotelOrder.setUserId(userId);
        hotelOrder.setRoomId(roomId);
        hotelOrder.setRoomNumber(roomNumber);
        hotelOrder.setPrice(new BigDecimal(room.get("price").toString()));
        //总金额等于房间均价*天数*房间数
        hotelOrder.setTotalAmount(hotelOrder.getPrice().multiply(BigDecimal.valueOf(checkOutDate.compareTo(checkInDate))).multiply(BigDecimal.valueOf(hotelOrder.getRoomNumber())));
        hotelOrder.setMobile(mobile);
        hotelOrder.setCheckInDate(DateTimeUtils.toDate(checkInDate));
        hotelOrder.setCheckOutDate(DateTimeUtils.toDate(checkOutDate));
        hotelOrder.setCanCancel((Integer) room.get("canCancel"));
        hotelOrder.setCreatedUser(userId);
        hotelOrder.setCreatedTime(now);
        hotelOrderDao.insertSelective(hotelOrder);//创建订单

        List<HotelOrderUser> orderUserList = JSONObject.parseArray(orderUserListString, HotelOrderUser.class);
        for (HotelOrderUser orderUser : orderUserList) {
            orderUser.setOrderId(hotelOrder.getId());
            orderUser.setRoomId(roomId);
            orderUser.setCreatedUser(userId);
            orderUser.setCreatedTime(now);
            orderUser.insert();
        }

        JSONObject returnJson = new JSONObject();
        returnJson.put("orderId", hotelOrder.getId());
        returnJson.put("checkInDate", checkInDate.format(DateTimeFormatter.ofPattern("M月d日")));
        returnJson.put("checkOutDate", checkOutDate.format(DateTimeFormatter.ofPattern("M月d日")));
        returnJson.put("TotalAmount", hotelOrder.getTotalAmount().divide(BigDecimal.TEN.multiply(BigDecimal.TEN), 2).stripTrailingZeros().toPlainString());
        returnJson.put("roomNumber", hotelOrder.getRoomNumber());
        Shop shop = shopMapper.selectById(hotelOrder.getShopId());
        returnJson.put("shopAndRoomName", (shop == null ? "" : shop.getFrontName()) + "(" + room.get("name") + ")");
        long minute = Duration.ofSeconds(Const.HOTEL_ORDER_OUT_TIME).toMinutes();
        long second = Duration.ofSeconds(Const.HOTEL_ORDER_OUT_TIME).minusMinutes(minute).getSeconds();
        returnJson.put("surplusTime", StringUtils.leftPad(String.valueOf(minute), 2, String.valueOf(0)) + ":" + StringUtils.leftPad(String.valueOf(second), 2, String.valueOf(0)));
        returnJson.put("surplusSeconds", Const.HOTEL_ORDER_OUT_TIME);

        //放入消息队列，超过30分钟未支付，取消订单
        messageSenderTask.sendMsgOfCancelHotelOrder(hotelOrder.getId());

        return new SuccessDataTip(returnJson);
    }

    /**
     * 订单详情
     *
     * @param hotelOrderId 订单ID
     * @return SuccessDataTip
     */
    @Override
    public SuccessDataTip detail(String hotelOrderId) {
        JSONObject returnJson = new JSONObject();
        //订单信息
        HotelOrder hotelOrder = hotelOrderMapper.selectById(hotelOrderId);
        if (hotelOrder == null || hotelOrder.getIsDeleted() == IsDeleted.DELETED.getCode()) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        //如果订单状态为待付款，但已超付款时间，则修改订单状态为已取消
        if (hotelOrder.getStatus() == HotelOrderStatus.WAIT_PAY.getCode() && DateTimeUtils.toLocalDateTime(hotelOrder.getCreatedTime()).plusSeconds(Const.HOTEL_ORDER_OUT_TIME).isBefore(LocalDateTime.now())) {
            HotelOrder hotelOrder1 = new HotelOrder();
            hotelOrder1.setId(hotelOrder.getId());
            hotelOrder1.setStatus(HotelOrderStatus.CANCELLED.getCode());
            hotelOrder1.setModifiedUser(ConstantFactory.me().getUserId());
            hotelOrder1.setModifiedTime(new Date());
            hotelOrder1.updateById();
        }
        //如果订单状态为待确认，但已超确认时间，则修改订单状态为已确认
        if (hotelOrder.getStatus() == HotelOrderStatus.WAIT_CONFIRM.getCode() && DateTimeUtils.toLocalDateTime(hotelOrder.getPayDatetime()).plusSeconds(Const.HOTEL_ORDER_OUT_TIME).isBefore(LocalDateTime.now())) {
            HotelOrder hotelOrder1 = new HotelOrder();
            hotelOrder1.setId(hotelOrder.getId());
            hotelOrder1.setStatus(HotelOrderStatus.WAIT_USE.getCode());
            hotelOrder1.setModifiedUser(ConstantFactory.me().getUserId());
            hotelOrder1.setModifiedTime(new Date());
            hotelOrder1.updateById();
        }
        hotelOrder = hotelOrderMapper.selectById(hotelOrderId);
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(HotelOrder.class, "id", "orderNo", "roomNumber", "totalAmount", "mobile", "checkInDate", "checkOutDate", "canCancel", "status", "payDatetime", "createdTime");
        JSONObject hotelOrderJson = JSONObject.parseObject(JSONObject.toJSONString(hotelOrder, filter));
        LocalDate checkInDate = DateTimeUtils.toLocalDateTime(hotelOrder.getCheckInDate()).toLocalDate();
        hotelOrderJson.put("checkInDate", checkInDate.format(DateTimeFormatter.ofPattern("M月d日")) + "(" + checkInDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.SIMPLIFIED_CHINESE) + ")");
        LocalDate checkOutDate = DateTimeUtils.toLocalDateTime(hotelOrder.getCheckOutDate()).toLocalDate();
        hotelOrderJson.put("checkOutDate", checkOutDate.format(DateTimeFormatter.ofPattern("M月d日")) + "(" + checkOutDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.SIMPLIFIED_CHINESE) + ")");
        hotelOrderJson.put("days", checkOutDate.compareTo(checkInDate));
        hotelOrderJson.put("createdTime", DateTimeUtils.toLocalDateTime(hotelOrder.getCreatedTime()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        hotelOrderJson.put("statusName", HotelOrderStatus.valueOf(hotelOrder.getStatus()));
        hotelOrderJson.put("canCancelName", RoomCanCancel.valueOf(hotelOrder.getCanCancel()));
        if (hotelOrder.getStatus() == HotelOrderStatus.WAIT_PAY.getCode()) {
            long minute = LocalDateTime.now().until(DateTimeUtils.toLocalDateTime(hotelOrder.getCreatedTime()).plusSeconds(Const.HOTEL_ORDER_OUT_TIME), ChronoUnit.MINUTES);
            long second = LocalDateTime.now().plusMinutes(minute).until(DateTimeUtils.toLocalDateTime(hotelOrder.getCreatedTime()).plusSeconds(Const.HOTEL_ORDER_OUT_TIME), ChronoUnit.SECONDS);
            hotelOrderJson.put("surplusTime", StringUtils.leftPad(String.valueOf(minute), 2, String.valueOf(0)) + ":" + StringUtils.leftPad(String.valueOf(second), 2, String.valueOf(0)));
        }
        if (hotelOrder.getStatus() == HotelOrderStatus.WAIT_CONFIRM.getCode()) {
            long minute = LocalDateTime.now().until(DateTimeUtils.toLocalDateTime(hotelOrder.getPayDatetime()).plusSeconds(Const.HOTEL_ORDER_OUT_TIME), ChronoUnit.MINUTES);
            long second = LocalDateTime.now().plusMinutes(minute).until(DateTimeUtils.toLocalDateTime(hotelOrder.getPayDatetime()).plusSeconds(Const.HOTEL_ORDER_OUT_TIME), ChronoUnit.SECONDS);
            hotelOrderJson.put("surplusTime", StringUtils.leftPad(String.valueOf(minute), 2, String.valueOf(0)) + ":" + StringUtils.leftPad(String.valueOf(second), 2, String.valueOf(0)));
        }
        returnJson.put("order", hotelOrderJson);

        //酒店信息
        Shop shop = shopMapper.selectById(hotelOrder.getShopId());
        if (shop != null) {
            if (StringUtils.isNotBlank(shop.getLogo())) {
                shop.setLogo(ImageUtil.PREFIX_IMAGE_URL + shop.getLogo());
            }
            if (StringUtils.isNotBlank(shop.getCover())) {
                shop.setCover(ImageUtil.PREFIX_IMAGE_URL + shop.getCover());
            }
            SimplePropertyPreFilter shopFilter = new SimplePropertyPreFilter(Shop.class, "id", "frontName", "address", "longitude", "latitude", "officeTelphone", "officePhone", "logo", "cover");
            JSONObject hotelJson = JSONObject.parseObject(JSONObject.toJSONString(shop, shopFilter));
            returnJson.put("hotel", hotelJson);
        }

        //房间信息
        Room room = roomMapper.selectById(hotelOrder.getRoomId());
        if (room != null) {
            SimplePropertyPreFilter shopFilter = new SimplePropertyPreFilter(Room.class, "id", "name", "breakfast");
            JSONObject roomJson = JSONObject.parseObject(JSONObject.toJSONString(room, shopFilter));
            roomJson.put("breakfastName", RoomBreakfast.valueOf(room.getBreakfast()));
            returnJson.put("room", roomJson);
        }

        //入住人
        List<Map<String, Object>> hotelOrderUserList = hotelOrderUserMapper.selectMaps(new EntityWrapper<HotelOrderUser>().setSqlSelect("realname").eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("order_id", hotelOrder.getId()));
        returnJson.put("users", hotelOrderUserList);

        return new SuccessDataTip(returnJson);
    }

    /**
     * 订单取消
     *
     * @param hotelOrderId 订单ID
     * @return SuccessMesTip
     */
    @Override
    @Transactional
    public SuccessMesTip cancel(String hotelOrderId) throws Exception {
        HotelOrder hotelOrder = hotelOrderMapper.selectById(hotelOrderId);
        if (hotelOrder == null || (hotelOrder.getStatus() != HotelOrderStatus.WAIT_PAY.getCode() && hotelOrder.getStatus() != HotelOrderStatus.WAIT_CONFIRM.getCode() && hotelOrder.getStatus() != HotelOrderStatus.WAIT_USE.getCode())) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        if (hotelOrder.getCanCancel() == RoomCanCancel.NO.getCode()) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_CAN_NOT_CANCEL);
        }
        if (DateTimeUtils.toLocalDateTime(hotelOrder.getCheckInDate()).withHour(14).isBefore(LocalDateTime.now())) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_OUT_CANCEL_DATETIME);
        }
        Long userId = ConstantFactory.me().getUserId();
        Date now = new Date();
        HotelOrder order = new HotelOrder();
        order.setId(hotelOrder.getId());
        order.setStatus(HotelOrderStatus.CANCELLED.getCode());
        order.setModifiedUser(userId);
        order.setModifiedTime(now);
        order.updateById();
        String message = "订单已取消";
        if (hotelOrder.getStatus() == HotelOrderStatus.WAIT_CONFIRM.getCode() || hotelOrder.getStatus() == HotelOrderStatus.WAIT_USE.getCode()) {
            hotelPayService.refund(hotelOrderId);
            message = "订单已取消，支付金额已退回原支付渠道。";
        }
        return new SuccessMesTip(message);
    }

    /**
     * 删除订单
     *
     * @param hotelOrderId 订单ID
     * @return SuccessMesTip
     */
    @Override
    public SuccessMesTip delete(String hotelOrderId) {
        HotelOrder hotelOrder = hotelOrderMapper.selectById(hotelOrderId);
        if (hotelOrder == null) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);
        }
        if (hotelOrder.getStatus() != HotelOrderStatus.CONSUMED.getCode() && hotelOrder.getStatus() != HotelOrderStatus.CANCELLED.getCode()) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_NOT_CAN_DELETE);
        }
        HotelOrder param = new HotelOrder();
        param.setId(hotelOrderId);
        param.setIsDeleted(IsDeleted.DELETED.getCode());
        param.setModifiedUser(ConstantFactory.me().getUserId());
        param.setModifiedTime(new Date());
        param.updateById();
        return new SuccessMesTip("删除成功");
    }

    @Override
    public SuccessMesTip evaluate(HotelOrderEvaluate hotelOrderEvaluate) {
        HotelOrder hotelOrder = hotelOrderMapper.selectById(hotelOrderEvaluate.getOrderId());
        if (hotelOrder == null) {
            throw new BussinessException(BizExceptionEnum.DATA_ERROR);

        }
        if (DateTimeUtils.toLocalDateTime(hotelOrder.getConfirmInDatetime()).plusDays(Const.HOTEL_ORDER_EVALUATE_OUT_DAY).isBefore(LocalDateTime.now())) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_CAN_NOT_EVALUATE);
        }
        List<HotelOrderEvaluate> hotelOrderEvaluateList = hotelOrderEvaluateMapper.selectList(new EntityWrapper<HotelOrderEvaluate>().eq("order_id", hotelOrderEvaluate.getOrderId()));
        if (hotelOrderEvaluateList.size() > 0) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_EVALUATE_REPEAT);
        }
        if (hotelOrderEvaluate.getScore() == null) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_SCORE_NOTE_NULL);
        }
        if (hotelOrderEvaluate.getContent().length() > 300) {
            throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_EVALUATE_CONTENT_MAX);
        }
        if (StringUtils.isNotBlank(hotelOrderEvaluate.getImages())) {
            String[] images = StringUtils.split(hotelOrderEvaluate.getImages(), ",");
            if (images.length > 9) {
                throw new BussinessException(BizExceptionEnum.HOTEL_ORDER_EVALUATE_IMAGE_MAX);
            }
            for (int i = 0; i < images.length; i++) {
                images[i] = ImageUtil.cutImageURL(images[i]);
            }
            hotelOrderEvaluate.setImages(StringUtils.join(images, ","));
        }
        hotelOrderEvaluate.setShopId(hotelOrder.getShopId());
        hotelOrderEvaluate.setUserId(ConstantFactory.me().getUserId());
        hotelOrderEvaluate.setCreatedUser(ConstantFactory.me().getUserId());
        hotelOrderEvaluate.setCreatedTime(new Date());
        hotelOrderEvaluate.insert();
        return new SuccessMesTip("评价成功");
    }

}
