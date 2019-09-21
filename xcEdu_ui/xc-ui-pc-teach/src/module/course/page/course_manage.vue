<template>
  <div style="margin-top: 10px">
    <el-menu
      :default-active="activeIndex2"
      class="el-menu-demo"
      mode="horizontal"
      background-color="#eee"
      text-color="#000"
      active-text-color="#000">
      <el-menu-item index="1">
        <router-link :to="{path:'/course/manage/summary/'+this.courseid}">课程首页</router-link>  
      </el-menu-item>
      <el-menu-item index="2">
        <router-link class="mui-tab-item" :to="{path:'/course/manage/baseinfo/'+this.courseid}">基本信息</router-link>
      </el-menu-item>
      <el-menu-item index="3">
        <router-link class="mui-tab-item" :to="{path:'/course/manage/picture/'+this.courseid}">课程图片</router-link>
      </el-menu-item>
      <el-menu-item index="4">
        <router-link class="mui-tab-item" :to="{path:'/course/manage/marketinfo/'+this.courseid}">课程营销</router-link>
      </el-menu-item>
      <el-menu-item index="5">
        <router-link class="mui-tab-item" :to="{path:'/course/manage/plan/'+this.courseid}">课程计划</router-link>
      </el-menu-item>
      <el-menu-item index="6">
        <router-link class="mui-tab-item" :to="{path:'/course/manage/teacher/'+this.courseid}">教师信息</router-link>
      </el-menu-item>
      <el-menu-item index="7">
        <router-link class="mui-tab-item" :to="{path:'/course/manage/pub/'+this.courseid}">发布课程</router-link>
      </el-menu-item>
    </el-menu>
    <router-view class="main" style="margin-top: 2px"></router-view>
  </div>
</template>
<script>
  import * as courseApi from '../api/course';
  import utilApi from '../../../common/utils';
  export default {
    data() {
      return {
        activeIndex:'2',
        courseid:''
      }
    },
    methods: {

    },
    mounted(){

      //课程id
      this.courseid = this.$route.params.courseid

      console.log("courseid=" + this.courseid)
      //跳转到课程基本信息
      this.$router.push({ path: '/course/manage/baseinfo/'+this.courseid})

    }
  }
</script>
<style scoped>


</style>
