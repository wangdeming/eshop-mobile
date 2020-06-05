package cn.ibdsr.web.common.constant.factory;

import cn.ibdsr.core.log.LogObjectHolder;
import cn.ibdsr.core.support.HttpKit;
import cn.ibdsr.core.support.StrKit;
import cn.ibdsr.core.util.Convert;
import cn.ibdsr.core.util.SpringContextHolder;
import cn.ibdsr.core.util.ToolUtil;
import cn.ibdsr.core.util.security.SecurityUtil;
import cn.ibdsr.web.common.constant.state.ManagerStatus;
import cn.ibdsr.web.common.constant.state.MenuStatus;
import cn.ibdsr.web.common.exception.BizExceptionEnum;
import cn.ibdsr.web.common.exception.BussinessException;
import cn.ibdsr.web.common.persistence.dao.AreaMapper;
import cn.ibdsr.web.common.persistence.dao.DeptMapper;
import cn.ibdsr.web.common.persistence.dao.DictMapper;
import cn.ibdsr.web.common.persistence.dao.MenuMapper;
import cn.ibdsr.web.common.persistence.dao.NoticeMapper;
import cn.ibdsr.web.common.persistence.dao.RoleMapper;
import cn.ibdsr.web.common.persistence.dao.UserMapper;
import cn.ibdsr.web.common.persistence.model.Area;
import cn.ibdsr.web.common.persistence.model.Dept;
import cn.ibdsr.web.common.persistence.model.Dict;
import cn.ibdsr.web.common.persistence.model.Menu;
import cn.ibdsr.web.common.persistence.model.Notice;
import cn.ibdsr.web.common.persistence.model.Role;
import cn.ibdsr.web.common.persistence.model.User;
import cn.ibdsr.web.config.properties.UrlProperties;
import cn.ibdsr.web.config.properties.WXProperties;
import cn.ibdsr.web.config.properties.WXpayProperties;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 常量的生产工厂
 *
 * @author fengshuonan
 * @date 2017年2月13日 下午10:55:21
 */
@Component
@DependsOn("springContextHolder")
public class ConstantFactory implements IConstantFactory {

    private RoleMapper roleMapper = SpringContextHolder.getBean(RoleMapper.class);
    private DeptMapper deptMapper = SpringContextHolder.getBean(DeptMapper.class);
    private DictMapper dictMapper = SpringContextHolder.getBean(DictMapper.class);
    private UserMapper userMapper = SpringContextHolder.getBean(UserMapper.class);
    private MenuMapper menuMapper = SpringContextHolder.getBean(MenuMapper.class);
    private NoticeMapper noticeMapper = SpringContextHolder.getBean(NoticeMapper.class);
    private AreaMapper areaMapper = SpringContextHolder.getBean(AreaMapper.class);
    private WXProperties wxProperties = SpringContextHolder.getBean(WXProperties.class);
    private WXpayProperties wxpayProperties = SpringContextHolder.getBean(WXpayProperties.class);
    private UrlProperties urlProperties = SpringContextHolder.getBean(UrlProperties.class);

    @Autowired
    private Environment environment;

    public static IConstantFactory me() {
        return SpringContextHolder.getBean("constantFactory");
    }

    /**
     * 从Request中获取code，解密出用户ID
     *
     * @return 用户ID
     */
    public Long getUserId() {
        if (StringUtils.equals("test", environment.getProperty("spring.profiles.active"))) {
            //测试环境
            String code = HttpKit.getRequest().getHeader("X-Nideshop-Token");
            if (StringUtils.isBlank(code)) {
                return null;
            }
            return Base64.decodeInteger(SecurityUtil.decrypt(code).getBytes()).longValue();
        } else if (StringUtils.equals("dev", environment.getProperty("spring.profiles.active"))) {
            //开发环境
            return Long.valueOf(environment.getProperty("userid"));
        } else {
            //环境异常
            throw new BussinessException(BizExceptionEnum.SPRING_PROFILES_ACTIVE_ERROR);
        }
    }


