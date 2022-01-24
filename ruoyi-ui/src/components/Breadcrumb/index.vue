<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <!--
            transition-group用于列表的过渡效果。
            默认为span, 可以使用tag 转换为其他元素。
            子元素通常使用v-for进行循环。
            子元素必须要有唯一的key属性，且key不能为索引值。
            css过渡是应用在子元素中，而不是这个容器本身。
            参考  https://blog.csdn.net/u012574931/article/details/103563183
       -->
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index == levelList.length - 1"
          class="no-redirect"
          >
          <!-- {{ index }},{{ levelList.length - 1 }},{{ item.meta.title }} -->
          {{ item.meta.title }}
          </span
        >
        <!-- 首页    /     1,2,系统管理    /    2,2,岗位管理
          levelList 长度为3
          对象1  首页
          对象2 系统管理
          对象3 岗位管理
          而 index 是从 0 开始计算的
          0 为首页
          1 为系统管理
          2 为岗位管理
          所以当 是二级菜单时，也不可选中
           -->
        <!-- no-redirect 效果是  首页/系统管理/用户管理  只有首页可以是超链接，其余两个禁止点击
        所有的一级菜单，比如 系统管理，是符合 item.redirect === 'noRedirect'
        所有的二级菜单，比如 用户管理，是符合 index == levelList.length - 1
        -->
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
        <!-- 所以这句话专门是给 首页  两个字用的，就它可以点击 -->
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
export default {
  data() {
    return {
      levelList: null //存储的是目录层级
    };
  },
  watch: {
    $route(route) {
      // if you go to the redirect page, do not update the breadcrumbs
      if (route.path.startsWith("/redirect/")) { //如何去的是重定向页面，不改变breadcrumbs。比如去的是Nacos控制台，若依官网等。
        return;
      }
      this.getBreadcrumb();
    }
  },
  created() {
    this.getBreadcrumb();
  },
  methods: {
    getBreadcrumb() {
      // only show routes with meta.title
      let matched = this.$route.matched.filter(
        item => item.meta && item.meta.title
      );
      // console.log("*************************");
      // console.log(
      //   "this.$route.matched.filter(item => item.meta && item.meta.title):"
      // );
      // this.$route.matched.filter(item => {
      /**
         *系统管理
          岗位管理
         **/
      // console.log(item.meta && item.meta.title);
      // console.log(item);
      // console.log(item.meta); //一级标题 系统管理
      // console.log(item.meta.title); //二级标题 用户管理/字典管理/。。。等等
      // });
      // console.log("*************************");
      // console.log("***********matched**************");
      // console.log(matched);
      const first = matched[0]; //两个路由对象

      if (!this.isDashboard(first)) {
        //如果不是首页，就添加一个首页对象在前面
        matched = [{ path: "/index", meta: { title: "首页" } }].concat(matched); // 首页  系统管理  岗位管理
        //如果是这样，首页也不可以选中
        // matched = [
        //   { path: "/index", meta: { title: "首页" }, redirect: "noRedirect" }
        //   // 0,2,首页/1,2,系统管理/2,2,岗位管理
        // ].concat(matched); // 首页  系统管理  岗位管理
      }
      // 否则的话  只是  首页
      // console.log("***********matched2**************");
      // console.log(matched);
      // this.levelList = matched.filter(item => {
      //   // console.log("item.meta && item.meta.title && item.meta.breadcrumb:");
      //   // console.log(item.meta);
      //   // console.log(item.meta.title);
      //   // console.log(item);
      //   // console.log(item.meta.breadcrumb);
      //   // 匹配，如果不是false，添加到 this.levelList
      //   item.meta && item.meta.title && item.meta.breadcrumb !== false;
      //   // 不理解   item.meta.breadcrumb是undefined呀   整个条件不就永真了吗
      // });
      this.levelList = matched; //和上面的过滤后效果一模一样
      //所以我再试试三个&&&
      // console.log("1"&&"2"&&"3"); //  打印为 3
      // console.log("1"&&"2"&&undefined); //  打印为 undefined
      // console.log("1"&&"2"&&null); //  打印为 null
      // console.log("1"&&"2"&&""); //  打印为 ""
      // 所以那个过滤效果为 item.meta.breadcrumb !== false
      // 因为item.meta.breadcrumb始终是undefined
      // console.log("undefined!==false:" + undefined !== false); //  打印为 true
      //所以？？？？过滤了啥
      // console.log("this.levelList:" + this.levelList);
      // for (const key in this.levelList) {
      //   if (Object.hasOwnProperty.call(this.levelList, key)) {
      //     const element = this.levelList[key];
      //     console.log(element);
      //   }
      // }
    },
    isDashboard(route) {
      // console.log("route:" + route && route.name); // System
      // console.log("1"&&"2"); //  打印为 2
      // console.log("1"&&undefined); //  打印为 undefined
      // console.log("1"&&null); //  打印为 null
      // console.log("1"&&""); //  打印为 ""
      const name = route && route.name;
      if (!name) {
        // 如果  ！没有名字
        return false;
      }
      return name.trim() === "Index"; //去掉两边的空格后返回是不是首页
    },
    handleLink(item) {
      //获取当前的对象
      // console.log(item)  // 首页路由对象  想想也是，只有首页支持跳转
      const { redirect, path } = item;
      // console.log(redirect)  // undefined
      // console.log(path)  // index
      if (redirect) {
        this.$router.push(redirect);
        return;
      }
      this.$router.push(path);
    }
  }
};
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
