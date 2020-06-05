package cn.ibdsr.web.modular.shop.order.service;

import cn.ibdsr.core.base.tips.SuccessDataTip;
import cn.ibdsr.core.base.tips.SuccessTip;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 微信端：订单接口Service
 *
 * @author xjc
 * @Date 2019-01-14 16:49:34
 */
public interface ShopPayService {
    SuccessDataTip pay(Long shopOrderId, int payType, String wechatPayType) throws Exception;

    JSONObject getChannel(List<Long> shopOrderIdList, Integer payTypeId, String wechatPayType) throws Exception;

    SuccessTip orderquery(String actualOutTradeNo) throws Exception;

    void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
