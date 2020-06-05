/**
 * Copyright (c) 2015-2020, ShangRao Institute of Big Data co.,LTD and/or its
 * affiliates. All rights reserved.
 */
package cn.ibdsr.web.modular.shop.home.transfer;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateDate: 2019/3/22 13:45
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/3/22      Zhujingrui               类说明
 *
 */
public class GoodsCategoryVO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 类别名称
     */
    private String name;

    /**
     * 前台展示名称
     */
    private String frontName;

    /**
     * 图标图片路径
     */
    private String iconImg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrontName() {
        return frontName;
    }

    public void setFrontName(String frontName) {
        this.frontName = frontName;
    }

    public String getIconImg() {
        return iconImg;
    }

    public void setIconImg(String iconImg) {
        this.iconImg = iconImg;
    }
}
