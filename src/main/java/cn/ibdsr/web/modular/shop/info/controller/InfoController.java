package cn.ibdsr.web.modular.shop.info.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.modular.shop.info.service.IInfoService;
import cn.ibdsr.web.modular.shop.info.transfer.UserInfoDTO;
import cn.ibdsr.web.modular.shop.info.warpper.PersonalInfoWarpper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
  * @Description 个人信息控制器
  * @Version V1.0
  * @CreateDate 2019-03-19 17:01:03
  *
  * Date           Author               Description
  * ------------------------------------------------------
  * 2019/03/19    Wujiayun            类说明
  */
@RestController
@RequestMapping("shop/personal/info")
public class InfoController extends BaseController {

    private String PREFIX = "/shop/personal/info/";

    @Autowired
    private IInfoService iInfoService;

    /**
     * 获取个人信息
     */
    @RequestMapping(value = "select")
    @WinXinLogin
    public Object select() {
        Map<String, Object> infoList = iInfoService.select();
        return new PersonalInfoWarpper(infoList).warp();
    }

    /**
     * 修改个人信息
     *
     * @param userInfoDTO
     * @return
     */
    @RequestMapping(value = "update")
    @WinXinLogin
    public Object update(UserInfoDTO userInfoDTO) {
        iInfoService.update(userInfoDTO);
        return super.SUCCESS_TIP;
    }

}
