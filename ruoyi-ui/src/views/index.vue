<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <!--el-row
      element-ui框架行元素
       gutter	栅格间隔  指定每一栏之间的间隔，默认间隔为 0。 -->
      <el-col :sm="24" :lg="24">
        <!--el-col
      响应式布局
      参照了 Bootstrap 的 响应式设计，预设了五个响应尺寸：xs、sm、md、lg 和 xl。
      element-ui框架列元素
        sm	≥768px 响应式栅格数或者栅格属性对象
        lg	≥1200px 响应式栅格数或者栅格属性对象
        -->
        <blockquote class="text-warning" style="font-size: 14px">
          <!--
            class="text-warning"  文本设为黄色
              标签选择器
              blockquote {
                padding: 10px 20px;
                margin: 0 0 20px;
                font-size: 17.5px;
                border-left: 5px solid #eee;
              }
              <blockquote>标签用于定义长引文（块级引文）。
              浏览器会在<blockquote>元素内容的前后分别插入一行空白，并在内容的左右两侧增添边距。
           -->
          涛涛的若依学习笔记——项目启动
          <el-link href="https://blog.csdn.net/niTaoTaoa/article/details/122459581" type="primary" target="_blank"
            >>☛☛点我进入☚☚</el-link
          >
          <!--el-link
      element-ui框架链接元素

      参数	说明	类型	      可选值	                                      默认值
      type	类型	string	primary / success / warning / danger / info      — default
      underline	是否下划线	boolean                                        —	true
      disabled	是否禁用状态	boolean                                       —	false
      href	原生 href 属性	string
      icon	图标类名	string

      target="_blank"
      标准浏览器、IE9+是新标签打开链接url
      ie6-8是新窗口打开链接url
       --><br />
           &nbsp;&nbsp;&nbsp; 涛涛的若依学习笔记——验证码的一整套引入——上（验证码获取）
          <!--&nbsp;一个空格  -->
          <el-link href="https://blog.csdn.net/niTaoTaoa/article/details/122463708" type="primary" target="_blank"
            >>☛☛点我进入☚☚</el-link
          ><br />
              &nbsp;&nbsp;&nbsp; 涛涛的若依学习笔记——验证码的一整套引入——下（验证码校验）
          <!--&nbsp;一个空格  -->
          <el-link href="https://blog.csdn.net/niTaoTaoa/article/details/122492792" type="primary" target="_blank"
            >>☛☛点我进入☚☚</el-link
          ><br />
                 &nbsp;&nbsp;&nbsp; 涛涛的若依学习笔记——登录
          <!--&nbsp;一个空格  -->
          <el-link href="https://blog.csdn.net/niTaoTaoa/article/details/122528410" type="primary" target="_blank"
            >>☛☛点我进入☚☚</el-link
          ><br />
                    &nbsp;&nbsp;&nbsp; 史上最简单的 SpringCloud 教程 | 终章
          <!--&nbsp;一个空格  -->
          <el-link href="https://forezp.blog.csdn.net/article/details/70148833" type="primary" target="_blank"
            >>☛☛点我进入☚☚</el-link
          ><br />
          <!--<br /> 换行  -->
          <h4 class="text-danger">
            涛涛英语学不进去
          </h4>
        </blockquote>
        <hr />
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <!--el-row
      element-ui框架行元素
       gutter	栅格间隔  指定每一栏之间的间隔，默认间隔为 0。 -->
      <el-col :sm="24" :lg="12" style="padding-left: 20px">
        <!--el-col
      响应式布局
      参照了 Bootstrap 的 响应式设计，预设了五个响应尺寸：xs、sm、md、lg 和 xl。
      element-ui框架列元素
        sm	≥768px 响应式栅格数或者栅格属性对象
        lg	≥1200px 响应式栅格数或者栅格属性对象
        -->
        <h2>若依后台管理框架</h2>
        <p>
          一直想学一款后台管理系统，如此找到了若依管理系统。她可以用于所有的Web应用程序，如网站管理后台，网站会员中心，CMS，CRM，OA等等，当然，也可以对她进行深度定制，以做出更强系统。所有前端后台代码封装过后十分精简易上手，出错概率低。同时支持移动客户端访问。
        </p>
        <p>
          <b>当前版本:</b> <span>v{{ version }}</span>
          <!--
            <b>这是粗体文本</b>

            <span> 用于对文档中的行内元素进行组合。
            <span> 标签没有固定的格式表现。当对它应用样式时，它才会产生视觉上的变化。如果不对 <span> 应用样式，那么 <span> 元素中的文本与其他文本不会任何视觉上的差异。
            <span> 标签提供了一种将文本的一部分或者文档的一部分独立出来的方式。

            {{version}} vue显示data数据的方式，显示的是data里的version数据
          -->
        </p>
        <p>
          <el-tag type="danger">&yen;免费开源</el-tag>
          <!--    <el-tag type="danger"> </el-tag>   这个标签确实好看-->
          <!--
            参考  https://www.w3school.com.cn/html/html_entities.asp
                  显示结果	描述	实体名称	实体编号
                    空格	&nbsp;	&#160;
                  <	小于号	&lt;	&#60;
                  >	大于号	&gt;	&#62;
                  &	和号	&amp;	&#38;
                  "	引号	&quot;	&#34;
                  '	撇号 	&apos; (IE不支持)	&#39;
                  ￠	分（cent）	&cent;	&#162;
                  £	镑（pound）	&pound;	&#163;
                  ¥	元（yen）	&yen;	&#165;
                  €	欧元（euro）	&euro;	&#8364;
                  §	小节	&sect;	&#167;
                  ©	版权（copyright）	&copy;	&#169;
                  ®	注册商标	&reg;	&#174;
                  ™	商标	&trade;	&#8482;
                  ×	乘号	&times;	&#215;
                  ÷	除号	&divide;	&#247;
           -->
        </p>
        <p>
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-cloudy"
            plain
            @click="goTarget('https://gitee.com/y_project/RuoYi-Cloud')"
            >访问码云</el-button
          >
          <!--
            https://element.eleme.cn/#/zh-CN/component/button
