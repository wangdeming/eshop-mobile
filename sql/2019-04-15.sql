ALTER TABLE `shop_order_refund`
DROP COLUMN `goods_num`,
ADD COLUMN `goods_num`  int(11) NULL DEFAULT NULL COMMENT '售后商品数量' AFTER `other_reason`;
