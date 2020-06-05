package cn.ibdsr.web.modular.wechatlogin.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.web.common.constant.factory.ConstantFactory;
import cn.ibdsr.web.modular.wechatlogin.service.WechatLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import weixin.popular.api.SnsAPI;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * 微信端：公众号授权控制器
 *
 * @author xjc
 * @Date 2019-01-14 16:49:34
 */
@Controller
@RequestMapping("wechatlogin")
public class WechatLoginController extends BaseController {

    @Autowired
    private WechatLoginService wechatLoginService;

    /**
     * 客户端选择微信授权时，访问的接口
     *
     * @param request HttpServletRequest
     * @return String
     */
    @GetMapping("auth")
    public String auth(HttpServletRequest request, @RequestParam String url) throws UnsupportedEncodingException {
        String callbackUrl = ConstantFactory.me().getUrlProperties().getBathurl() + "/wechatlogin/callback?url=" + URLEncoder.encode(url, "utf-8"); // 设置回调接口
        String appID = ConstantFactory.me().getWXProperties().getAppid();
        String urls = SnsAPI.connectOauth2Authorize(appID, callbackUrl, true, "STATE");
        return "redirect:" + urls; // 重定向至获取code的微信接口
    }

    /**
     * 微信回调的接口，并携带code参数,存储用户信息，并放在cookie中
     *
     * @param code 授权code
     * @param url  重定向url
     * @return String
     */
    @GetMapping("callback")
    public String callback(String code, String url) {
        try {
            String userId = wechatLoginService.login(code);
            if (StringUtils.contains(url, "?")) {
                url = url + "&code=" + userId;
            } else {
                url = url + "?code=" + userId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:" + url;
    }

}
