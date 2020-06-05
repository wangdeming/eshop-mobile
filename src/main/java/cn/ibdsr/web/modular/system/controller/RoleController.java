//package cn.ibdsr.web.modular.system.controller;
//
//import cn.ibdsr.core.base.controller.BaseController;
//import cn.ibdsr.core.base.tips.Tip;
//import cn.ibdsr.core.cache.CacheKit;
//import cn.ibdsr.core.log.LogObjectHolder;
//import cn.ibdsr.core.node.ZTreeNode;
//import cn.ibdsr.core.util.ToolUtil;
//import cn.ibdsr.web.common.annotion.BussinessLog;
//import cn.ibdsr.web.common.annotion.Permission;
//import cn.ibdsr.web.common.constant.BussinessLogType;
//import cn.ibdsr.web.common.constant.Const;
//import cn.ibdsr.web.common.constant.cache.Cache;
//import cn.ibdsr.web.common.constant.dictmap.systemdict.SystemDict;
//import cn.ibdsr.web.common.constant.factory.ConstantFactory;
//import cn.ibdsr.web.common.exception.BizExceptionEnum;
//import cn.ibdsr.web.common.exception.BussinessException;
//import cn.ibdsr.web.common.persistence.dao.RoleMapper;
//import cn.ibdsr.web.common.persistence.dao.UserMapper;
//import cn.ibdsr.web.common.persistence.model.Role;
//import cn.ibdsr.web.modular.system.dao.RoleDao;
//import cn.ibdsr.web.modular.system.service.IRoleService;
//import cn.ibdsr.web.modular.system.warpper.RoleWarpper;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.annotation.Resource;
//import javax.validation.Valid;
//import java.util.List;
//import java.util.Map;
//
///**
// * 角色控制器
// *
// * @author fengshuonan
// * @Date 2017年2月12日21:59:14
// */
//@Controller
//@RequestMapping("/role")
//public class RoleController extends BaseController {
//
//    private static String PREFIX = "/system/role";
//
//    @Resource
//    UserMapper userMapper;
//
//    @Resource
//    RoleMapper roleMapper;
//
//    @Resource
//    RoleDao roleDao;
//
//    @Resource
//    IRoleService roleService;
//
//    /**
//     * 跳转到角色列表页面
//     */
//    @RequestMapping("")
//    public String index() {
//        return PREFIX + "/role.html";
//    }
//
//    /**
//     * 跳转到添加角色
//     */
//    @RequestMapping(value = "/role_add")
//    public String roleAdd() {
//        return PREFIX + "/role_add.html";
//    }
//
//    /**
//     * 跳转到修改角色
//     */
//    @Permission
//    @RequestMapping(value = "/role_edit/{roleId}")
//    public String roleEdit(@PathVariable Long roleId, Model model) {
//        if (ToolUtil.isEmpty(roleId)) {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
//        Role role = this.roleMapper.selectById(roleId);
//        model.addAttribute(role);
//        model.addAttribute("pName", ConstantFactory.me().getSingleRoleName(role.getPid()));
//        model.addAttribute("deptName", ConstantFactory.me().getDeptName(role.getDeptid()));
//        LogObjectHolder.me().set(role);
//        return PREFIX + "/role_edit.html";
//    }
//
//    /**
//     * 跳转到角色分配
//     */
//    @Permission
//    @RequestMapping(value = "/role_assign/{roleId}")
//    public String roleAssign(@PathVariable("roleId") Long roleId, Model model) {
//        if (ToolUtil.isEmpty(roleId)) {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
//        model.addAttribute("roleId", roleId);
//        model.addAttribute("roleName", ConstantFactory.me().getSingleRoleName(roleId));
//        return PREFIX + "/role_assign.html";
//    }
//
//    /**
//     * 获取角色列表
//     */
//    @Permission
//    @RequestMapping(value = "/list")
//    @ResponseBody
//    public Object list(@RequestParam(required = false) String roleName) {
//        List<Map<String, Object>> roles = this.roleDao.selectRoles(super.getPara("roleName"));
//        return super.warpObject(new RoleWarpper(roles));
//    }
//
//    /**
//     * 角色新增
//     */
//    @RequestMapping(value = "/add")
//    @BussinessLog(name = "添加角色", key = "name", dict = SystemDict.RoleDict)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public Tip add(@Valid Role role, BindingResult result) {
//        if (result.hasErrors()) {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
//        role.setId(null);
//        this.roleMapper.insert(role);
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 角色修改
//     */
//    @RequestMapping(value = "/edit")
//    @BussinessLog(name = "修改角色", key = "name",logType = BussinessLogType.DETAILEDLOG,dict = SystemDict.RoleDict)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public Tip edit(@Valid Role role, BindingResult result) {
//        if (result.hasErrors()) {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
//        this.roleMapper.updateById(role);
//
//        //删除缓存
//        CacheKit.removeAll(Cache.CONSTANT);
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 删除角色
//     */
//    @RequestMapping(value = "/remove")
//    @BussinessLog(name = "删除角色", key = "roleId", dict = SystemDict.DeleteDict)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public Tip remove(@RequestParam Long roleId) {
//        if (ToolUtil.isEmpty(roleId)) {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
//
//        //不能删除超级管理员角色
//        if(roleId.equals(Const.ADMIN_ROLE_ID)){
//            throw new BussinessException(BizExceptionEnum.CANT_DELETE_ADMIN);
//        }
//
//        //缓存被删除的角色名称
//        LogObjectHolder.me().set(ConstantFactory.me().getSingleRoleName(roleId));
//
//        this.roleService.delRoleById(roleId);
//
//        //删除缓存
//        CacheKit.removeAll(Cache.CONSTANT);
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 查看角色
//     */
//    @RequestMapping(value = "/view/{roleId}")
//    @ResponseBody
//    public Tip view(@PathVariable Long roleId) {
//        if (ToolUtil.isEmpty(roleId)) {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
//        this.roleMapper.selectById(roleId);
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 配置权限
//     */
//    @RequestMapping("/setAuthority")
//    @BussinessLog(name = "配置权限", key = "roleId,ids", dict = SystemDict.RoleDict)
//    @Permission(Const.ADMIN_NAME)
//    @ResponseBody
//    public Tip setAuthority(@RequestParam("roleId") Long roleId, @RequestParam("ids") String ids) {
//        if (ToolUtil.isOneEmpty(roleId)) {
//            throw new BussinessException(BizExceptionEnum.REQUEST_NULL);
//        }
//        this.roleService.setAuthority(roleId, ids);
//        return SUCCESS_TIP;
//    }
//
//    /**
//     * 获取角色列表
//     */
//    @RequestMapping(value = "/roleTreeList")
//    @ResponseBody
//    public List<ZTreeNode> roleTreeList() {
//        List<ZTreeNode> roleTreeList = this.roleDao.roleTreeList();
//        roleTreeList.add(ZTreeNode.createParent());
//        return roleTreeList;
//    }
//}
