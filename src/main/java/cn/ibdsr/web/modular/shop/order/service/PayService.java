package cn.ibdsr.web.modular.shop.order.service;

import cn.ibdsr.web.modular.shop.order.payment.Payment;

/**
 * @Description 类功能和用法的说明
 * @Version V1.0
 * @CreateDate 2018/6/20 16:08
 * <p>
 * Date           Author               Description
 * ------------------------------------------------------
 * 2018/6/20       xjc                类说明
 */
public interface PayService {
    Object getChannel(Payment payment) throws Exception;

    Object refundChannel(Payment payment) throws Exception;
}
