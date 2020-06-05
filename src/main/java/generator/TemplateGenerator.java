package generator;

import cn.ibdsr.web.core.template.config.ContextConfig;
import cn.ibdsr.web.core.template.engine.SimpleTemplateEngine;
import cn.ibdsr.web.core.template.engine.base.GunsTemplateEngine;

import java.io.IOException;

/**
 * 测试Guns模板引擎
 *
 * @author fengshuonan
 * @date 2017-05-09 20:27
 */
public class TemplateGenerator {

    public static void main(String[] args) throws IOException {
        ContextConfig contextConfig = new ContextConfig();
        contextConfig.setFileOverride(false);
        //生成java后端控制器代码
        contextConfig.setControllerSwitch(true);
        //生成java后端dao层代码
        contextConfig.setDaoSwitch(true);
        //生成java后端service层代码
        contextConfig.setServiceSwitch(true);
        //生成web前端主页页面
        //contextConfig.setIndexPageSwitch(true);
        //生成主页对应的添加页
        //contextConfig.setAddPageSwitch(false);
        //生成主页对应的编辑页
        //contextConfig.setEditPageSwitch(false);
        //生成主页对应的js
        //contextConfig.setJsSwitch(false);
        //生成添加页与编辑页共用的js
        //contextConfig.setInfoJsSwitch(false);


        contextConfig.setBizChName("订单");
        contextConfig.setBizEnName("order");
        contextConfig.setModuleName("personal/order");
        contextConfig.setProjectPath("C:/Users/zys/Desktop/eshop-mobile");



        GunsTemplateEngine gunsTemplateEngine = new SimpleTemplateEngine();
        gunsTemplateEngine.setContextConfig(contextConfig);
        gunsTemplateEngine.start();
    }

}
