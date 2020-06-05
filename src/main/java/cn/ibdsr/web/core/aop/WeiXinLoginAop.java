package cn.ibdsr.web.core.aop;


import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.support.HttpKit;
import cn.ibdsr.core.util.security.SecurityUtil;
import cn.ibdsr.web.common.constant.Const;
import cn.ibdsr.web.common.constant.state.SuccessStatus;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.core.util.DateTimeUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信登录拦截器
 *
 * @author xjc
 * @date 2019年11月17日 下午14:48:30
 */
@Aspect
@Component
public class WeiXinLoginAop {

    @Autowired
    private Environment environment;

    @Autowired
    private UserMapper userMapper;

    @Pointcut(value = "@annotation(cn.ibdsr.web.common.annotion.WinXinLogin)")
    private void pointcut() {
    }

    @Around("pointcut()")
    public Object Aroundmothod(ProceedingJoinPoint point) throws Throwable {
        if (StringUtils.equals("test", environment.getProperty("spring.profiles.active"))) {
            //测试环境
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("autoLogin", Boolean.FALSE);
            SuccessDataTip tip = new SuccessDataTip(9999, SuccessStatus.LOGIN_FAILURE.getMessage(), dataMap);

            HttpServletRequest request = HttpKit.getRequest();
            String code = request.getHeader("X-Nideshop-Token");
            //需登录接口前端未传code则未登录
            if (StringUtils.isBlank(code)) {
                return tip;
            }
            Long userId = Base64.decodeInteger(SecurityUtil.decrypt(code).getBytes()).longValue();
            User user = userMapper.selectById(userId);
            //用户不存在或没有登录时间则用户未登录
            if (user == null || user.getWechatLoginDatetime() == null) {
                return tip;
            }
            LocalDateTime wechatLoginDateTime = DateTimeUtils.toLocalDateTime(user.getWechatLoginDatetime());
            if (wechatLoginDateTime.plusSeconds(Const.WECHAT_LOGIN_DATETIME_MAX).isBefore(LocalDateTime.now())) {
                return tip;
            }
            return point.proceed();
        } else if (StringUtils.equals("dev", environment.getProperty("spring.profiles.active"))) {
            //开发环境
            return point.proceed();
        } else {
            //环境异常
            throw new BussinessException(BizExceptionEnum.SPRING_PROFILES_ACTIVE_ERROR);
        }
    }

}
