package cn.ibdsr.web.modular.shop.tourist.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 游客Dao
 * @Version V1.0
 * @CreateDate 2019-03-18 13:55:19
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun             类说明
 */
public interface TouristDao {


    /**
     * 获取游客列表
     * @param userId 用户ID
     * @return
     */
    List<Map<String,Object>> list(@Param("userId")Long userId);

    /**
     * 统计当前用户的游客数量
     * @param userId 用户ID
     * @return
     */
    Integer touristCount(Long userId);
}
