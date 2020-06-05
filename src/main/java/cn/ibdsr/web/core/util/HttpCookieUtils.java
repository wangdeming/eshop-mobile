package cn.ibdsr.web.core.util;

import cn.ibdsr.core.support.HttpKit;
import cn.ibdsr.core.util.HttpCookieHolder;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;

/**
 * @Description: Cookie工具类
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/4/10 15:41
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/4/10 xujincai init
 */
public class HttpCookieUtils extends HttpCookieHolder {
    public static void setCookie(String name, String value, int seconds_age, String domain, String cookiePath) {
        Cookie cookie = new Cookie(name, value);
        if (seconds_age != 0) {
            cookie.setMaxAge(seconds_age);
        }
        if (domain != null && !StringUtils.isEmpty(domain)) {
            cookie.setDomain(getCookieDomain(domain));
        }
        cookie.setPath(cookiePath);
        HttpKit.getResponse().addCookie(cookie);
    }
}
