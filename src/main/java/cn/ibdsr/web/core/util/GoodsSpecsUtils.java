package cn.ibdsr.web.core.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * @Description: 商品规格转换工具类
 * @Version: V1.0
 * @CreateUser: xujincai
 * @CreateDate: 2019/3/20 9:21
 * <p>
 * Date Author Description
 * ------------------------------------------------------
 * 2019/3/20 xujincai init
 */
public class GoodsSpecsUtils {

    /**
     * 使用特定符号拼接商品规格值，如{"包装":"1KG","口味":"香辣"}转换为1KG,香辣
     *
     * @param goodsSpecsJson 商品规格JSONObject对象
     * @param separator      特定符号
     * @return String
     */
    public static String joinGoodsSpecsValue(JSONObject goodsSpecsJson, String separator) {
        Collection<Object> valueList = goodsSpecsJson.values();
        return StringUtils.join(valueList, separator);
    }

}
