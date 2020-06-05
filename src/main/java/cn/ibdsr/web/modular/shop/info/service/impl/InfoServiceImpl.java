package cn.ibdsr.web.modular.shop.info.service.impl;

import cn.ibdsr.core.check.StaticCheck;
import cn.ibdsr.core.support.BeanKit;
import cn.ibdsr.core.util.DateUtil;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.factory.IConstantFactory;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.core.util.DateUtils;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.shop.info.service.IInfoService;
import cn.ibdsr.web.modular.shop.info.transfer.UserInfoDTO;
import cn.ibdsr.web.modular.shop.info.transfer.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
  * @Description 个人信息ServiceImpl
  * @Version V1.0
  * @CreateDate 2019-03-19 17:01:03
  *
  * Date           Author               Description
  * ------------------------------------------------------
  * 2019/03/19    Wujiayun            类说明
  */
@Service
public class InfoServiceImpl implements IInfoService {
    @Autowired
    private UserMapper userMapper;
    /**
     * 获取个人信息列表
     */
    @Override
    public Map<String, Object> select(){
        String Province;
        String City;
        String District;
        Long userId = ConstantFactory.me().getUserId();
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER );//获取当前用户Id失败
         }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);//不存在该用户
         }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        userInfoVO.setAvatar(ImageUtil.setImageURL(userInfoVO.getAvatar()));
        if (ToolUtil.isEmpty(userInfoVO.getNickname())){
            userInfoVO.setNickname("请填写");
        }
        userInfoVO.setBirthday(DateUtils.birthdayDate(user.getBirthday()));
        Province = user.getProvince() == null ? "":user.getProvince();
        City = user.getCity() == null ? "":user.getCity();
        District = user.getDistrict() == null ? "":user.getDistrict();
        userInfoVO.setAddress(Province + "" + City + "" + District);
        userInfoVO.setProvinceId(user.getProvinceId() == null ?  Long.parseLong("0"):user.getProvinceId());
        userInfoVO.setCityId(user.getCityId() == null ? Long.parseLong("0"):user.getCityId());
        userInfoVO.setDistrictId(user.getDistrictId() == null ? Long.parseLong("0"):user.getDistrictId());
        if (ToolUtil.isEmpty(userInfoVO.getAddress())){
            userInfoVO.setAddress("请填写");
        }
        Map<String, Object> userInfoMap = BeanKit.beanToMap(userInfoVO);
        return userInfoMap;
    }

    /**
     * 修改个人信息
     *
     * @param userInfoDTO
     * @return
     */
    @Override
    public void  update(UserInfoDTO userInfoDTO){
        StaticCheck.check(userInfoDTO);
        //System.out.println(userInfoDTO);
        User user = new User();
        BeanUtils.copyProperties(userInfoDTO,user);
        //System.out.println(user);
        convertAreaId2Name(user);
        if (userInfoDTO.getAvatar() != null){
            user.setAvatar(ImageUtil.cutImageURL(userInfoDTO.getAvatar()));
        }
        user.setModifiedUser(ConstantFactory.me().getUserId());
        user.setModifiedTime(new Date());
        user.updateById();
    }

    private void convertAreaId2Name(User user) {
        IConstantFactory factory = ConstantFactory.me();
        if (ToolUtil.isNotEmpty(user.getProvinceId())) {
            user.setProvince(factory.getAreaNameById(user.getProvinceId()));
        }
        if (ToolUtil.isNotEmpty(user.getCityId())) {
            user.setCity(factory.getAreaNameById(user.getCityId()));
        }
        if (ToolUtil.isNotEmpty(user.getDistrictId())) {
            user.setDistrict(factory.getAreaNameById(user.getDistrictId()));
        }
    }


}
