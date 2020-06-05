package cn.ibdsr.web.modular.shop.record.service;

/**
 * @Description: 类功能和用法的说明
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/5/27 10:43
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/5/27 xujincai init
 */
public interface RecordService {
    void viewShop(Long shopId);

    void viewGoods(Long goodsId);

    void viewRoom(Long roomId);

    void payGoods(Long goodsId, Integer number);

    void payRoom(Long roomId, Integer number);
}
