package cn.ibdsr.web.modular.shop.shop.service;

import cn.ibdsr.web.common.persistence.model.Shop;

import java.util.Map;

/**
 * @Description 店铺Service
 * @Version V1.0
 * @CreateDate 2019-03-15 14:41:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 14:41:22    XuZhipeng               类说明
 *
 */
public interface IShopService {

    /**
     * 店铺主页 查询店铺信息
     *
     * @param shopId 店铺ID
     * @return
     */
    Map<String, Object> info(Long shopId);

    /**
     * 校验shopId是否存在
     *
     * @param shopId 店铺ID
     * @return
     */
    Shop checkShopId(Long shopId);
}
