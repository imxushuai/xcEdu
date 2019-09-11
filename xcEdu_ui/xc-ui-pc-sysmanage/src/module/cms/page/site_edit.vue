  <template>
    <div style="margin-top:10px; width:40%">
        <el-form :model="cmsSite" :rules="rules" ref="cmsSiteForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="站点名称" prop="siteName">
                <el-input v-model="cmsSite.siteName"></el-input>
            </el-form-item>
            <el-form-item label="站点域" prop="siteDomain">
                <el-input v-model="cmsSite.siteDomain"></el-input>
            </el-form-item>
            <el-form-item label="站点端口" prop="sitePort">
                <el-input v-model="cmsSite.sitePort"></el-input>
            </el-form-item>
            <el-form-item label="访问路径" prop="siteWebPath">
                <el-input v-model="cmsSite.siteWebPath"></el-input>
            </el-form-item>
            <el-form-item label="创建日期" prop="siteCreateTime">
                <el-date-picker
                    v-model="cmsSite.siteCreateTime"
                    type="datetime"
                    placeholder="选择日期时间">
                </el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button @click="goBack">返回</el-button>
                <el-button type="primary" @click="onSubmit('cmsSiteForm')">提交</el-button>
            </el-form-item>
        </el-form>
    </div>
  </template>

  <script>
    import * as cmsApi from '../api/cms'
    export default {
      data() {
        return {
            cmsSite: {
                siteId: '',
                siteName: '', 
                siteDomain: '', 
                sitePort: '',
                siteWebPath:'',
                siteCreateTime: new Date()
            },
            rules: {
                siteCreateTime: [
                    { required: true, message: '请选择创建时间', trigger: 'change' }
                ],
                siteDomain: [
                    { required: true, message: '请输入站点域', trigger: 'blur' }
                ],
                siteName: [
                    { required: true, message: '请输入站点名称', trigger: 'blur' }
                ],
                sitePort: [
                    { required: true, message: '请输入站点端口', trigger: 'blur' }
                ],
                siteWebPath: [
                    { required: true, message: '请输入站点访问路径', trigger: 'blur' }
                ]
            }
        }
      },
      methods: {
        onSubmit:function(formname) {
            // 校验表单
            this.$refs[formname].validate((valid) => {
                if (valid) {// 校验通过
                    this.$confirm('确认提交?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'info'
                    }).then(() => {
                        cmsApi.editCmsSite(this.cmsSite).then(res => {
                            this.$message({
                                showClose: true,
                                message: res.message,
                                type: 'success'
                            })
                            // 清空数据
                            this.cmsSite = {}
                            // 回退页面
                            this.goBack()
                        })
                    })
                } else {
                    this.$message({
                        showClose: true,
                        message: '提交失败，请检查是否正确录入数据！',
                        type: 'error'
                    });
                }
            });
            
        },
        goBack:function() {
            if (this.$route.query.page) {
                // 返回
                this.$router.push({
                    path:'/cms/site/list', 
                    query: {
                        page:this.$route.query.page,
                        siteId:this.$route.query.siteId
                    }
                })
            } else {
                this.$router.push({
                    path:'/cms/site/list'
                })
            }
        },
        getCmsSite:function(siteId) {
            cmsApi.findCmsSiteById(siteId).then(res => {
                this.cmsSite = res.cmsSite
            })
        }
      },
      created() {
        this.getCmsSite(this.$route.query.siteId)
      }
    }
  </script>