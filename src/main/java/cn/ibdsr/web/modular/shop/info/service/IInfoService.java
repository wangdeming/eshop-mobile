package cn.ibdsr.web.modular.shop.info.service;

import cn.ibdsr.web.modular.shop.info.transfer.UserInfoDTO;

import java.util.Map;

/**
  * @Description 个人信息Service
  * @Version V1.0
  * @CreateDate 2019-03-19 17:01:03
  *
  * Date           Author               Description
  * ------------------------------------------------------
  * 2019/03/19    Wujiayun            类说明
  */
public interface IInfoService {

    /**
     * 获取个人信息
     */
    Map<String, Object> select();

    /**
     * 修改个人信息
     *
     * @param userInfoDTO
     * @return
     */
    void update(UserInfoDTO userInfoDTO);
}
