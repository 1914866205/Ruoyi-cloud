<template>
  <div :class="{ show: show }" class="header-search">
    <!-- :class="{'show':show}"  绑定动态样式 -->
    <svg-icon
      class-name="search-icon"
      icon-class="search"
      @click.stop="click"
    />
    <!-- 事件冒泡 ：当一个元素接收到事件的时候 会把他接收到的事件传给自己的父级，一直到window 。
    （注意这里传递的仅仅是事件 并不传递所绑定的事件函数。所以如果父级没有绑定事件函数，就算传递了事件 也不会有什么表现 但事件确实传递了。）
    @click.stop: 阻止事件冒泡；
     -->
    <el-select
      ref="headerSearchSelect"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="Search"
      class="header-search-select"
      @change="change"
    >
      <!--
    为了启用远程搜索，需要将filterable和remote设置为true，同时传入一个remote-method。
    remote-method为一个Function，它会在输入值发生变化时调用，参数为当前输入值。
    需要注意的是，如果el-option是通过v-for指令渲染出来的，此时需要为el-option添加key属性，且其值需具有唯一性，比如此例中的item.value。
    ref="headerSearchSelect"  参考 https://www.jianshu.com/p/623c8b009a85
    方便下面调用取值
    v-model="search"  双向绑定一个search值
    :remote-method="querySearch"   调用querySearch方法
    filterable	是否可搜索	boolean
    default-first-option	在输入框按下回车，选择第一个匹配项。需配合 filterable 或 remote 使用
    remote	是否为远程搜索
    placeholder="Search"  没有输入时的默认值
    @change当输入框失焦的时候触发而且在elementUI中使用change时是这样的@visible-change
    @input是输入框发生变化时触发，也就是说输入框一动就出触发了
     -->
      <el-option
        v-for="option in options"
        :key="option.item.path"
        :value="option.item"
        :label="option.item.title.join(' > ')"
      />
      <!--
        选择框
        v-for="option in options" :key="option.item.path" :value="option.item"
        遍历可选项， key为它们的路径
        :label 分组的组名之间添加 >
       -->
    </el-select>
  </div>
</template>

<script>
// fuse is a lightweight fuzzy-search module
// make search results more in line with expectations
import Fuse from "fuse.js/dist/fuse.min.js"; //https://fusejs.io/api/methods.html  fuse.js模糊搜索
import path from "path";

