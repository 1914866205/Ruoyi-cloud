<template>
  <div class="navbar">
    <hamburger
      id="hamburger-container"
      :is-active="sidebar.opened"
      class="hamburger-container"
      @toggleClick="toggleSideBar"
    />
    <!-- 此处根据 sidebar.opened的值进行动态变换
    这个 sidebar.opened 的值让我找了好久

来自下面这里
  ...mapGetters([
      'sidebar', // // 使用对象展开运算符将 getter 混入 computed 对象中
      'avatar',
      'device'
    ])
那么这个值从哪里来的呢

import { mapGetters } from 'vuex'

来自这里的引入，来 我们看看这里面


const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  introduction: state => state.user.introduction,
  roles: state => state.user.roles,
  permissions: state => state.user.permissions,
  permission_routes: state => state.permission.routes,
  topbarRouters:state => state.permission.topbarRouters,
  defaultRoutes:state => state.permission.defaultRoutes,
  sidebarRouters:state => state.permission.sidebarRouters,
}
export default getters

好家伙 ，一大堆，这里第一个就是我们要的值
 sidebar: state => state.app.sidebar,
 不就是 this.$store.state.app.sidebar

    ...mapGetters([
      // 'sidebar',  // 使用对象展开运算符将 getter 混入 computed 对象中
      'avatar',
      'device'
    ]),
    sidebar:function(){
      return this.$store.state.app.sidebar
    }
    完美替换


@toggleClick="toggleSideBar"   https://www.runoob.com/jquery/eff-toggle.html
    隐藏和显示之间的切换
     -->
    <breadcrumb
      id="breadcrumb-container"
      class="breadcrumb-container"
      v-if="!topNav"
    />
    <!-- 当没有顶部导航栏时显示顶部标签路径 -->
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav" />

    <div class="right-menu">
      <template v-if="device !== 'mobile'">
        <!-- 如果不是手机设备   -->
        <!-- 搜索组件 -->
        <search id="header-search" class="right-menu-item" />
        <el-tooltip content="源码地址" effect="dark" placement="bottom">
          <ruo-yi-git id="ruoyi-git" class="right-menu-item hover-effect" />
        </el-tooltip>
        <!--
        effect	默认提供的主题	String	dark/light	dark
        content	显示的内容，也可以通过 slot#content 传入 DOM	String

        placement	Tooltip 的出现位置
        top/top-start/top-end/bottom/bottom-start/bottom-end/left/left-start/left-end/right/right-start/right-end
        默认值 	bottom
         -->
        <el-tooltip content="文档地址" effect="dark" placement="bottom">
          <ruo-yi-doc id="ruoyi-doc" class="right-menu-item hover-effect" />
        </el-tooltip>

<!-- 这是一个全屏组件 -->
        <screenfull id="screenfull" class="right-menu-item hover-effect" />
<!-- 调节布局大小 -->
        <el-tooltip content="布局大小" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>
      </template>

      <!-- 下拉菜单  https://element.eleme.cn/#/zh-CN/component/dropdown#bu-tong-chi-cun -->
      <el-dropdown
        class="avatar-container right-menu-item hover-effect"
        trigger="click"
      >
      <!--trigger="click" 激活方式  点击会显示下拉菜单，默认是悬浮激活 hover  -->
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar" />
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from "vuex";
import Breadcrumb from "@/components/Breadcrumb";
import TopNav from "@/components/TopNav";
import Hamburger from "@/components/Hamburger";
import Screenfull from "@/components/Screenfull";
import SizeSelect from "@/components/SizeSelect";
import Search from "@/components/HeaderSearch";
import RuoYiGit from "@/components/RuoYi/Git";
import RuoYiDoc from "@/components/RuoYi/Doc";

export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc
  },
  computed: {
    ...mapGetters([
      // 'sidebar',  // 使用对象展开运算符将 getter 混入 computed 对象中
      "avatar", //用户头像
      "device" //装置
    ]),
    sidebar: function() {
      return this.$store.state.app.sidebar;
    },
    setting: {
      get() {
        return this.$store.state.settings.showSettings;
      },
      set(val) {
        this.$store.dispatch("settings/changeSetting", {
          key: "showSettings",
          value: val
        });
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav;
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch("app/toggleSideBar");
      /**
       * this.$store.dispatch() 与 this.$store.commit()方法的区别总的来说他们只是存取方式的不同,两个方法都是传值给vuex的mutation改变state
this.$store.dispatch() ：含有异步操作，例如向后台提交数据，写法：this.$store.dispatch(‘action方法名’,值)
this.$store.commit()：同步操作，，写法：this.$store.commit(‘mutations方法名’,值)
commit: 同步操作
存储 this.$store.commit('changeValue',name)
取值 this.$store.state.changeValue
dispatch: 异步操作
存储 this.$store.dispatch('getlists',name)
取值 this.$store.getters.getlists
*/
    },
    async logout() {
      this.$confirm("确定注销并退出系统吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$store.dispatch("LogOut").then(() => {
            location.href = "/index";
          });
        })
        .catch(() => {});
    }
  }
};
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background 0.3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, 0.025);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background 0.3s;

        &:hover {
          background: rgba(0, 0, 0, 0.025);
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
