/**
 * Copyright (c) 2015-2020, ShangRao Institute of Big Data co.,LTD and/or its
 * affiliates. All rights reserved.
 */
package cn.ibdsr.web.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description 整个系统关于 URL前缀的配置文件信息
 * @Version V1.0
 * @CreateDate 2018/2/27 17:00
 * <p>
 * Date           Author               Description
 * ------------------------------------------------------
 * 2018/2/27      xjc              类说明
 */
@Component
@ConfigurationProperties(prefix = "weixin")
public class WXProperties {

    private String appid;

    private String appsecret;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }
}