export default {
  name: "HeaderSearch",
  data() {
    return {
      search: "", //搜索框的内容
      options: [], //可选项
      searchPool: [], // 可被搜索的数据池
      show: false, //搜索输入框的开关
      fuse: undefined // fuse对象，用于查找
    };
  },
  computed: {
    routes() {
      return this.$store.getters.permission_routes; //返回当前权限路径
    }
  },
  watch: {
    //监听
    routes() {
      this.searchPool = this.generateRoutes(this.routes); //用于搜索框的搜索
    },
    searchPool(list) {
      this.initFuse(list); // 初始化fuse数据池
    },
    show(value) {
      if (value) {
        // 动态监听事件 该对象添加this.close事件
        document.body.addEventListener("click", this.close);
      } else {
        // 动态监听事件 该对象删除this.close事件
        document.body.removeEventListener("click", this.close);
      }
    }
  },
  mounted() {
    this.searchPool = this.generateRoutes(this.routes); //更新fuse数据池
  },
  methods: {
    click() {
      this.show = !this.show; //搜索框的开和关
      if (this.show) {
        // 如果是打开的，则调用 this.$refs.headerSearchSelect.focus()
        this.$refs.headerSearchSelect && this.$refs.headerSearchSelect.focus();
      }
    },
    close() {
      // 当前框失去焦点时 触发
      this.$refs.headerSearchSelect && this.$refs.headerSearchSelect.blur();
      this.options = []; //可选项置空
      this.show = false; //展示为不可显示
    },
    change(val) {
      const path = val.path;
      if (this.ishttp(val.path)) {
        // http(s):// 路径新窗口打开
        const pindex = path.indexOf("http"); //如果是http地址
        window.open(path.substr(pindex, path.length), "_blank"); //在新窗口中打开
      } else {
        this.$router.push(val.path); //否则路由跳转到那个页面
      }
      this.search = ""; //搜索框置空
      this.options = []; //选择框置空
      this.$nextTick(() => {
        this.show = false; //搜索框关闭
      });
    },
    initFuse(list) {
      //初始化fuse，把可能被搜索的数据放入新的fuse
      this.fuse = new Fuse(list, {
        shouldSort: true, //按分数对结果列表进行排序
        threshold: 0.4, //匹配算法在什么时候放弃。阈值0.0需要完美匹配（字母和位置），阈值1.0可以匹配任何内容。
        location: 0, //大致确定文本中预期要找到的模式的位置
        distance: 100, //默认： 100
        //确定匹配必须与模糊位置（由 指定location）的接近程度。distance与模糊位置相距字符的精确字母匹配将计分为完全不匹配。A distanceof0要求匹配在location指定的精确位置。
        //距离 of1000需要完美匹配才能在使用of找到的800字符内。locationthreshold0.8
        maxPatternLength: 32, //最大匹配长度
        minMatchCharLength: 1, //仅返回长度超过此值的匹配项。
        keys: [
          //将被搜索的键列表。这支持嵌套路径、加权搜索、在字符串和对象数组中搜索
          {
            name: "title",
            weight: 0.7
          },
          {
            name: "path",
            weight: 0.3
          }
        ]
      });
    },
    // Filter out the routes that can be displayed in the sidebar
    // And generate the internationalized title
    /**
     * //筛选出可以在侧边栏中显示的路线
     * //并生成国际化的标题
     * */
    generateRoutes(routes, basePath = "/", prefixTitle = []) {
      let res = [];

      for (const router of routes) {
        // skip hidden router  跳过隐藏路由
        if (router.hidden) {
          continue;
        }

        const data = {
          path: !this.ishttp(router.path)
            ? path.resolve(basePath, router.path)
            : router.path,
          title: [...prefixTitle] // title 是 复制 prefixTitle 数组
          //如果是http或者https开头//给定的路径的序列是从右往左被处理的，后面每个 path 被依次解析，直到构造完成一个绝对路径。
          //  例如，给定的路径片段的序列为：/foo、/bar、baz，则调用 path.resolve('/foo', '/bar', 'baz') 会返回 /bar/baz。
        };
        // console.log("router.path:"+router.path)
        // console.log("basePath.path:"+basePath.path)
        // console.log("path.resolve(basePath, router.path):"+path.resolve(basePath, router.path))

        //router.path:/system
        //path.resolve(basePath, router.path):/system
        //basePath.path:undefined

        //router.path:http://localhost:8080/swagger-ui/index.html
        //path.resolve(basePath, router.path):/tool/gen
        //basePath.path:undefined
        // console.log("*******");
        // console.log("data.path:" + data.path);
        // console.log("data.title:" + data.title);
        // console.log("router.meta:" + router.meta);
        // console.log(" router.meta.title:" + router.meta.title);
        // console.log(
        //   "router.meta && router.meta.title:" + router.meta && router.meta.title
        // );
        //               for (const key in router.meta) {
        //           if (Object.hasOwnProperty.call(router.meta, key)) {
        //           const element = router.meta[key];
        //           console.log(key+"------"+element);
        //           /*
        // *******
        // index.vue?6ced:183 title------定时任务
        // index.vue?6ced:183 icon------job
        // index.vue?6ced:183 noCache------false
        // index.vue?6ced:183 link------null
        // index.vue?6ced:206 *******
        // index.vue?6ced:172 *******
        // index.vue?6ced:183 title------Sentinel控制台
        // index.vue?6ced:183 icon------sentinel
        // index.vue?6ced:183 noCache------false
        // index.vue?6ced:183 link------http://localhost:8718
        // *******
        // index.vue?6ced:183 Admin控制台
        // index.vue?6ced:183 server
        // index.vue?6ced:183 false
        // index.vue?6ced:183 http://localhost:9100/login
        // index.vue?6ced:186 *******
        //           */
        //         }
        //       }
        //         console.log("*******");
        /**
 * *******
 *
 *
 *
index.vue?6ced:173 data.path:/system
index.vue?6ced:174 data.title:
index.vue?6ced:175 router.meta:[object Object]
index.vue?6ced:180 *******
index.vue?6ced:172 *******
index.vue?6ced:173 data.path:/system/user
index.vue?6ced:174 data.title:系统管理
index.vue?6ced:175 router.meta:[object Object]
index.vue?6ced:180 *******
 * */
        if (router.meta && router.meta.title) {
          //router.meta.title
          data.title = [...data.title, router.meta.title];
          // console.log("data.title:"+data.title)
          /*
          *******
index.vue?6ced:223 data.title:首页
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,用户管理
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,角色管理
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,菜单管理
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,部门管理
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,岗位管理
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,字典管理
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,参数设置
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,通知公告
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,操作日志
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统管理,登录日志
index.vue?6ced:223 data.title:系统监控
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统监控,在线用户
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统监控,定时任务
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统监控,Sentinel控制台
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统监控,Nacos控制台
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统监控,Admin控制台
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统工具
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统工具,表单构建
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统工具,代码生成
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:系统工具,系统接口
index.vue?6ced:172 *******
index.vue?6ced:223 data.title:若依官网
。。。。
          */
          if (router.redirect !== "noRedirect") {
            // 如果  ！ 不需要重定向  即重定向
            // only push the routes with title
            // special case: need to exclude parent router without redirect
            res.push(data);
          }
        }
        // console.log("++++++++++++")
        // console.log("res:"+res)
        // for (const key in res) {
        //   if (Object.hasOwnProperty.call(res, key)) {
        //     const element = res[key];
        //   }
        // }
        //     console.log("++++++++++++")
        // recursive child routes
        if (router.children) {
          //如果有子路由
          //获取子路由
          const tempRoutes = this.generateRoutes(
            router.children,
            data.path,
            data.title
          );
          if (tempRoutes.length >= 1) {
            res = [...res, ...tempRoutes];
            //  ... 是复制数组
            // 即可以抽象为 res+=tempRoutes
          }
        }
      }
      // console.log(res)
      // console.log("++++++++++++");
      // for (let i = 0; i < res.length; i++) {
      //   if (res[i].title.length == 1) {
      //     console.log(res[i].title[0]+"----"+res[i].path);
      //   } else {
      //     console.log(res[i].title[0] + "----" + res[i].title[1]+"----"+res[i].path);
      //   }
      // }
      // console.log("++++++++++++");
      /*
++++++++++++
index.vue?6ced:308 首页----/index
index.vue?6ced:310 系统管理----用户管理----/system/user
index.vue?6ced:310 系统管理----角色管理----/system/role
index.vue?6ced:310 系统管理----菜单管理----/system/menu
index.vue?6ced:310 系统管理----部门管理----/system/dept
index.vue?6ced:310 系统管理----岗位管理----/system/post
index.vue?6ced:310 系统管理----字典管理----/system/dict
index.vue?6ced:310 系统管理----参数设置----/system/config
index.vue?6ced:310 系统管理----通知公告----/system/notice
index.vue?6ced:310 系统管理----操作日志----/system/log/operlog
index.vue?6ced:310 系统管理----登录日志----/system/log/logininfor
index.vue?6ced:310 系统监控----在线用户----/monitor/online
index.vue?6ced:310 系统监控----定时任务----/monitor/job
index.vue?6ced:310 系统监控----Sentinel控制台----http://localhost:8718
index.vue?6ced:310 系统监控----Nacos控制台----http://localhost:8848/nacos
index.vue?6ced:310 系统监控----Admin控制台----http://localhost:9100/login
index.vue?6ced:310 系统工具----表单构建----/tool/build
index.vue?6ced:310 系统工具----代码生成----/tool/gen
index.vue?6ced:310 系统工具----系统接口----http://localhost:8080/swagger-ui/index.html
index.vue?6ced:308 若依官网----http://ruoyi.vip
index.vue?6ced:313 ++++++++++++
*/
      return res;
    },
    querySearch(query) {
      if (query !== "") {
        this.options = this.fuse.search(query);
      } else {
        this.options = [];
      }
    },
    ishttp(url) {
      //判断是不是http:// 或者 https://
      return url.indexOf("http://") !== -1 || url.indexOf("https://") !== -1;
    }
  }
};
</script>

