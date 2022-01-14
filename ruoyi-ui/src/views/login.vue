<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">若依后台管理系统</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaOnOff">
        <!-- el开头，是element UI   官网： https://element.eleme.cn/#/zh-CN/component/installation -->
        <!-- code 是自己输入的验证码，此处先讲获取验证码
        在 Vue 中，父子组件的关系可以总结为 props down, events up。父组件通过 props 向下传递数据给子组件，子组件通过 events 给父组件发送消息。
        显然这个code是父组件通过prop传递数据给子组件的
         -->
        <!-- captchaOnOff 初始值为true,是验证码开关 -->
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
        <!-- 此处绑定了loginForm.code，这个code是表单对象里的code，不是上文说的code

        auto-complete="off"   关闭代码自动补全，什么意思呢，把鼠标光标放到输入框，不会自动出现下拉框，如果改为on,则会出现近期输入的数据。
        autocomplete 属性规定输入字段是否应该启用自动完成功能。默认情况下是启动的，也就是当你点击了input
        获取焦点之后浏览器会自动将以前的输入记录作为填入选项显示出来。这个是HTML5中的新属性，在不支持HTML5的浏览器下是没有用的。
        注释：autocomplete 属性适用于 <form>，以及下面的 <input> 类型：text, search, url, telephone, email, password, datepickers, range 以及 color。
        在一部分浏览器中，autocomplete 属性是默认关闭的，如果需要打开就打开 一下。因为这是input自带的数学，会在自己的div上加了一层div的ul列表。

        placeholder 属性提供可描述输入字段预期值的提示信息（hint）。
        该提示会在输入字段为空时显示，并会在字段获得焦点时消失。
        @keyup.enter  是  vue 监听键盘回车事件
        如果是封装了组件，要用
        @keyup.enter.native

        最后效果就是，按下回车键，调用handleLogin方法
         -->
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          <!-- Vue中slot的使用   https://www.cnblogs.com/qq735675958/p/8377761.html
           是对组件的扩展，通过slot插槽向组件内部指定位置传递内容，通过slot可以父子传参
           即 slot的出现是为了父组件可以堂而皇之地在子组件中加入内容。
