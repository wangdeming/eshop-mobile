package cn.ibdsr.web.modular.shop.info.transfer;

import cn.ibdsr.core.base.dto.BaseDTO;
import cn.ibdsr.core.check.CheckUtil;
import cn.ibdsr.core.check.Verfication;
import cn.ibdsr.web.common.persistence.model.Area;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * @Description 个人信息管理 用户信息DTO
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public class UserInfoDTO extends BaseDTO {
    /**
     * 头像
     */
    @Verfication(name = "头像", regx = {CheckUtil.URL, "格式不正确"})
    private String avatar;
    /**
     * 昵称
     */
    @Verfication(name = "昵称", maxlength = 15)
    private String nickname;
    /**
     * 性别（0-保密；1-男；2-女；）
     */
    @Verfication(name = "性别")
    private Integer sex;
    /**
     * 生日
     */

    private Date birthday;
    /**
     * 地区：省ID
     */
    @Verfication(name = "收货地址省份", forenign = Area.class)
    private Long provinceId;
    /**
     * 地区：市ID
     */
    @Verfication(name = "收货地址城市", forenign = Area.class)
    private Long cityId;
    /**
     * 地区：区ID
     */
    @Verfication(name = "收货地址县区", forenign = Area.class)
    private Long districtId;



    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

}
