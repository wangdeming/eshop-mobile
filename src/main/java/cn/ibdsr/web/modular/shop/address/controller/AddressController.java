package cn.ibdsr.web.modular.shop.address.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.modular.shop.address.service.IAddressService;
import cn.ibdsr.web.modular.shop.address.transfer.UserDeliveryAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Description 收货地址控制器
 * @Version V1.0
 * @CreateDate 2019-03-15 12:21:19
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/15    Wujiayun            类说明
 */
@RestController
@RequestMapping("/shop/personal/address")
public class AddressController extends BaseController {

    @Autowired
    private IAddressService iAddressService;


    private String PREFIX = "/shop/personal/address/";


    /**
     * 跳转到添加收货地址
     *
     */
    @RequestMapping("/toAdd")
    @WinXinLogin
    public Object addressAdd() {
        Long userId = ConstantFactory.me().getUserId();
        ;
        if ((iAddressService.addressCount(userId)) >= 15){
            throw new BussinessException(BizExceptionEnum.ADDRESS_ARRIVAL_CAP);
        }
        return super.SUCCESS_TIP;
    }

    /**
     * 跳转到修改收货地址
     *
     * @param addressId
     * @param
     * @return
     */
    @RequestMapping("/toEdit")
    @WinXinLogin
    public Object addressUpdate(@Valid Long addressId) {
        //model.addAttribute("address",iAddressService.detail(addressId));
        return iAddressService.detail(addressId);
    }

    /**
     * 获取收货地址列表
     *
     * @return
     */
    @RequestMapping(value = "/list")
    @WinXinLogin
    public Object list() {
        Long userId = ConstantFactory.me().getUserId();
        //Long userId = ConstantFactory.me().getUserId();
        List<Map<String, Object>> addressList = iAddressService.list(userId);
        return new SuccessDataTip(addressList);
    }

    /**
     * 新增收货地址
     *
     * @param addressDTO
     * @return
     */
    @RequestMapping(value = "/add")
    @WinXinLogin
    public Object add(@Valid UserDeliveryAddressDTO addressDTO) {
        iAddressService.add(addressDTO);
        return super.SUCCESS_TIP;
    }

    /**
     * 修改收货地址
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/update")
    @WinXinLogin
    public Object update(UserDeliveryAddressDTO addressDTO) {
        iAddressService.update(addressDTO);
        return super.SUCCESS_TIP;
    }

    /**
     * 删除收货地址
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/delete")
    @WinXinLogin
    public Object delete(@RequestParam Long addressId) {
        iAddressService.delete(addressId);
        return super.SUCCESS_TIP;
    }



    /**
     * 根据父地区ID获取区域集合
     *
     * @param pid 父地区ID（0-省份级别；）
     * @return
     */
    @RequestMapping(value = "/listAreasByPid")
    public Object listAreasByPid(@RequestParam Long pid) {
        if (pid == null) {
            throw new BussinessException(BizExceptionEnum.AREA_PID_IS_NULL);
        }
        List<Map<String, Object>> areaList = iAddressService.listAreasByPid(pid);
        return new SuccessDataTip(areaList);
    }

    /**
     * 获取地址区域集合
     * @return
     */
    @RequestMapping(value = "/listArea")
    public SuccessDataTip listAreas() {
        return iAddressService.listAreas();
    }

}
