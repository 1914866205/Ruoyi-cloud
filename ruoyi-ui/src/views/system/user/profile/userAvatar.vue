<template>
  <div>
    <div class="user-info-head" @click="editCropper()"> 
      <!-- editCropper() 打开对话框  -->
      <img v-bind:src="options.img" title="点击上传头像" class="img-circle img-lg" />
    </div>  
    <!-- 显示头像  options 是用户对象  -->
    <el-dialog :title="title" :visible.sync="open"  width="800px" append-to-body @opened="modalOpened"  @close="closeDialog">
      <!-- 对话框 
      需要设置visible属性，它接收Boolean，当为true时显示 Dialog。
      Dialog 分为两个部分：body和footer，
      footer需要具名为footer的slot。

      title属性用于定义标题，它是可选的，默认值为空。
      :visible.sync="open" 设置开关 open是布尔值
      append-to-body  Dialog 自身是否插入至 body 元素上。默认为 flase     嵌套的 Dialog 必须指定该属性并赋值为 true
      fullscreen	是否为全屏 Dialog 默认为false
      close-on-click-modal	是否可以通过点击 modal 关闭 Dialog	boolean	—	默认true
      close-on-press-escape	是否可以通过按下 ESC 关闭 Dialog	boolean	—	默认true
      show-close	是否显示关闭按钮

      Events
      事件名称	说明	回调参数
      open	Dialog 打开的回调	
      opened	Dialog 打开动画结束时的回调	
      close	Dialog 关闭的回调	
      closed	Dialog 关闭动画结束时的回调	
      -->
      <el-row>
        <!-- 左边列  是图片裁剪器 -->
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <vue-cropper
            ref="cropper"
            :img="options.img"
            :info="true"
            :autoCrop="options.autoCrop"
            :autoCropWidth="options.autoCropWidth"
            :autoCropHeight="options.autoCropHeight"
            :fixedBox="options.fixedBox"
            @realTime="realTime"
            v-if="visible"
            />
        </el-col>
          <!-- 
            基于vue的图片裁剪vue-cropper使用 https://blog.csdn.net/qq_41107231/article/details/109725839
                      img: store.getters.avatar, //裁剪图片的地址
                      info: true,          //图片大小信息
                      autoCrop: true, // 是否默认生成截图框
                      autoCropWidth: 200, // 默认生成截图框宽度
                      autoCropHeight: 200, // 默认生成截图框高度
                      fixedBox: true // 固定截图框大小 不允许改变
            通过按钮触发事件打开我们的剪裁窗口，选择图片，点击上传之后，将地址回调回来，
            拿到地址就可以处理我们的业务了，比如随着表单一起将回调地址存入数据库等等。

        img: '',             //裁剪图片的地址
        outputSize: 1,       //裁剪生成图片的质量(可选0.1 - 1)
        outputType: 'jpeg',  //裁剪生成图片的格式（jpeg || png || webp）
        info: true,          //图片大小信息
        canScale: true,      //图片是否允许滚轮缩放
        autoCrop: true,      //是否默认生成截图框
        autoCropWidth: 230,  //默认生成截图框宽度
        autoCropHeight: 150, //默认生成截图框高度
        fixed: true,         //是否开启截图框宽高固定比例
        fixedNumber: [1.53, 1], //截图框的宽高比例
        full: false,         //false按原比例裁切图片，不失真
        fixedBox: true,      //固定截图框大小，不允许改变
        canMove: false,      //上传图片是否可以移动
        canMoveBox: true,    //截图框能否拖动
        original: false,     //上传图片按照原始比例渲染
        centerBox: false,    //截图框是否被限制在图片里面
        height: true,        //是否按照设备的dpr 输出等比例图片
        infoTrue: false,     //true为展示真实输出图片宽高，false展示看到的截图框宽高
        maxImgSize: 3000,    //限制图片最大宽度和高度
        enlarge: 1,          //图片根据截图框输出比例倍数
        mode: '230px 150px'  //图片默认渲染方式
      -->

      <!-- 右边的图片预览 -->
        <el-col :xs="24" :md="12" :style="{height: '350px'}">
          <div class="avatar-upload-preview">
            <img :src="previews.url" :style="previews.img" />
            <!-- 显示的是 :src="previews.url" 预览图片 -->
          </div>
        </el-col>
      </el-row>
      <br />

      <!-- 行 -->
      <el-row>
        <el-col :lg="2" :md="2">
          <el-upload action="#" :http-request="requestUpload" :show-file-list="false" :before-upload="beforeUpload">
            <!-- 
              upload 上传
              https://element.eleme.cn/#/zh-CN/component/upload

              http-request	覆盖默认的上传行为，可以自定义上传的实现
              show-file-list	是否显示已上传文件列表 默认ture
              before-upload	上传文件之前的钩子，参数为上传的文件，若返回 false 或者返回 Promise 且被 reject，则停止上传。
             -->
            <el-button size="small">
              选择
              <i class="el-icon-upload el-icon--right"></i>
            </el-button>
          </el-upload>
          <!-- 上传图片 -->
        </el-col>
        <el-col :lg="{span: 1, offset: 2}" :md="2">
          <el-button icon="el-icon-plus" size="small" @click="changeScale(1)"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :md="2">
          <el-button icon="el-icon-minus" size="small" @click="changeScale(-1)"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :md="2">
          <el-button icon="el-icon-refresh-left" size="small" @click="rotateLeft()"></el-button>
        </el-col>
        <el-col :lg="{span: 1, offset: 1}" :md="2">
          <el-button icon="el-icon-refresh-right" size="small" @click="rotateRight()"></el-button>
        </el-col>
        <el-col :lg="{span: 2, offset: 6}" :md="2">
          <el-button type="primary" size="small" @click="uploadImg()">提 交</el-button>
        </el-col>
      </el-row>
    </el-dialog>
  </div>