<style lang="scss" scoped>
// 为了样式模块化，给其加上scoped属性
/**
给HTML的DOM节点加一个不重复data属性(形如：data-v-2311c06a)来表示他的唯一性
在每句css选择器的末尾（编译后的生成的css语句）加一个当前组件的data属性选择器（如[data-v-2311c06a]）来私有化样式
如果组件内部包含有其他组件，只会给其他组件的最外层标签加上当前组件的data属性
*/
.header-search {
  font-size: 0 !important;

  .search-icon {
    cursor: pointer;
    font-size: 18px;
    vertical-align: middle;
  }

  .header-search-select {
    font-size: 18px;
    transition: width 0.2s;
    width: 0;
    overflow: hidden;
    background: transparent;
    border-radius: 0;
    display: inline-block;
    vertical-align: middle;
    // p::after	在每个 <p> 的内容之后插入内容。
    ::v-deep .el-input__inner {
      border-radius: 0;
      border: 0;
      padding-left: 0;
      padding-right: 0;
      box-shadow: none !important;
      border-bottom: 1px solid #d9d9d9;
      vertical-align: middle;
    }
  }
  // 父选择器的标识符 & 在嵌套 CSS 规则时，有时也需要直接使用嵌套外层的父选择器，
  // 例如，当给某个元素设定 hover 样式时，或者当 body 元素有某个 classname 时，可以用 & 代表嵌套规则外层的父选择器。
  &.show {
    //父级class样式是 show 的组件
    .header-search-select {
      width: 210px; //搜索框的宽度
      margin-left: 10px; //搜索框和搜索图标之间的距离
    }
  }
}
</style>
