package cn.ibdsr.web.modular.wechatlogin.service.impl;

import cn.ibdsr.core.util.security.SecurityUtil;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.core.util.ImageUtil;
import cn.ibdsr.web.modular.wechatlogin.service.WechatLoginService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.MimeTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

/**
 * 微信端：订单接口Service
 *
 * @author xjc
 * @Date 2019-01-14 16:51:46
 */
@Service
public class WechatLoginServiceImpl implements WechatLoginService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信用户登录
     *
     * @param code
     * @return
     */
    @Override
    public String login(String code) {
        if (code == null || StringUtils.isEmpty(code)) {
            throw new IllegalStateException("微信授权回调的接口携带code为空");
        }
        User wechatUser = callback(code);
        cn.ibdsr.web.common.persistence.model.User paramUser = new cn.ibdsr.web.common.persistence.model.User();
        paramUser.setWxOpenId(wechatUser.getOpenid());
        cn.ibdsr.web.common.persistence.model.User user = userMapper.selectOne(paramUser);
        Long id;
        if (user == null) {
            id = addUser(wechatUser);
        } else {
            id = user.getId();
        }
        user = new cn.ibdsr.web.common.persistence.model.User();
        user.setId(id);
        user.setWechatLoginDatetime(new Date());
        user.updateById();
        return SecurityUtil.encrypt(new String(Base64.encodeInteger(BigInteger.valueOf(id))));
    }

    /**
     * 新增用户
     */
    private Long addUser(User wechatUser) {
        cn.ibdsr.web.common.persistence.model.User user = new cn.ibdsr.web.common.persistence.model.User();
        user.setWxOpenId(wechatUser.getOpenid());
        user.setNickname(wechatUser.getNickname());
        user.setSex(wechatUser.getSex());
        user.setProvince(wechatUser.getProvince());
        user.setCity(wechatUser.getCity());
        user.setCountry(wechatUser.getCountry());
        try {
            user.setAvatar(ImageUtil.upload(wechatUser.getHeadimgurl()));
        } catch (IOException | MimeTypeException e) {
            e.printStackTrace();
        }
        //user.setPrivilege();
        user.setWxUnionId(wechatUser.getUnionid());
        user.setCreatedTime(new Date());
        userMapper.insert(user);
        return user.getId();
    }

    /**
     * 微信回调的接口,返回用户信息
     */
    private User callback(String code) {
        // 用code换access_token、openid
        String appID = ConstantFactory.me().getWXProperties().getAppid();
        String appSecret = ConstantFactory.me().getWXProperties().getAppsecret();
        SnsToken snsToken = SnsAPI.oauth2AccessToken(appID, appSecret, code);
        String errcode = snsToken.getErrcode();
        if (errcode != null && !"".equals(errcode)) {
            throw new IllegalStateException("获取access_token出错");
        }
        // 用access_token及openid获取微信用户基本信息
        User user = SnsAPI.userinfo(snsToken.getAccess_token(), snsToken.getOpenid(), "zh_CN", 1);
        if (user == null) {
            throw new IllegalStateException("获取用户信息出错");
        }
        String errcode1 = user.getErrcode();
        if (errcode1 != null && !"".equals(errcode1)) {
            throw new IllegalStateException("获取用户信息出错");
        }
        return user;
    }

}
