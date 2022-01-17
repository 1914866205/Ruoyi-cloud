package com.ruoyi.system.service.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysPermissionService;
import com.ruoyi.system.service.ISysRoleService;

@Service
public class SysPermissionServiceImpl implements ISysPermissionService {
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取角色数据权限
     *
     * @param userId 用户Id
     * @return 角色权限信息
     */
    @Override
    public Set<String> getRolePermission(Long userId) {
        Set<String> roles = new HashSet<String>();
        // 管理员拥有所有权限
        if (SysUser.isAdmin(userId)) {
            /**
             * public static boolean isAdmin(Long userId) {
             *         // 如何用户编号为 1L，则他是管理员
             *         return userId != null && 1L == userId;
             *     }
             */
            roles.add("admin");
        } else {
            //如果不是1号用户 ， 查找这个人的用户权限，并添加到权限列表中
            roles.addAll(roleService.selectRolePermissionByUserId(userId));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId 用户Id
     * @return 菜单权限信息
     */
    @Override
    public Set<String> getMenuPermission(Long userId) {
        Set<String> perms = new HashSet<String>();
        // 管理员拥有所有权限
        if (SysUser.isAdmin(userId)) {
            //如果是管理员。获取所有的权限
            perms.add("*:*:*");
        } else {
            //如果不是管理员，根据用户id获取菜单数据权限
            perms.addAll(menuService.selectMenuPermsByUserId(userId));
        }
        return perms;
    }
}