icon="el-icon-cloudy"  云朵图标
plain 	是否朴素按钮	boolean	—	false
去掉该属性后，鼠标悬停在上面和移开后，按钮样式不变
添加该属性后，鼠标不在上面时，通体浅色，鼠标移动到上面后，通体深色
           -->
          <el-button
            size="mini"
            icon="el-icon-s-home"
            plain
            @click="goTarget('http://ruoyi.vip')"
            >访问主页</el-button
          >
        </p>
      </el-col>

      <el-col :sm="24" :lg="12" style="padding-left: 50px">
        <el-row>
          <el-col :span="12">
            <!-- span	栅格占据的列数	number	—	24 -->
            <h2>技术选型</h2>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <h4>后端技术</h4>
            <ul>
              <li>SpringBoot</li>
              <li>SpringCloud</li>
              <li>...</li>
            </ul>
          </el-col>
          <el-col :span="6">
            <h4>前端技术</h4>
            <ul>
              <li>Vue</li>
              <li>Vuex</li>
              <li>...</li>
            </ul>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
    <el-divider />
    <!--
      https://element.eleme.cn/#/zh-CN/component/divider
区隔内容的分割线。
      参数	说明	类型	可选值	默认值
direction	设置分割线方向	string	horizontal / vertical	horizontal
content-position	设置分割线文案的位置	string	left / right / center	center
     -->
    <el-row :gutter="20">
      <!-- 栅格间隙为20 -->


      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <!-- https://element.eleme.cn/#/zh-CN/component/card -->
          <div slot="header" class="clearfix">
            <!-- 设置 header，也可以通过 slot#header 传入 DOM  -->
            <span>联系信息</span>
          </div>
          <div class="body">
            <p>
              <i class="el-icon-s-promotion"></i> 官网：<el-link
                href="http://www.ruoyi.vip"
                target="_blank"
                >http://www.ruoyi.vip</el-link
              >
              <!--
                i 斜体文本
                 target="_blank"  IE9+是打开一个新标签
              IE/-8是打开一个新窗口 -->
            </p>
            <p>
              <i class="el-icon-user-solid"></i> QQ：<del>18851855106</del>
              <!-- <s></s>  对那些不正确、不准确或者没有用的文本进行标识。  -->
              <a href="https://jq.qq.com/?_wv=1027&k=qdT1Ojpz" target="_blank">
                1914866205</a
              >
            </p>
            <p>
              <i class="el-icon-chat-dot-round"></i> 微信：<a
                href="javascript:;"
                >/ *涛涛</a>
                <!--  href="javascript:;"
                 执行一段空白的javascript语句，返回空或者false值，从而防止链接跳转。跟当前a标签无关，这段代码始终都会执行。 -->
            </p>
            <p>
              <i class="el-icon-money"></i> 支付宝：<a
                href="javascript:;"
                class="支付宝信息"
                >/ *涛涛</a
              >
            </p>
          </div>
        </el-card>
      </el-col>


      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>更新日志</span>
          </div>
          <el-collapse accordion>
            <!--el-collapse 折叠面板
            https://element.eleme.cn/#/zh-CN/component/collapse
            手风琴效果
            每次只能展开一个面板
            通过 accordion 属性来设置是否以手风琴模式显示。 去掉该属性后，可以同时展开多个面板
              -->
            <el-collapse-item title="v3.3.0 - 2021-12-13">
              <ol>
                <li>新增配套并同步的Vue3前端版本</li>
                <li>修复代码生成复选框字典遗漏问题</li>
                <li>修复代码生成模板主子表删除缺少事务</li>
                <li>其他细节优化</li>
              </ol>
            </el-collapse-item>
            <el-collapse-item title="v1.0.0 - 2020-05-20">
              <ol>
                <li>若依微服务系统正式发布</li>
              </ol>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>


      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>可爱支持</span>
          </div>
          <div class="body">
            <img
              src="https://img2.baidu.com/it/u=3013615533,1404649440&fm=253&fmt=auto&app=138&f=JPEG?w=450&h=450"
              alt="donate"
              width="100%"
            />
            <span style="display: inline-block; height: 30px; line-height: 30px"
              >你可以夸我帅</span
            >
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      // 版本号
      version: "3.3.0"
    };
  },
  methods: {
    goTarget(href) {
      /**
       * 参考
       * https://www.cnblogs.com/wonyun/p/5284163.html
       * window.open打开新窗口还是打开新标签页
 调用window.open是打开新窗口，还是打开新标签页，其实没有什么要紧关系，但是有些需求在这方面有很强的意愿时，那就得区分一下了。具体的打开什么还是根据具体情况来定的，以下结论是经过本人测试得出的，若有不正确的地方，请大家批评指正。
1、window.open(url)或者window.open(url, name),其中name为_blank
标准浏览器、IE9+是新标签打开链接url
ie6-8是新窗口打开链接url
2、window.open(url, name)，其中name为非_blank的其他4个值
　　此时会会在指定窗口或者frame打开链接url

              窗口name值	描述
              _blank	默认的，在新窗口打开链接的url
              _self	在当前窗口打开链接url
              _parent	在父窗口打开链接url
              _top	在顶级窗口打开url
              framename	在指定的框架中打开链接url
       */
      window.open(href, "_blank");
      /**
       * 标准浏览器、IE9+是新标签打开链接url
          ie6-8是新窗口打开链接url
       */
    }
  }
};
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>
