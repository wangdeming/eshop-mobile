package cn.ibdsr.web.modular.shop.shop.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.modular.shop.shop.service.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Description 店铺控制器
 * @Version V1.0
 * @CreateDate 2019-03-15 14:41:22
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-03-15 14:41:22    XuZhipeng               类说明
 *
 */
@RestController
@RequestMapping("shop/shop")
public class ShopController extends BaseController {

    @Autowired
    private IShopService shopService;

    /**
     * 店铺主页 查询店铺信息
     *
     * @param shopId 店铺ID
     * @return
     */
    @RequestMapping(value = "info")
    public Object info(Long shopId) {
        Map<String, Object> resultMap = shopService.info(shopId);
        return new SuccessDataTip(resultMap);
    }

}
