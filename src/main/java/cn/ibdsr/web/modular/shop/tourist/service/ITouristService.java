package cn.ibdsr.web.modular.shop.tourist.service;


import cn.ibdsr.web.common.persistence.model.UserTourist;
import cn.ibdsr.web.modular.shop.tourist.transfer.UserTouristDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description 游客Service
 * @Version V1.0
 * @CreateDate 2019-03-18 13:55:19
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public interface ITouristService {
    /**
     * 获取游客列表
     * @param userId 用户ID
     * @return
     */
    List<Map<String,Object>> list(Long userId);
    /**
     * 新增游客
     *
     * @param touristDTO
     * @return
     */
    void add(UserTouristDTO touristDTO);

    /**
     * 游客详情
     * @param touristId
     * @return
     */
    UserTourist detail(Long touristId);

    /**
     * 修改游客
     *
     * @param touristDTO
     * @return
     */
    void update(UserTouristDTO touristDTO);

    /**
     * 删除游客
     *
     * @param touristId 游客ID
     * @return
     */
    void delete(Long touristId);

    /**
     * 统计当前用户的游客数量
     * @param userId 用户ID
     * @return
     */
    Integer touristCount(Long userId);
}
