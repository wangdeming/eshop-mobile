package cn.ibdsr.web.modular.shop.shop.service.impl;

import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.ShopMapper;
import cn.ibdsr.web.common.persistence.model.Shop;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.shop.shop.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
@Service
public class ShopServiceImpl implements IShopService {

    @Autowired
    private ShopMapper shopMapper;

    /**
     * 店铺主页 查询店铺信息
     *
     * @param shopId 店铺ID
     * @return
     */
    @Override
    public Map<String, Object> info(Long shopId) {
        Shop shop = checkShopId(shopId);

        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("frontName", shop.getFrontName());                        // 店铺前台名称
        returnMap.put("logoUrl", ImageUtil.setImageURL(shop.getLogo()));        // 店铺logo
        returnMap.put("coverUrl", ImageUtil.setImageURL(shop.getCover()));      // 店铺封面
        returnMap.put("intro", shop.getIntro());                                // 店铺介绍
        returnMap.put("status", shop.getStatus());                              // 店铺状态（1-未开通账号；2-正常营业；3-下架；）
        return returnMap;
    }

    /**
     * 校验shopId是否存在
     *
     * @param shopId 店铺ID
     * @return
     */
    @Override
    public Shop checkShopId(Long shopId) {
        if (ToolUtil.isEmpty(shopId)) {
            throw new BussinessException(BizExceptionEnum.SHOP_ID_IS_NULL);
        }

        Shop shop = shopMapper.selectById(shopId);
        if (shop == null) {
            throw new BussinessException(BizExceptionEnum.SHOP_IS_NOT_EXIST);
        }
        return shop;
    }
}