    /**
     * 根据用户id获取用户名称
     *
     * @author stylefeng
     * @Date 2017/5/9 23:41
     */
    @Override
    public String getUserNameById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user != null) {
            return user.getNickname();
        } else {
            return "--";
        }
    }

    /**
     * 通过角色ids获取角色名称
     */
    @Override
    public String getRoleName(String roleIds) {
        Long[] roles = Convert.toLongArray(roleIds);
        StringBuilder sb = new StringBuilder();
        for (Long role : roles) {
            Role roleObj = roleMapper.selectById(role);
            if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
                sb.append(roleObj.getName()).append(",");
            }
        }
        return StrKit.removeSuffix(sb.toString(), ",");
    }

    /**
     * 通过角色id获取角色名称
     */
    @Override
    public String getSingleRoleName(Long roleId) {
        if (0 == roleId) {
            return "--";
        }
        Role roleObj = roleMapper.selectById(roleId);
        if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
            return roleObj.getName();
        }
        return "";
    }

    /**
     * 通过角色id获取角色英文名称
     */
    @Override
    public String getSingleRoleTip(Long roleId) {
        if (0L == roleId) {
            return "--";
        }
        Role roleObj = roleMapper.selectById(roleId);
        if (ToolUtil.isNotEmpty(roleObj) && ToolUtil.isNotEmpty(roleObj.getName())) {
            return roleObj.getTips();
        }
        return "";
    }

    /**
     * 获取部门名称
     */
    @Override
    public String getDeptName(Long deptId) {
        Dept dept = deptMapper.selectById(deptId);
        if (ToolUtil.isNotEmpty(dept) && ToolUtil.isNotEmpty(dept.getFullname())) {
            return dept.getFullname();
        }
        return "";
    }

    /**
     * 获取菜单的名称们(多个)
     */
    @Override
    public String getMenuNames(String menuIds) {
        Long[] menus = Convert.toLongArray(menuIds);
        StringBuilder sb = new StringBuilder();
        for (Long menu : menus) {
            Menu menuObj = menuMapper.selectById(menu);
            if (ToolUtil.isNotEmpty(menuObj) && ToolUtil.isNotEmpty(menuObj.getName())) {
                sb.append(menuObj.getName()).append(",");
            }
        }
        return StrKit.removeSuffix(sb.toString(), ",");
    }

    /**
     * 获取菜单名称
     */
    @Override
    public String getMenuName(Long menuId) {
        if (ToolUtil.isEmpty(menuId)) {
            return "";
        } else {
            Menu menu = menuMapper.selectById(menuId);
            if (menu == null) {
                return "";
            } else {
                return menu.getName();
            }
        }
    }

    /**
     * 获取菜单名称通过编号
     */
    @Override
    public String getMenuNameByCode(String code) {
        if (ToolUtil.isEmpty(code)) {
            return "";
        } else {
            Menu param = new Menu();
            param.setCode(code);
            Menu menu = menuMapper.selectOne(param);
            if (menu == null) {
                return "";
            } else {
                return menu.getName();
            }
        }
    }

    /**
     * 获取字典名称
     */
    @Override
    public String getDictName(Long dictId) {
        if (ToolUtil.isEmpty(dictId)) {
            return "";
        } else {
            Dict dict = dictMapper.selectById(dictId);
            if (dict == null) {
                return "";
            } else {
                return dict.getName();
            }
        }
    }

    /**
     * 根据字典名称和获取对应的字典集合
     *
     * @param name 字典主名称
     * @return
     */
    @Override
    public List<Map<String, Object>> getSubListByName(String name) {
        Dict query = new Dict();
        query.setName(name);
        Dict dict = dictMapper.selectOne(query);
        if (dict == null) {
            return null;
        } else {
            Wrapper<Dict> wrapper = new EntityWrapper<>();
            wrapper = wrapper.setSqlSelect("num, name").eq("pid", dict.getId());
            List<Map<String, Object>> dicts = dictMapper.selectMaps(wrapper);
            return dicts;
        }
    }

    /**
     * 获取通知标题
     */
    @Override
    public String getNoticeTitle(Long dictId) {
        if (ToolUtil.isEmpty(dictId)) {
            return "";
        } else {
            Notice notice = noticeMapper.selectById(dictId);
            if (notice == null) {
                return "";
            } else {
                return notice.getTitle();
            }
        }
    }

    /**
     * 根据字典名称和字典中的值获取对应的名称
     */
    @Override
    public String getDictsByName(String name, Integer val) {
        Dict temp = new Dict();
        temp.setName(name);
        Dict dict = dictMapper.selectOne(temp);
        if (dict == null) {
            return "";
        } else {
            Wrapper<Dict> wrapper = new EntityWrapper<>();
            wrapper = wrapper.eq("pid", dict.getId());
            List<Dict> dicts = dictMapper.selectList(wrapper);
            for (Dict item : dicts) {
                if (item.getNum() != null && item.getNum().equals(val)) {
                    return item.getName();
                }
            }
            return "";
        }
    }

    /**
     * 获取性别名称
     */
    @Override
    public String getSexName(Integer sex) {
        return getDictsByName("性别", sex);
    }

    /**
     * 获取用户登录状态
     */
    @Override
    public String getStatusName(Integer status) {
        return ManagerStatus.valueOf(status);
    }

    /**
     * 获取菜单状态
     */
    @Override
    public String getMenuStatusName(Integer status) {
        return MenuStatus.valueOf(status);
    }

    /**
     * 查询字典
     */
    @Override
    public List<Dict> findInDict(Long id) {
        if (ToolUtil.isEmpty(id)) {
            return null;
        } else {
            EntityWrapper<Dict> wrapper = new EntityWrapper<>();
            List<Dict> dicts = dictMapper.selectList(wrapper.eq("pid", id));
            if (dicts == null || dicts.size() == 0) {
                return null;
            } else {
                return dicts;
            }
        }
    }

    /**
     * 获取被缓存的对象(用户删除业务)
     */
    @Override
    public String getCacheObject(String para) {
        return LogObjectHolder.me().get().toString();
    }

    /**
     * 获取子部门id
     */
    @Override
    public List<Long> getSubDeptId(Long deptid) {
        Wrapper<Dept> wrapper = new EntityWrapper<>();
        wrapper = wrapper.like("pids", "%[" + deptid + "]%");
        List<Dept> depts = this.deptMapper.selectList(wrapper);

        ArrayList<Long> deptids = new ArrayList<>();

        if (depts != null || depts.size() > 0) {
            for (Dept dept : depts) {
                deptids.add(dept.getId());
            }
        }

        return deptids;
    }

    /**
     * 获取所有父部门id
     */
    @Override
    public List<Long> getParentDeptIds(Long deptid) {
        Dept dept = deptMapper.selectById(deptid);
        String pids = dept.getPids();
        String[] split = pids.split(",");
        ArrayList<Long> parentDeptIds = new ArrayList<>();
        for (String s : split) {
            parentDeptIds.add(Long.valueOf(StrKit.removeSuffix(StrKit.removePrefix(s, "["), "]")));
        }
        return parentDeptIds;
    }

    /**
     * 通过ID获取省市县区街道地区名称
     *
     * @param areaId
     * @return
     */

    public String getAreaNameById(Long areaId) {
        Area area = areaMapper.selectById(areaId);
        if (area == null) {
            return "";
        }
        return area.getName();
    }

    /**
     * 获取微信服务的配置信息
     */
    @Override
    public WXProperties getWXProperties() {
        return wxProperties;
    }

    /**
     * 获取微信服务的配置信息
     */
    @Override
    public WXpayProperties getWXpayProperties() {
        return wxpayProperties;
    }

    /**
     * URL对象
     */
    @Override
    public UrlProperties getUrlProperties() {
        return urlProperties;
    }

}
