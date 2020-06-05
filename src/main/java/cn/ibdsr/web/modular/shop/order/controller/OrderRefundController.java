package cn.ibdsr.web.modular.shop.order.controller;

import cn.ibdsr.core.base.controller.BaseController;
import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.web.common.annotion.WinXinLogin;
import cn.ibdsr.web.core.mq.rabbitmq.MessageSenderTask;
import cn.ibdsr.web.core.util.CommonUtils;
import cn.ibdsr.web.modular.shop.order.service.IOrderRefundService;
import cn.ibdsr.web.modular.shop.order.transfer.refund.ApplyRefundDTO;
import cn.ibdsr.web.modular.shop.order.transfer.refund.ApplyRefundVO;
import cn.ibdsr.web.modular.shop.order.transfer.refund.LogisticsDTO;
import cn.ibdsr.web.modular.shop.order.transfer.refund.RefundDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 订单商品退款控制器
 * @Version V1.0
 * @CreateDate 2019-04-02 10:54:32
 *
 * Date                     Author               Description
 * ----------------------------------------------------------
 * 2019-04-02 10:54:32    XuZhipeng               类说明
 *
 */
@RestController
@RequestMapping("shop/orderRefund")
public class OrderRefundController extends BaseController {

    @Autowired
    private IOrderRefundService orderRefundService;

    @Autowired
    private MessageSenderTask messageSenderTask;

    /**
     * 校验是否已存在售后服务，且申请次数不超过5次
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    @RequestMapping(value = "checkServiced")
    @WinXinLogin
    public Object checkServiced(Long orderGoodsId) {
        orderRefundService.checkServiced(orderGoodsId);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("isRefundable", true);
        return new SuccessDataTip(resultMap);
    }

    /**
     * 申请退款页面获取商品信息
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    @RequestMapping(value = "getGoodsInfo4Refund")
    @WinXinLogin
    public Object getGoodsInfo4Refund(Long orderGoodsId) {
        ApplyRefundVO applyRefundVO = orderRefundService.getGoodsInfo4Refund(orderGoodsId);
        return new SuccessDataTip(applyRefundVO);
    }

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
     * @return
     */
    @RequestMapping(value = "applyRefund")
    @WinXinLogin
    public Object applyRefund(ApplyRefundDTO applyRefundDTO) {
        orderRefundService.applyRefund(applyRefundDTO);
        return super.SUCCESS_TIP;
    }

    /**
     * 退款详情
     *
     * @param orderGoodsId 订单商品ID
     * @return
     */
    @RequestMapping(value = "getRefundDetail")
    @WinXinLogin
    public Object getRefundDetail(Long orderGoodsId) {
        RefundDetailVO refundDetailVO = orderRefundService.getRefundDetail(orderGoodsId);
        return new SuccessDataTip(CommonUtils.cleanData(refundDetailVO));
    }

    /**
     * 获取快递公司集合
     *
     * @return
     */
    @RequestMapping(value = "listExpressCompanys")
    @WinXinLogin
    public Object listExpressCompanys() {
        List<Map<String, Object>> resultList = orderRefundService.listExpressCompanys();
        return new SuccessDataTip(resultList);
    }

    /**
     * 填写退货物流
     *
     * @param logisticsDTO 申请退款信息
     *                     refundId 退款订单ID
     *                     expressCompany 物流公司
     *                     expressNo 物流单号
     *                     expressRemark 物流说明
     *                     imgList 凭证图片集合
     * @return
     */
    @RequestMapping(value = "addLogistics")
    @WinXinLogin
    public Object addLogistics(LogisticsDTO logisticsDTO) {
        orderRefundService.addLogistics(logisticsDTO);
        return super.SUCCESS_TIP;
    }

    /**
     * 用户撤销
     *
     * @param refundId 退款订单ID
     * @return
     */
    @RequestMapping(value = "revoke")
    @WinXinLogin
    public Object revoke(Long refundId) {
        orderRefundService.revoke(refundId);
        return super.SUCCESS_TIP;
    }

    /**
     * 测试发送消息
     *
     * @return
     */
    @RequestMapping(value = "sendMsg")
    @WinXinLogin
    public Object sendMsg() {
        // 发送消息

        // 放入消息队列，处理订单支付超时，1小时后自动取消
        List<Long> shopOrderIdList = new ArrayList<>();
        shopOrderIdList.add(94L);
        messageSenderTask.sendMsgOfCancelShopOrder(shopOrderIdList);
        return super.SUCCESS_TIP;
    }

}