</template>

<script>
import store from "@/store";
import { VueCropper } from "vue-cropper";
import { uploadAvatar } from "@/api/system/user";

export default {
  components: { VueCropper },
  props: {
    user: {
      type: Object  //用户信息
    }
  },
  data() {
    return {
      // 是否显示弹出层，控制对话框的开关
      open: false,
      // 是否显示cropper
      visible: false,
      // 弹出层标题
      title: "修改头像",
      options: {
        img: store.getters.avatar, //裁剪图片的地址
        autoCrop: true, // 是否默认生成截图框
        autoCropWidth: 200, // 默认生成截图框宽度
        autoCropHeight: 200, // 默认生成截图框高度
        fixedBox: true // 固定截图框大小 不允许改变
      },
      previews: {}
    };
  },
  methods: {
    // 编辑头像
    editCropper() {
      this.open = true; //打开对话框
    },
    // 打开弹出层结束时的回调
    // opened	Dialog 打开动画结束时的回调	—
    modalOpened() {
      this.visible = true;
    },
    // 覆盖默认的上传行为
    requestUpload() {
    },
    // 向左旋转
    rotateLeft() {
      // 调用组件的方法，左旋转
      this.$refs.cropper.rotateLeft();
    },
    // 向右旋转
    rotateRight() {
      // 调用组件的方法，右旋转
      this.$refs.cropper.rotateRight();
    },
    // 图片缩放
    changeScale(num) {
      num = num || 1;
      // 改变图片的大小
      this.$refs.cropper.changeScale(num);
    },
    // 上传预处理
    beforeUpload(file) {
      if (file.type.indexOf("image/") == -1) {
        this.$modal.msgError("文件格式错误，请上传图片类型,如：JPG，PNG后缀的文件。");
      } else {
        // 文件输入流
        const reader = new FileReader();
        // 读取一个文件
        /*
        readAsDataURL 方法会读取指定的 Blob 或 File 对象。读取操作完成的时候，
        readyState 会变成已完成DONE，并触发 loadend (en-US) 事件，同时 result 属性将包含一个data:URL格式的字符串（base64编码）以表示所读取文件的内容。
        https://developer.mozilla.org/zh-CN/docs/Web/API/FileReader/readAsDataURL
        */
       // https://blog.csdn.net/qq_42705221/article/details/83750352
       //  readAsDataURL: 方法可以将读取到的文件编码成DataURL ，可以将资料(例如图片)内嵌在网页之中，不用放到外部文件
        reader.readAsDataURL(file);
				//当读取成功后触发
        reader.onload = () => {
          // 把读取结果放到对象图片中
          // console.log(reader.result)
          // data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEBLAEsAAD/4QBaRXhpZ...
          this.options.img = reader.result;
        };
      }
    },
    // 上传图片
    uploadImg() {
      this.$refs.cropper.getCropBlob(data => {
        let formData = new FormData();
        formData.append("avatarfile", data);
        uploadAvatar(formData).then(response => {
          this.open = false;
          this.options.img = response.imgUrl;
          store.commit('SET_AVATAR', this.options.img);
          this.$modal.msgSuccess("修改成功");
          this.visible = false;
        });
      });
    },
    // 实时预览
    realTime(data) {
      this.previews = data;
    },
    // close	Dialog 关闭的回调	—
    // 关闭窗口
    closeDialog() {
      this.options.img = store.getters.avatar
      this.visible = false;
    }
  }
};
</script>
<style scoped lang="scss">
.user-info-head {
  position: relative;
  display: inline-block;
  height: 120px;
}

.user-info-head:hover:after {
  content: '+';
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  color: #eee;
  background: rgba(0, 0, 0, 0.5);
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  cursor: pointer;
  line-height: 110px;
  border-radius: 50%;
}
</style>