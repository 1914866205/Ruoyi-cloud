<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>
<script>
export default {
  name: 'RuoYiDoc',
  data() {
    return {
      url: 'http://doc.ruoyi.vip/ruoyi-cloud'
    }
  },
  methods: {
    goto() {
      window.open(this.url) //打开一个新的窗口
    }
  }
}
</script>
