package cn.ibdsr.web.modular.shop.address.service.impl;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.check.StaticCheck;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.factory.IConstantFactory;
import cn.ibdsr.web.common.constant.state.IsDefault;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.AreaMapper;
import cn.ibdsr.web.common.persistence.dao.UserDeliveryAddressMapper;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.model.Area;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.common.persistence.model.UserDeliveryAddress;
import cn.ibdsr.web.modular.shop.address.dao.AddressDao;
import cn.ibdsr.web.modular.shop.address.service.IAddressService;
import cn.ibdsr.web.modular.shop.address.transfer.UserDeliveryAddressDTO;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Description 收货地址ServiceImpl
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    private AddressDao addressDao;

    @Autowired
    private UserDeliveryAddressMapper addressMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public List<Map<String,Object>> list(Long userId){
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER );//获取当前用户Id失败
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);//不存在该用户
        }
        List<Map<String,Object>> address = addressDao.list(userId);
        if (address == null || ToolUtil.isEmpty(address)){
            throw new BussinessException(BizExceptionEnum.ADDRESS_IS_NOT_EXIST );//该用户暂无收获地址，请添加收货地址
        }
        return address;
    }

    @Override
    public  void add(UserDeliveryAddressDTO addressDTO){
        if ((addressCount(ConstantFactory.me().getUserId())) >= 15){
            throw new BussinessException(BizExceptionEnum.ADDRESS_ARRIVAL_CAP);
        }
        StaticCheck.check(addressDTO);
        Long userId = ConstantFactory.me().getUserId();
        checkUserId(userId);
        List<UserDeliveryAddress> addresses = addressMapper.selectList(
                new EntityWrapper<UserDeliveryAddress>()
                        .eq("user_id", userId).eq("is_deleted",IsDeleted.NORMAL.getCode())
        );
        if(addresses.size() == 0){
            addressDTO.setIsDefault(IsDefault.Y.getCode());
        }
        checkDefault(addressDTO,userId);
        UserDeliveryAddress address = new UserDeliveryAddress();
        BeanUtils.copyProperties(addressDTO,address);
        convertAreaId2Name(address);
        address.setUserId(userId);
        address.setCreatedUser(userId);
        address.setCreatedTime(new Date());
        address.setIsDeleted(IsDeleted.NORMAL.getCode());
        address.insert();

    }

    @Override
    public UserDeliveryAddress detail(Long addressId){
        if (ToolUtil.isEmpty(addressId)){
            throw new BussinessException(BizExceptionEnum.ADDRESS_ID_IS_NULL );
        }
        UserDeliveryAddress address = addressMapper.selectById(addressId);
        if (address == null){
            throw new BussinessException(BizExceptionEnum.ADDRESS_IS_NOT_EXIST);
        }
        return address;
    }

    @Override
    public void update(UserDeliveryAddressDTO addressDTO){
        StaticCheck.check(addressDTO);
        Long userId = ConstantFactory.me().getUserId();
        checkUserId(userId);
        if (addressDTO.getIsDefault() == null){
            addressDTO.setIsDefault(IsDefault.Y.getCode());
        }
        checkDefault(addressDTO,userId);
        UserDeliveryAddress address = new UserDeliveryAddress();
        BeanUtils.copyProperties(addressDTO,address);
        convertAreaId2Name(address);
        address.setUserId(userId);
        address.setModifiedUser(userId);
        address.setModifiedTime(new Date());
        address.updateById();

    }

    @Override
    public void delete(Long addressId){
        UserDeliveryAddress address = detail(addressId);
        Long userId = ConstantFactory.me().getUserId();
        checkUserId(userId);
        if (address.getIsDefault() == IsDefault.Y.getCode()){
            List<UserDeliveryAddress> addresses = addressMapper.selectList(
                    new EntityWrapper<UserDeliveryAddress>()
                            .eq("user_id", userId)
                            .eq("is_default", IsDefault.N.getCode())
                            .eq("is_deleted", IsDeleted.NORMAL.getCode())
                            .orderBy("created_time")
                            .last("LIMIT 1")
            );
            if(addresses != null && addresses.size() > 0){
                for (UserDeliveryAddress deliveryAddress: addresses){
                    deliveryAddress.setIsDefault(IsDefault.Y.getCode());
                    deliveryAddress.setModifiedUser(userId);
                    deliveryAddress.setModifiedTime(new Date());
                    deliveryAddress.updateById();
                }
            }

        }
        address.setModifiedUser(userId);
        address.setModifiedTime(new Date());
        address.setIsDeleted(IsDeleted.DELETED.getCode());
        address.updateById();

    }

    /**
     * 统计当前用户收货地址数量
     * @param userId 用户ID
     * @return
     */
    @Override
    public int addressCount(Long userId){
        checkUserId(userId);
        return addressDao.addressCount(userId);
    }

    /**
     * 根据父地区ID获取区域集合
     *
     * @param pid 父区域ID
     * @return
     */
    @Override
    public List<Map<String, Object>> listAreasByPid(Long pid) {
        List<Map<String, Object>> areaList = areaMapper.selectMaps(
                new EntityWrapper<Area>().setSqlSelect("id, name").eq("pid", pid));
        return areaList;
    }

    @Override
    public SuccessDataTip listAreas() {
        Wrapper<Area> areaWrapper=new EntityWrapper<>();
        areaWrapper.setSqlSelect("id value","name text");
        areaWrapper.eq("pid",0);
        List<Map<String ,Object>> provinceList=areaMapper.selectMaps(areaWrapper);
        for(Map<String ,Object> province:provinceList){
            areaWrapper=new EntityWrapper<>();
            areaWrapper.setSqlSelect("id value","name text");
            areaWrapper.eq("pid",province.get("value"));
            List<Map<String ,Object>> cityList=areaMapper.selectMaps(areaWrapper);
            for(Map<String,Object> city:cityList){
                areaWrapper=new EntityWrapper<>();
                areaWrapper.setSqlSelect("id value","name text");
                areaWrapper.eq("pid",city.get("value"));
                List<Map<String ,Object>> districtList=areaMapper.selectMaps(areaWrapper);
                city.put("children",districtList);
            }
            province.put("children",cityList);
        }
        return new SuccessDataTip(provinceList);
    }

    /**
     * 校验userId是否存在
     *
     * @param userId 用户ID
     * @return
     */

    private User checkUserId(Long userId) {
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER );//获取当前用户Id失败
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);//不存在该用户
        }
        return user;
    }

    /**
     * 默认地址唯一性
     * @param
     */
    private void checkDefault(UserDeliveryAddressDTO addressDTO,Long userId){
            if(addressDTO.getIsDefault() == IsDefault.Y.getCode()){
                List<UserDeliveryAddress> addresses = addressMapper.selectList(
                        new EntityWrapper<UserDeliveryAddress>()
                                .eq("user_id", userId).eq("is_default", IsDefault.Y.getCode()).eq("is_deleted",IsDeleted.NORMAL.getCode())
                );
                if(addresses != null && addresses.size() > 0){
                    for (UserDeliveryAddress address: addresses){
                        address.setIsDefault(IsDefault.N.getCode());
                        address.setModifiedUser(userId);
                        address.setModifiedTime(new Date());
                        address.updateById();
                    }
                }

        }

    }

    private void convertAreaId2Name(UserDeliveryAddress address) {
        IConstantFactory factory = ConstantFactory.me();
        address.setProvince(factory.getAreaNameById(address.getProvinceId()));
        address.setCity(factory.getAreaNameById(address.getCityId()));
        address.setDistrict(factory.getAreaNameById(address.getDistrictId()));
    }

}
