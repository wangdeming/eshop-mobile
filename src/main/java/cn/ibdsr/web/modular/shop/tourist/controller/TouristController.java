package cn.ibdsr.web.modular.shop.tourist.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.model.UserTourist;
import cn.ibdsr.web.modular.shop.tourist.service.ITouristService;
import cn.ibdsr.web.modular.shop.tourist.transfer.UserTouristDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Description 游客控制器
 * @Version V1.0
 * @CreateDate 2019-03-18 13:55:19
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
@Controller
@RequestMapping("shop/personal/tourist")
public class TouristController extends BaseController {

    @Autowired
    private ITouristService iTouristService;

    private String PREFIX = "/shop/personal/tourist/";

    /**
     * 跳转到游客首页
     */
    @RequestMapping("")
    @WinXinLogin
    public String index() {
        return PREFIX + "tourist.html";
    }

    /**
     * 跳转到添加游客
     *
     */
    @RequestMapping("/toAdd")
    @ResponseBody
    @WinXinLogin
    public Object touristAdd() {
        Long userId = ConstantFactory.me().getUserId();
        if ((iTouristService.touristCount(userId)) >= 15){
            throw new BussinessException(BizExceptionEnum.TOURIST_ARRIVAL_CAP);
        }
       // return PREFIX + "tourist_add.html";
        return super.SUCCESS_TIP;
    }

    /**
     * 跳转到修改游客
     *
     * @param touristId 游客ID
     * @param
     * @return
     */
    @RequestMapping("/toEdit")
    @ResponseBody
    @WinXinLogin
    public Object touristUpdate(@Valid Long touristId) {
        //model.addAttribute("tourist",iTouristService.detail(touristId));
        UserTourist tourist = iTouristService.detail(touristId);
        return new SuccessDataTip(tourist);
    }

    /**
     * 获取游客列表
     *
     * @return
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @WinXinLogin
    public Object list() {
        Long userId = ConstantFactory.me().getUserId();
        List<Map<String, Object>> touristList = iTouristService.list(userId);
        return new SuccessDataTip(touristList);
    }

    /**
     * 新增游客
     *
     * @param touristDTO
     * @return
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    @WinXinLogin
    public Object add(@Valid UserTouristDTO touristDTO) {
        iTouristService.add(touristDTO);
        return super.SUCCESS_TIP;
    }

    /**
     * 删除游客
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @WinXinLogin
    public Object delete(@RequestParam Long touristId) {
        iTouristService.delete(touristId);
        return SUCCESS_TIP;
    }


    /**
     * 修改游客
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    @WinXinLogin
    public Object update(@Valid UserTouristDTO touristDTO) {
        iTouristService.update(touristDTO);
        return super.SUCCESS_TIP;
    }

}
