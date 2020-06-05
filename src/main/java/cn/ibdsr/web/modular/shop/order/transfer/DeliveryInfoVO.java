package cn.ibdsr.web.modular.shop.order.transfer;

import java.util.Date;

/**
 * @Description 我的订单 配送信息VO
 * @Version V1.0
 * @CreateDate 2019-03-18 13:57:38
 *
 * Date           Author               Description
 * ------------------------------------------------------
 * 2019/03/18     Wujiayun            类说明
 */
public class DeliveryInfoVO {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 运输时间
     */
    private Date transportTime;
    /**
     * 运输状态
     */
    private Integer transportType;
    /**
     * 配送内容详情
     */
    private String transportContent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTransportTime() {
        return transportTime;
    }

    public void setTransportTime(Date transportTime) {
        this.transportTime = transportTime;
    }

    public Integer getTransportType() {
        return transportType;
    }

    public void setTransportType(Integer transportType) {
        this.transportType = transportType;
    }

    public String getTransportContent() {
        return transportContent;
    }

    public void setTransportContent(String transportContent) {
        this.transportContent = transportContent;
    }
}
