/**
 * Copyright (c) 2015-2020, ShangRao Institute of Big Data co.,LTD and/or its
 * affiliates. All rights reserved.
 */
package cn.ibdsr.web.modular.shop.home.transfer;

import cn.ibdsr.core.base.dto.BaseDTO;

import java.util.Date;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateDate: 2019/3/25 15:23
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/3/25      Zhujingrui               类说明
 *
 */
public class SearchHistoryDTO extends BaseDTO {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 搜索类型（1-商品；2-店铺；）
     */
    private Integer type;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 创建人
     */
    private Long createdUser;

    /**
     * 创建时间
     */
    private Date createdTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(Long createdUser) {
        this.createdUser = createdUser;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
