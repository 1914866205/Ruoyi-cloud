package com.ruoyi.common.security.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.LoginUser;

/**
 * token验证处理
 *
 * @author ruoyi
 */
@Component
public class TokenService {
    @Autowired
    private RedisService redisService;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private final static long expireTime = CacheConstants.EXPIRATION;

    private final static String ACCESS_TOKEN = CacheConstants.LOGIN_TOKEN_KEY;

    private final static Long MILLIS_MINUTE_TEN = CacheConstants.REFRESH_TIME * MILLIS_MINUTE;

    /**
     * 创建令牌
     */
    public Map<String, Object> createToken(LoginUser loginUser) {
        /**
         * LoginUser属性
         *      * 用户唯一标识 token
         *      * 用户名id  userId
         *      * 用户名
         *      * 登录时间
         *      * 过期时间
         *      * 登录IP地址  ipaddr
         *      * 权限列表
         *      * 角色列表
         *      * 用户信息
         */

        //生成全局唯一的UUID，UUID即为token
        String token = IdUtils.fastUUID();
        //获取用户id
        Long userId = loginUser.getSysUser().getUserId();
        //获取用户名
        String userName = loginUser.getSysUser().getUserName();
        // 添加token，userId,userName,IP地址
        loginUser.setToken(token);
        loginUser.setUserid(userId);
        loginUser.setUsername(userName);
        loginUser.setIpaddr(IpUtils.getIpAddr(ServletUtils.getRequest()));
        //刷新token
        refreshToken(loginUser);

        // Jwt存储信息
        /**
         * Json web token (JWT), 是为了在网络应用环境间传递声明而执行的一种基于JSON的开放标准（(RFC 7519).
         * 该token被设计为紧凑且安全的，特别适用于分布式站点的单点登录（SSO）场景。
         * JWT的声明一般被用来在身份提供者和服务提供者间传递被认证的用户身份信息，
         * 以便于从资源服务器获取资源，也可以增加一些额外的其它业务逻辑所必须的声明信息，
         * 该token也可直接被用于认证，也可被加密。
         */
        Map<String, Object> claimsMap = new HashMap<String, Object>();
        // user_key:token
        claimsMap.put(SecurityConstants.USER_KEY, token);
        // user_id :userId
        claimsMap.put(SecurityConstants.DETAILS_USER_ID, userId);
        // username:userName
        claimsMap.put(SecurityConstants.DETAILS_USERNAME, userName);
        // 接口返回信息
        Map<String, Object> rspMap = new HashMap<String, Object>();
        // 根据uuid,userId,userName创建access_token
        rspMap.put("access_token", JwtUtils.createToken(claimsMap));
        // 过期时间 720 min
        rspMap.put("expires_in", expireTime);
        return rspMap;
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser() {
        return getLoginUser(ServletUtils.getRequest());
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = SecurityUtils.getToken(request);
        return getLoginUser(token);
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(String token) {
        LoginUser user = null;
        try {
            if (StringUtils.isNotEmpty(token)) {
                //根据token解析出用户信息userkey
                String userkey = JwtUtils.getUserKey(token);
                //根据userkey获取token的key，并从redis中获取缓存用户信息
                user = redisService.getCacheObject(getTokenKey(userkey));
                return user;
            }
        } catch (Exception e) {
        }
        return user;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户缓存信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userkey = JwtUtils.getUserKey(token);
            redisService.deleteObject(getTokenKey(userkey));
        }
    }

    /**
     * 验证令牌有效期，相差不足120分钟，自动刷新缓存
     *
     * @param loginUser
     */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        // 谁知当前时间为登录时间
        loginUser.setLoginTime(System.currentTimeMillis());
        //设置过期时间为当前时间+规定的过期时间
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        // userKey=login_tokens:uuid
        /**
         *
         * userKey    login_tokens:ffbf3246-f782-46e8-838b-a1a6eca254f9
         *
         * loginUser:
         * token = "ffbf3246-f782-46e8-838b-a1a6eca254f9"
         * userid = {Long@15180} 1
         * username = "admin"
         * loginTime = {Long@15182} 1642404937201
         * expireTime = {Long@15183} 1642448137201
         * ipaddr = "127.0.0.1"
         * permissions = {HashSet@15185}  size = 1
         * roles = {HashSet@15186}  size = 1
         * sysUser = {SysUser@11475} "com.ruoyi.system.api.domain.SysUser@23bf00d8[\r\n  userId=1\r\n  deptId=103\r\n  userName=admin\r\n  nickName=若依\r\n  email=ry@163.com\r\n  phonenumber=15888888888\r\n  sex=1\r\n  avatar=\r\n  password=$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2\r\n  status=0\r\n  delFlag=0\r\n  loginIp=127.0.0.1\r\n  loginDate=Wed Jan 12 19:59:38 CST 2022\r\n  createBy=admin\r\n  createTime=Thu Jan 13 03:59:38 CST 2022\r\n  updateBy=<null>\r\n  updateTime=<null>\r\n  remark=管理员\r\n  dept=com.ruoyi.system.api.domain.SysDept@2a0ca240[\r\n  deptId=103\r\n  parentId=101\r\n  ancestors=<null>\r\n  deptName=研发部门\r\n  orderNum=1\r\n  leader=若依\r\n  phone=<null>\r\n  email=<null>\r\n  status=0\r\n  delFlag=<null>\r\n  createBy=<null>\r\n  createTime=<null>\r\n  updateBy=<null>\r\n  updateTime=<null>\r\n]\r\n]"
         *
         * expireTime 720 过期时间
         *
         * TimeUnit.MINUTES  过期时间单位
         */
        redisService.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    private String getTokenKey(String token) {
        return ACCESS_TOKEN + token;
    }
}