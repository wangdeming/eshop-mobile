package cn.ibdsr.web.modular.shop.tourist.transfer;

import cn.ibdsr.core.base.dto.BaseDTO;
import cn.ibdsr.core.check.CheckUtil;
import cn.ibdsr.core.check.Verfication;


/**
 * @Description 游客管理 游客DTO
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public class UserTouristDTO extends BaseDTO {
    /**
     * 姓名
     */
    @Verfication(name = "游客姓名", notNull = true, maxlength = 15)
    private String name;
    /**
     * 身份证号码
     */
    @Verfication(name = "游客身份证号码", notNull = true, regx = {CheckUtil.ID_CARD, "格式不正确"})
    private String idCardNo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCardNo() {
        return idCardNo;
    }

    public void setIdCardNo(String idCardNo) {
        this.idCardNo = idCardNo;
    }
}
