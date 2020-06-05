package cn.ibdsr.web.modular.shop.address.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 收货地址Dao
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public interface AddressDao {
    /**
     * 获取收货地址列表
     * @param userId 用户ID
     * @return
     */
    List<Map<String,Object>> list(@Param("userId") Long userId);

    /**
     * 统计当前用户收货地址数量
     * @param userId 用户ID
     * @return
     */
    int addressCount(Long userId);
}
