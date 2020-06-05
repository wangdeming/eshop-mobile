package cn.ibdsr.web.modular.shop.order.service;

import cn.ibdsr.web.modular.shop.order.transfer.refund.*;

import java.util.List;
import java.util.Map;

/**
 * @Description 订单商品退款Service
 * @Version V1.0
 * @CreateDate 2019-04-02 10:54:32
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-02 10:54:32    XuZhipeng               类说明
 *
 */
public interface IOrderRefundService {

    /**
     * 校验是否已存在售后服务，并返回订单商品信息
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    OrderGoodsDTO checkServiced(Long orderGoodsId);

    /**
     * 申请退款页面获取商品信息
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    ApplyRefundVO getGoodsInfo4Refund(Long orderGoodsId);

    /**
     * 申请退款
     *
     * @param applyRefundDTO 申请退款信息
     *                       orderGoodsId 订单商品ID
     *                       reasonId 退款原因
     *                       amount 退款金额
     *                       phone 联系手机
     *                       refundRemark 退款说明
     *                       imgList 凭证图片集合
     */
    void applyRefund(ApplyRefundDTO applyRefundDTO);

    /**
     * 退款详情
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    RefundDetailVO getRefundDetail(Long orderGoodsId);

    /**
     * 获取快递公司集合
     *
     * @return
     */
    List<Map<String,Object>> listExpressCompanys();

    /**
     * 填写退货物流
     *
     * @param logisticsDTO 申请退款信息
     *                     refundId 退款订单ID
     *                     expressCompany 物流公司
     *                     expressNo 物流单号
     *                     expressRemark 物流说明
     *                     imgList 凭证图片集合
     */
    void addLogistics(LogisticsDTO logisticsDTO);

    /**
     * 用户撤销
     *
     * @param refundId 退款订单ID
     */
    void revoke(Long refundId);
}
