<template>
  <div>
    <svg-icon
      :icon-class="isFullscreen ? 'exit-fullscreen' : 'fullscreen'"
      @click="click"
    />
    <!--
      :icon-class="isFullscreen?'exit-fullscreen':'fullscreen'" 动态样式绑定 是全屏？退出全屏：全屏
     -->
  </div>
</template>

<script>
// 全屏插件
import screenfull from "screenfull";

export default {
  name: "Screenfull",
  data() {
    return {
      isFullscreen: false
    };
  },
  // https://www.jianshu.com/p/672e967e201c
  // vue 生命周期详解
  /*
  beforeCreate( 创建前 )
  在实例初始化之后，数据观测和事件配置之前被调用，
  此时组件的选项对象还未创建，el 和 data 并未初始化，
  因此无法访问methods， data， computed等上的方法和数据。

  created ( 创建后 ）
  实例已经创建完成之后被调用，在这一步，
  实例已完成以下配置：数据观测、属性和方法的运算，watch/event事件回调，
  完成了data 数据的初始化，el没有。
   然而，挂在阶段还没有开始, $el属性目前不可见，这是一个常用的生命周期，
  因为你可以调用methods中的方法，改变data中的数据，
   并且修改可以通过vue的响应式绑定体现在页面上，
   获取computed中的计算属性等等，通常我们可以在这里对实例进行预处理，

   也有一些童鞋喜欢在这里发ajax请求，值得注意的是，

   这个周期中是没有什么方法来对实例化过程进行拦截的，
   因此假如有某些数据必须获取才允许进入页面的话，并不适合在这个方法发请求，
   建议在组件路由钩子beforeRouteEnter中完成

  beforeMount
  挂载开始之前被调用，相关的render函数首次被调用（虚拟DOM），
  实例已完成以下的配置： 编译模板，把data里面的数据和模板生成html，完成了el和data 初始化，
  注意此时还没有挂在html到页面上。

  mounted
  挂载完成，也就是模板中的HTML渲染到HTML页面中，此时一般可以做一些ajax操作

  mounted只会执行一次。


  beforeUpdate
  在数据更新之前被调用，发生在虚拟DOM重新渲染和打补丁之前，可以在该钩子中进一步地更改状态，
  不会触发附加地重渲染过程

  updated（更新后）
  在由于数据更改导致地虚拟DOM重新渲染和打补丁只会调用，调用时，
  组件DOM已经更新，所以可以执行依赖于DOM的操作，
  然后在大多是情况下，应该避免在此期间更改状态，
  因为这可能会导致更新无限循环，
  该钩子在服务器端渲染期间不被调用

  beforeDestroy（销毁前）
  在实例销毁之前调用，实例仍然完全可用，

  这一步还可以用this来获取实例，
  一般在这一步做一些重置的操作，比如清除掉组件中的定时器 和 监听的dom事件

  destroyed（销毁后）
  在实例销毁之后调用，调用后，所以的事件监听器会被移出，所有的子实例也会被销毁，
  该钩子在服务器端渲染期间不被调用

  */
  mounted() { //  挂载完成，也就是模板中的HTML渲染到HTML页面中，此时一般可以做一些ajax操作；mounted只会执行一次。
    this.init();
  },
  beforeDestroy() {//  在实例销毁之前调用，实例仍然完全可用，  这一步还可以用this来获取实例，
  // 一般在这一步做一些重置的操作，比如清除掉组件中的定时器 和 监听的dom事件
    this.destroy();
  },
  methods: {
    click() {
      // screenfull.isEnabled 判断浏览器能不能全屏
      if (!screenfull.isEnabled) {
        this.$message({ message: "你的浏览器不支持全屏", type: "warning" });
        return false;
      }
      // 开启全屏
      screenfull.toggle();
    },
    change() {
      // 全屏状态 的 参数更新，这个是控制图标显示的
      this.isFullscreen = screenfull.isFullscreen;
    },
    init() {
      if (screenfull.isEnabled) {
        // 开启全屏
        screenfull.on("change", this.change);
      }
    },
    destroy() {
      if (screenfull.isEnabled) {
        // 关闭全屏
        screenfull.off("change", this.change);
      }
    }
  }
};
</script>

<style scoped>
.screenfull-svg {
  display: inline-block;
  cursor: pointer;
  fill: #5a5e66;
  width: 20px;
  height: 20px;
  vertical-align: 10px;
}
</style>