<span slot=”header”>hello world</span>
<slot  name=”header”>这是拥有命名的slot的默认内容</slot>
      显然这个叫 prefix 的slot 就是上文提到过的gif
           -->
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
          <!-- 根据codeUrl显示验证码图片，点击这个img会调用 getCode方法 -->
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleLogin"
        >
          <!-- 绑定一个 loading参数，默认是false，意思是  是不是加载中呗

          type	类型	string	primary / success / warning / danger / info / text

           @click.native.prevent
          1.给vue组件绑定事件时候，必须加上native ，否则会认为监听的是来自Item组件自定义的事件，
          2.prevent 是用来阻止默认的 ，相当于原生的event.preventDefault()
          注意：preventDefault() 方法阻止元素发生默认的行为（例如，当点击提交按钮时阻止对表单的提交）。

          显然，这个按钮会触发一个  handleLogin  的方法
           -->
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
          <!-- 这里就是根据 loading 的值动态显示要显示的内容 -->
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
      <span>Copyright © 2018-2021 ruoyi.vip All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaOnOff: true,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        // 会调用 getCodeImg()接口，把返回的res进行处理
        this.captchaOnOff = res.captchaOnOff === undefined ? true : res.captchaOnOff;
        //如果返回的captchaOnOff是undefined，估计是没访问出来啥东西，就返回true，否则返回false
        // 什么鬼？仔细看看data里的captchaOnOff,嗷嗷，是验证码开关，初始值为true，所以这行的代码就是能不能继续 请求验证码 呗
        //如果是true，说明没返回出来东西，肯定就能继续请求，如果是false，那就不能继续请求验证码
        //再看一下这个在哪用captchaOnOff，哦，原来是   <el-form-item prop="code" v-if="captchaOnOff">
        //显然，如果captchaOnOff为false就不会显示这个组件了
        if (this.captchaOnOff) {  //如果有验证码
          this.codeUrl = "data:image/gif;base64," + res.img; //codeUrl的值即改为 data:image/gif;base64,res.img
          //如果你不懂 base64   看看这个 https://blog.csdn.net/weixin_38465623/article/details/80199999
          //当然，也可以看我总结的
          /**
           * base64             data:image/gif;base64," + res.img
           * data表示取得数据的协定名称，image/gif 是数据类型名称，base64 是数据的编码方法，逗号后面就是这个image/gif文件base64编码后的数据。
           * 目前，Data URI scheme支持的类型有：
              data:,文本数据
              data:text/plain,文本数据
              data:text/html,HTML代码
              data:text/html;base64,base64编码的HTML代码
              data:text/css,CSS代码
              data:text/css;base64,base64编码的CSS代码
              data:text/javascript,Javascript代码
              data:text/javascript;base64,base64编码的Javascript代码
              data:image/gif;base64,base64编码的gif图片数据
              data:image/png;base64,base64编码的png图片数据
              data:image/jpeg;base64,base64编码的jpeg图片数据
            base64简单地说，它把一些 8-bit 数据翻译成标准 ASCII 字符，网上有很多免费的base64 编码和解码的工具，在PHP中可以用函数base64_encode() 进行编码
举个图片的例子：
网页中一张图片可以这样显示：
            <img src=“http://www.aimks.com/images/wg.png”/>
也可以这样显示
            <img src=“data:image/png;base64,iVBORw0KGgoAAAANSUhEU
            gAAAAEAAAAkCAYAAABIdFAMAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZS
            BJbWFnZVJlYWR5ccllPAAAAHhJREFUeNo8zjsOxCAMBFB/KEAUFFR
            0Cbng3nQPw68ArZdAlOZppPFIBhH5EAB8b+Tlt9MYQ6i1BuqFaq1C
            KSVcxZ2Acs6406KUgpt5/LCKuVgz5BDCSb13ZO99ZOdcZGvt4mJjz
            MVKqcha68iIePB86GAiOv8CDADlIUQBs7MD3wAAAABJRU5ErkJggg%3D%3D”/>
           */
          //显然 res.img就是传来的图片，在后端生成图片，base64图片转成字符 传输回前端，再在前端  字符转成图片  显示给用户   妙啊秒啊
          this.loginForm.uuid = res.uuid;
          //并且表单的uuid设置为res.uuid
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        /**
         * 以前经常用 this.refs，只知道这样用，不知道是干啥的，今天刚好看看了
         * 参考  https://blog.csdn.net/qq_38128179/article/details/88876060
         *   在JavaScript中需要通过document.querySelector("#demo")来获取dom节点，然后再获取这个节点的值。
         * 在Vue中，我们不用获取dom节点，元素绑定ref之后，直接通过this.$refs即可调用，这样可以减少获取dom节点的消耗。
         *
         * 咦，在哪绑定的ref
         * 哦，在第三行
         * <template>
                 <div class="login">
                    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
                           <h3 class="title">若依后台管理系统</h3>

           通俗的讲，ref特性就是为元素或子组件赋予一个ID引用,通过this.$refs.refName来访问元素或子组件的实例
           this.$refs是一个对象，持有当前组件中注册过 ref特性的所有 DOM 元素和子组件实例
           且只有在组件渲染完成后才填充，在初始渲染的时候不能访问它们，并且它是非响应式的，因此不能用它在模板中做数据绑定

         validate 根据自定义的规则进行校验，符合规则返回true，否则false
         */
        if (valid) {
          this.loading = true; //表示现在开始登录
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            //this.$store.dispatch(‘Login’, this.loginForm)来调取store里的user.js的login方法，从而要更新。
            //参考 https://blog.csdn.net/longzhoufeng/article/details/103658726
            //dispatch：含有异步操作，数据提交至 actions ，可用于向后台提交数据
            // this.$store.dispatch('isLogin', true);
            // commit：同步操作，数据提交至 mutations ，可用于读取用户信息写到缓存里
            // this.$store.commit('loginStatus', 1);

            //如果登录成功，路由转发到首页
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaOnOff) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.login-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.login-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.login-code-img {
  height: 38px;
}
</style>
