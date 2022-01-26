<template>
  <el-dropdown trigger="click" @command="handleSetSize">
    <!-- 下拉框
trigger="click"  点击事件
trigger	触发下拉的行为	string	 可选值 ：hover, click	  默认 hover
@command="handleSetSize"     command	点击菜单项触发的事件回调	dropdown-item 的指令
 -->
    <div>
      <svg-icon class-name="size-icon" icon-class="size" />
    </div>
    <!-- 下拉框元素 -->
    <el-dropdown-menu slot="dropdown">
      <!--
显然，这里也是父级  子集在el-dropdown-menu中
Dropdown Slots https://element.eleme.cn/#/zh-CN/component/drawer
传入dropdown 给el-dropdown-menu
该组件内容嵌入到el-dropdown-menu中去

对于这样的情况，<slot> 元素有一个特殊的 attribute：name。这个 attribute 可以用来定义额外的插槽：

子集对应接收

<div class="container">
  <header>
    <slot name="header"></slot>
  </header>
  <main>
    <slot></slot>
  </main>
  <footer>
    <slot name="footer"></slot>
  </footer>
</div>
一个不带 name 的 <slot> 出口会带有隐含的名字“default”。

在向具名插槽提供内容的时候，我们可以在一个 <template> 元素上使用 v-slot 指令，并以 v-slot 的参数的形式提供其名称：

父级，把需要的内容传进去

<base-layout>
  <template v-slot:header>
    <h1>Here might be a page title</h1>
  </template>

  <p>A paragraph for the main content.</p>
  <p>And another one.</p>

  <template v-slot:footer>
    <p>Here's some contact info</p>
  </template>
</base-layout>
现在 <template> 元素中的所有内容都将会被传入相应的插槽。任何没有被包裹在带有 v-slot 的 <template> 中的内容都会被视为默认插槽的内容。
       -->
      <el-dropdown-item
        v-for="item of sizeOptions"
        :key="item.value"
        :disabled="size === item.value"
        :command="item.value"
      >
        <!-- item.value 传给 handleSetSize
      @command="handleSetSize"
      :command="item.value"

      disabled 属性规定应该禁用 input 元素。
被禁用的 input 元素既不可用，也不可点击。可以设置 disabled 属性，
直到满足某些其他的条件为止（比如选择了一个复选框等等）。
然后，就需要通过 JavaScript 来删除 disabled 值，将 input 元素的值切换为可用。
就是当前的值 如果是size === item.value
则禁止点击这个选项。
      -->
        {{ item.label }}
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
export default {
  data() {
    return {
      // 尺寸元素
      sizeOptions: [
        { label: "Default", value: "default" },
        { label: "Medium", value: "medium" },
        { label: "Small", value: "small" },
        { label: "Mini", value: "mini" }
      ]
    };
  },
  computed: {
    size() {
      return this.$store.getters.size; //返回当前尺寸大小
    }
  },
  methods: {
    //参考 https://segmentfault.com/q/1010000021461812
    handleSetSize(size) {
      this.$ELEMENT.size = size; //这是 Element-UI 向 Vue 暴露的实例属性
      this.$store.dispatch("app/setSize", size); //把当前大小更新到 store
      this.refreshView(); //主要为了及时当前页面生效，做了一个 replace
      this.$message({
        message: "Switch Size Success",
        type: "success"
      });
    },
    refreshView() {
      //刷新视图
      // In order to make the cached page re-rendered
      this.$store.dispatch("tagsView/delAllCachedViews", this.$route); //删除当前路径所有缓存视图
      const { fullPath } = this.$route; //获取全路径
      // 页面刷新  https://blog.csdn.net/liubangbo/article/details/103333959
      /**
     * 在vue项目中，经常会遇到需要刷新当前页面的需求。
      因为vue-router判断如果路由没有变化，是不会刷新页面获取数据的。

      方式1：go(0)和reload()
      通过location.reload()或是this.$router.go(0)两种强制刷新方式，相当于按F5，会出现瞬间白屏，体验差，不推荐。

      方式2：定义一个空白路由页面，路由跳转到该空白页后立马跳回当前页，实现路由刷新。
      在router路由表中定义一个空白路由，

      // 强制刷新当前页所用的中间跳转页
  这种方式，基本上能够应付绝大多数情况，推荐使用。
但是，有时候，有一些极端情况下，这种刷新不起作用，而又不想用第一种那种毛子般的简单粗暴的方式的话，下面的方式可以选择使用。

方式3：provede/inject 方式
vue官方文档说了，这个依赖注入方式是给插件开发使用的，普通应用中不推荐使用。
但是，效果却很好。
原理就是通过依赖注入的方式，在顶部app通过v-if的显示隐藏来强制切换显示，以此来让vue重新渲染整个页面，app中通过provide方式定义的reload方法，在它的后代组件中，无论嵌套多深，都能够触发调用这个方法。具体说明查看官方文档。
这种方式刷新，虽然官方说不推荐，但是反正效果挺好，有些方式2解决不了的刷新问题，这个方式能解决。慎用。

本文采用的就是第三种
     */
      this.$nextTick(() => {
        //this.$nextTick()是在数据完成更新后立即获取数据
        this.$router.replace({
          //当遇到你需要刷新页面的情况，你就手动重定向页面到redirect页面，
          // 它会将页面重新redirect重定向回来，由于页面的 key 发生了变化，从而间接实现了刷新页面组件的效果。
          path: "/redirect" + fullPath
        });
      });
    }
  }
};
</script>
