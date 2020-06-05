package cn.ibdsr.web.modular.shop.info.warpper;

import cn.ibdsr.core.base.warpper.BaseControllerWarpper;
import cn.ibdsr.web.common.constant.state.SexType;

import java.util.Map;

public class PersonalInfoWarpper extends BaseControllerWarpper {

    public PersonalInfoWarpper(Object list){
        super(list);
    }
    @Override
    public void warpTheMap(Map<String, Object> map) {

        // 店铺类型名称
        map.put("sexName", SexType.valueOf(Integer.valueOf(map.get("sex").toString())));
    }
}
