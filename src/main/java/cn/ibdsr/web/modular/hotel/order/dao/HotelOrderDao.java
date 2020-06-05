package cn.ibdsr.web.modular.hotel.order.dao;

import cn.ibdsr.web.common.persistence.model.HotelOrder;

public interface HotelOrderDao {
    int deleteByPrimaryKey(String id);

    int insert(HotelOrder record);

    int insertSelective(HotelOrder record);

    HotelOrder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(HotelOrder record);

    int updateByPrimaryKey(HotelOrder record);
}