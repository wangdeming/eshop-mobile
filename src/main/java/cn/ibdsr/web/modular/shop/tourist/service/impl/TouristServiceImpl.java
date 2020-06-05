package cn.ibdsr.web.modular.shop.tourist.service.impl;

import cn.ibdsr.core.check.StaticCheck;
import cn.ibdsr.core.util.LoginContextHolder;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.constant.state.IsDeleted;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.dao.UserTouristMapper;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.common.persistence.model.UserTourist;
import cn.ibdsr.web.modular.shop.tourist.dao.TouristDao;
import cn.ibdsr.web.modular.shop.tourist.transfer.UserTouristDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.ibdsr.web.modular.shop.tourist.service.ITouristService;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Description 游客ServiceImpl
 * @Version V1.0
 * @CreateDate 2019-03-18 13:55:19
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
@Service
public class TouristServiceImpl implements ITouristService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TouristDao touristDao;

    @Autowired
    private UserTouristMapper userTouristMapper;

    /**
     * 获取游客列表
     * @param userId 用户ID
     * @return
     */
    @Override
    public List<Map<String,Object>> list(Long userId){
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER );//获取当前用户Id失败
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);//不存在该用户
        }
        List<Map<String,Object>> tourist = touristDao.list(userId);
        if (tourist == null || ToolUtil.isEmpty(tourist)){
            throw new BussinessException(BizExceptionEnum.TOURIST_IS_NOT_EXIST );//该用户暂无游客，请添加游客
        }
        return tourist;
    }
    /**
     * 新增游客
     *
     * @param touristDTO
     * @return
     */
    @Override
    public void add(UserTouristDTO touristDTO){
        if ((touristCount(ConstantFactory.me().getUserId())) >= 15){
            throw new BussinessException(BizExceptionEnum.TOURIST_ARRIVAL_CAP);
        }
        StaticCheck.check(touristDTO);
        UserTourist tourist = new UserTourist();
        BeanUtils.copyProperties(touristDTO,tourist);
        tourist.setUserId(ConstantFactory.me().getUserId());
        tourist.setCreatedUser(ConstantFactory.me().getUserId());
        tourist.setCreatedTime(new Date());
        tourist.setIsDeleted(IsDeleted.NORMAL.getCode());
        tourist.insert();
    }

    /**
     * 游客详情
     * @param touristId
     * @return
     */
    @Override
    public UserTourist detail(Long touristId){
        if (ToolUtil.isEmpty(touristId)){
            throw new BussinessException(BizExceptionEnum.TOURIST_ID_IS_NULL );
        }
        UserTourist tourist = userTouristMapper.selectById(touristId);
        if (tourist == null){
            throw new BussinessException(BizExceptionEnum.TOURIST_IS_NOT_EXIST);
        }
        return tourist;
    }

    /**
     * 修改游客
     *
     * @param touristDTO
     * @return
     */
    @Override
    public void update(UserTouristDTO touristDTO){
        StaticCheck.check(touristDTO);
        UserTourist tourist = new UserTourist();
        BeanUtils.copyProperties(touristDTO,tourist);
        tourist.setUserId(ConstantFactory.me().getUserId());
        tourist.setModifiedUser(ConstantFactory.me().getUserId());
        tourist.setModifiedTime(new Date());
        tourist.updateById();
    }

    /**
     * 删除游客
     *
     * @param touristId 游客ID
     * @return
     */
    @Override
    public void delete(Long touristId){
        UserTourist tourist = detail(touristId);
        tourist.setModifiedUser(ConstantFactory.me().getUserId());
        tourist.setModifiedTime(new Date());
        tourist.setIsDeleted(IsDeleted.DELETED.getCode());
        tourist.updateById();
    }


    /**
     * 统计当前用户的游客数量
     * @param userId 用户ID
     * @return
     */
    @Override
    public Integer touristCount(Long userId){
        if (ToolUtil.isEmpty(userId)){
            throw new BussinessException(BizExceptionEnum.NO_THIS_USER );//获取当前用户Id失败
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException(BizExceptionEnum.USER_NOT_EXISTED);//不存在该用户
        }
        return touristDao.touristCount(userId);
    }

}
