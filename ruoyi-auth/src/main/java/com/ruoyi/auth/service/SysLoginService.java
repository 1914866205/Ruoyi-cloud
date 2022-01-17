package com.ruoyi.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.enums.UserStatus;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.RemoteLogService;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.domain.SysLogininfor;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
public class SysLoginService {
    @Autowired
    private RemoteLogService remoteLogService;

    @Autowired
    private RemoteUserService remoteUserService;

    /**
     * 登录
     */
    public LoginUser login(String username, String password) {
        // 用户名或密码为空 错误
        //有一个为空就报错
        if (StringUtils.isAnyBlank(username, password)) {
            //记录日志信息
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户/密码必须填写");
            throw new ServiceException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        // 密码长度不对
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码不在指定范围");
            throw new ServiceException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        // 用户名长度不对
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户名不在指定范围");
            throw new ServiceException("用户名不在指定范围");
        }
        // 查询用户信息
        //远程方法调用
        R<LoginUser> userResult = remoteUserService.getUserInfo(username, SecurityConstants.INNER);
        /**
         * userResult = {R@12724}
         *  code = 200
         *  msg = null
         *  data = {LoginUser@12725}
         *   token = null
         *   userid = null
         *   username = null
         *   loginTime = null
         *   expireTime = null
         *   ipaddr = null
         *   permissions = {HashSet@12726}  size = 1
         *   roles = {HashSet@12727}  size = 1
         *   sysUser = {SysUser@12728} "com.ruoyi.system.api.domain.SysUser@73cefbeb[\r\n  userId=1\r\n  deptId=103\r\n  userName=admin\r\n  nickName=若依\r\n  email=ry@163.com\r\n  phonenumber=15888888888\r\n  sex=1\r\n  avatar=\r\n  password=$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2\r\n  status=0\r\n  delFlag=0\r\n  loginIp=127.0.0.1\r\n  loginDate=Wed Jan 12 19:59:38 CST 2022\r\n  createBy=admin\r\n  createTime=Thu Jan 13 03:59:38 CST 2022\r\n  updateBy=<null>\r\n  updateTime=<null>\r\n  remark=管理员\r\n  dept=com.ruoyi.system.api.domain.SysDept@6ae2bb09[\r\n  deptId=103\r\n  parentId=101\r\n  ancestors=<null>\r\n  deptName=研发部门\r\n  orderNum=1\r\n  leader=若依\r\n  phone=<null>\r\n  email=<null>\r\n  status=0\r\n  delFlag=<null>\r\n  createBy=<null>\r\n  createTime=<null>\r\n  updateBy=<null>\r\n  updateTime=<null>\r\n]\r\n]"
         *    userId = {Long@12743} 1
         *    deptId = {Long@12744} 103
         *    userName = "admin"
         *    nickName = "若依"
         *    email = "ry@163.com"
         *    phonenumber = "15888888888"
         *    sex = "1"
         *    avatar = ""
         *    password = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2"
         *    status = "0"
         *    delFlag = "0"
         *    loginIp = "127.0.0.1"
         *    loginDate = {Date@12755} "Wed Jan 12 19:59:38 CST 2022"
         *    dept = {SysDept@12756} "com.ruoyi.system.api.domain.SysDept@6ae2bb09[\r\n  deptId=103\r\n  parentId=101\r\n  ancestors=<null>\r\n  deptName=研发部门\r\n  orderNum=1\r\n  leader=若依\r\n  phone=<null>\r\n  email=<null>\r\n  status=0\r\n  delFlag=<null>\r\n  createBy=<null>\r\n  createTime=<null>\r\n  updateBy=<null>\r\n  updateTime=<null>\r\n]"
         *    roles = {ArrayList@12757}  size = 1
         *    roleIds = null
         *    postIds = null
         *    roleId = null
         *    searchValue = null
         *    createBy = "admin"
         *    createTime = {Date@12759} "Thu Jan 13 03:59:38 CST 2022"
         *    updateBy = null
         *    updateTime = null
         *    remark = "管理员"
         *    params = {LinkedHashMap@12761}  size = 0
         */

        // 500==userResult.getCode()
        if (R.FAIL == userResult.getCode()) {
            throw new ServiceException(userResult.getMsg());
            //服务调用失败
        }
        //如果没找到用户
        if (StringUtils.isNull(userResult) || StringUtils.isNull(userResult.getData())) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "登录用户不存在");
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        //如果找到用户，获取用户信息
        LoginUser userInfo = userResult.getData();
        //获取用户对象
        SysUser user = userResult.getData().getSysUser();
        //用户删除标志     UserStatus.DELETED   2    user.getDelFlag()  0
        //  OK("0", "正常"), DISABLE("1", "停用"), DELETED("2", "删除");
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "对不起，您的账号已被删除");
            throw new ServiceException("对不起，您的账号：" + username + " 已被删除");
        }
        //    OK("0", "正常"), DISABLE("1", "停用"), DELETED("2", "删除");
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户已停用，请联系管理员");
            throw new ServiceException("对不起，您的账号：" + username + " 已停用");
        }
        // 如果密码不相等，则密码错误
        // password是用户登录时的明文  admin123  user.getPassword()是系统根据用户名admin在数据库查到的密文
        if (!SecurityUtils.matchesPassword(password, user.getPassword())) {
            recordLogininfor(username, Constants.LOGIN_FAIL, "用户密码错误");
            throw new ServiceException("用户不存在/密码错误");
        }
        //记录用户登录成功的信息
        recordLogininfor(username, Constants.LOGIN_SUCCESS, "登录成功");
        //返回用户信息
        return userInfo;
    }

    public void logout(String loginName) {
        recordLogininfor(loginName, Constants.LOGOUT, "退出成功");
    }

    /**
     * 注册
     */
    public void register(String username, String password) {
        // 用户名或密码为空 错误
        if (StringUtils.isAnyBlank(username, password)) {
            throw new ServiceException("用户/密码必须填写");
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new ServiceException("账户长度必须在2到20个字符之间");
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new ServiceException("密码长度必须在5到20个字符之间");
        }

        // 注册用户信息
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);
        sysUser.setNickName(username);
        sysUser.setPassword(SecurityUtils.encryptPassword(password));
        R<?> registerResult = remoteUserService.registerUserInfo(sysUser, SecurityConstants.INNER);

        if (R.FAIL == registerResult.getCode()) {
            throw new ServiceException(registerResult.getMsg());
        }
        recordLogininfor(username, Constants.REGISTER, "注册成功");
    }

    /**
     * 记录登录信息
     *
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     * @return
     */
    public void recordLogininfor(String username, String status, String message) {
        //创建一个系统访问记录实体，属性包括 实体id、用户名、访问成功状态、IP地址、访问时间、描述
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(IpUtils.getIpAddr(ServletUtils.getRequest()));
        logininfor.setMsg(message);
        // 日志状态
        //如果当前 status 和Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER其中一个相等
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            //访问成功置为0
            logininfor.setStatus("0");
        } else if (Constants.LOGIN_FAIL.equals(status)) {
            //访问失败置为1
            logininfor.setStatus("1");
        }
        //远程日志记录服务 记录 日志信息
        // SecurityConstants.INNER   权限相关通用常量  内部请求
        remoteLogService.saveLogininfor(logininfor, SecurityConstants.INNER);
    }
}