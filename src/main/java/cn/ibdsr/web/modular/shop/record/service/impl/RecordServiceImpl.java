package cn.ibdsr.web.modular.shop.record.service.impl;

import cn.ibdsr.core.support.HttpKit;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.persistence.dao.GoodsMapper;
import cn.ibdsr.web.common.persistence.dao.RoomMapper;
import cn.ibdsr.web.common.persistence.dao.ViewRecordMapper;
import cn.ibdsr.web.common.persistence.dao.ViewStatsMapper;
import cn.ibdsr.web.common.persistence.model.Goods;
import cn.ibdsr.web.common.persistence.model.Room;
import cn.ibdsr.web.common.persistence.model.ViewRecord;
import cn.ibdsr.web.common.persistence.model.ViewStats;
import cn.ibdsr.web.modular.shop.record.service.RecordService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Description: 特产商店、酒店、商品、房间的浏览、支付、购买的记录
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/27 10:43
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/27 xujincai init
 */
@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private ViewRecordMapper viewRecordMapper;

    @Autowired
    private ViewStatsMapper viewStatsMapper;

    /**
     * 访问记录
     *
     * @param shopId  特产店铺ID或酒店ID
     * @param goodsId 商品ID
     * @param roomId  房间ID
     */
    private void view(Long shopId, Long goodsId, Long roomId) {
        ViewRecord viewRecord = new ViewRecord();
        viewRecord.setShopId(shopId);
        viewRecord.setGoodsId(goodsId);
        viewRecord.setRoomId(roomId);
        if (ConstantFactory.me().getUserId() != null) {
            viewRecord.setUserId(ConstantFactory.me().getUserId());
        }
        viewRecord.setIp(HttpKit.getIp());
        viewRecord.setCreatedTime(new Date());
        viewRecord.insert();
    }

    /**
     * 访问统计，统计浏览数和访客量
     *
     * @param shopId  特产店铺ID或酒店ID
     * @param goodsId 商品ID
     * @param roomId  房间ID
     */
    private void stats(Long shopId, Long goodsId, Long roomId) {
        EntityWrapper<ViewStats> viewStatsEntityWrapper = new EntityWrapper<>();
        viewStatsEntityWrapper.eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("shop_id", shopId);
        if (goodsId != null) {
            viewStatsEntityWrapper.eq("goods_id", goodsId);
        }
        if (roomId != null) {
            viewStatsEntityWrapper.eq("room_id", roomId);
        }
        if (goodsId == null && roomId == null) {
            viewStatsEntityWrapper.where("goods_id IS NULL AND room_id IS NULL");
        }
        List<ViewStats> viewStatsList = viewStatsMapper.selectList(viewStatsEntityWrapper);
        if (viewStatsList.size() == 0) {
            ViewStats viewStats = new ViewStats();
            viewStats.setShopId(shopId);
            viewStats.setGoodsId(goodsId);
            viewStats.setRoomId(roomId);
            viewStats.insert();
        } else {
            ViewStats stats = viewStatsList.get(0);
            ViewStats viewStats = new ViewStats();
            viewStats.setId(stats.getId());
            viewStats.setViewNum(stats.getViewNum() + 1);
            EntityWrapper<ViewRecord> viewRecordEntityWrapper = new EntityWrapper<>();
            viewRecordEntityWrapper.eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("shop_id", shopId);
            if (goodsId != null) {
                viewRecordEntityWrapper.eq("goods_id", goodsId);
            }
            if (roomId != null) {
                viewRecordEntityWrapper.eq("room_id", roomId);
            }
            if (ConstantFactory.me().getUserId() != null) {
                //登录用户
                viewRecordEntityWrapper.eq("user_id", ConstantFactory.me().getUserId());
                if (goodsId == null && roomId == null) {
                    viewRecordEntityWrapper.where("goods_id IS NULL AND room_id IS NULL");
                }
                List<ViewRecord> viewRecordList = viewRecordMapper.selectList(viewRecordEntityWrapper);
                if (viewRecordList.size() == 0) {
                    viewStats.setVisitorNum(stats.getVisitorNum() + 1);
                }
            } else {
                //未登录用户
                viewRecordEntityWrapper.eq("ip", HttpKit.getIp());
                if (goodsId == null && roomId == null) {
                    viewRecordEntityWrapper.where("goods_id IS NULL AND room_id IS NULL");
                }
                List<ViewRecord> viewRecordList = viewRecordMapper.selectList(viewRecordEntityWrapper);
                if (viewRecordList.size() == 0) {
                    viewStats.setVisitorNum(stats.getVisitorNum() + 1);
                }
            }
            viewStats.updateById();
        }
    }

    @Override
    public void viewShop(Long shopId) {
        stats(shopId, null, null);
        view(shopId, null, null);
    }

    @Override
    public void viewGoods(Long goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods != null) {
            stats(goods.getShopId(), goodsId, null);
            view(goods.getShopId(), goodsId, null);
        }
    }

    @Override
    public void viewRoom(Long roomId) {
        Room room = roomMapper.selectById(roomId);
        if (room != null) {
            stats(room.getShopId(), null, roomId);
            view(room.getShopId(), null, roomId);
        }
    }

    /**
     * 统计商品或房间的付款数
     *
     * @param goodsId 商品ID
     * @param roomId  房间ID
     */
    private void payStats(Long goodsId, Long roomId, Integer number) {
        EntityWrapper<ViewStats> viewStatsEntityWrapper = new EntityWrapper<>();
        viewStatsEntityWrapper.eq("is_deleted", IsDeleted.NORMAL.getCode()).eq("goods_id", goodsId);
        if (goodsId != null) {
            viewStatsEntityWrapper.eq("goods_id", goodsId);
        }
        if (roomId != null) {
            viewStatsEntityWrapper.eq("room_id", roomId);
        }
        List<ViewStats> viewStatsList = viewStatsMapper.selectList(viewStatsEntityWrapper);
        if (viewStatsList.size() > 0) {
            ViewStats viewStats = new ViewStats();
            viewStats.setId(viewStatsList.get(0).getId());
            viewStats.setPaymentNum(viewStatsList.get(0).getPaymentNum() + number);
            viewStats.setModifiedTime(new Date());
            viewStats.updateById();
        }
    }

    @Override
    public void payGoods(Long goodsId, Integer number) {
        payStats(goodsId, null, number);
    }

    @Override
    public void payRoom(Long roomId, Integer number) {
        payStats(null, roomId, number);
    }

}
