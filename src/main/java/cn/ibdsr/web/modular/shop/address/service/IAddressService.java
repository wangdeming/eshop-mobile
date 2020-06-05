package cn.ibdsr.web.modular.shop.address.service;


import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.persistence.model.UserDeliveryAddress;
import cn.ibdsr.web.modular.shop.address.transfer.UserDeliveryAddressDTO;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @Description 收货地址Service
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public interface IAddressService {
    /**
     * 获取收货地址列表
     * @param userId 用户ID
     * @return
     */
    List<Map<String,Object>> list(Long userId);

    /**
     * 新增收货地址
     * @param addressDTO
     * @return
     */
    void add(UserDeliveryAddressDTO addressDTO);

    /**
     * 收货地址详情
     * @param addressId 收货地址ID
     * @return
     */
    UserDeliveryAddress detail(Long addressId);

    /**
     * 修改收货地址
     * @param addressDTO
     * @return
     */
    void update(UserDeliveryAddressDTO addressDTO);

    /**
     * 删除收货地址
     * @param addressId 收货地址ID
     * @return
     */
    void delete(Long addressId);

    /**
     * 统计当前用户收货地址数量
     * @param userId 用户ID
     * @return
     */
    int addressCount(Long userId);

    List<Map<String,Object>> listAreasByPid(Long pid);

    SuccessDataTip listAreas();
}
