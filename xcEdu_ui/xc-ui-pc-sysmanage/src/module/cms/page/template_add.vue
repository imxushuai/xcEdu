  <template>
    <div style="margin-top:10px; width:40%">
        <el-form :model="cmsTemplate" :rules="rules" ref="cmsTemplateForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="站点ID" prop="siteId">
                <!-- 站点ID下拉选择 -->
                <el-select v-model="cmsTemplate.siteId" placeholder="请选择站点">
                    <el-option
                        v-for="item in cmsSiteList"
                        :key="item.siteId"
                        :label="item.siteName"
                        :value="item.siteId">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="模板名称" prop="templateName">
                <el-input v-model="cmsTemplate.templateName"></el-input>
            </el-form-item>
            <el-form-item label="模板参数" prop="templateParameter">
                <el-input v-model="cmsTemplate.templateParameter"></el-input>
            </el-form-item>
            <el-form-item label="模板文件ID">
                <el-upload
                    class="upload-demo"
                    drag
                    action="http://localhost:11000/api/cms/template/upload"
                    :multiple="multiple"
                    :limit="limit"
                    :on-success="uploadOnSuccess"
                    :on-remove="onRemove">
                    <i class="el-icon-upload"></i>
                    <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
                </el-upload>
            </el-form-item>

            <el-form-item>
                <el-button @click="goBack">返回</el-button>
                <el-button type="primary" @click="onSubmit('cmsTemplateForm')">提交</el-button>
            </el-form-item>
        </el-form>
    </div>
  </template>

  <script>
    import * as cmsApi from '../api/cms'
    export default {
      data() {
        return {
            multiple:false,
            limit:1,
            cmsSiteList:[],
            cmsTemplate: {
                templateFileId: '',
                templateName: '', 
                templateParameter: '',
                siteId: ''
            },
            rules: {
                siteId: [
                    { required: true, message: '请选择站点', trigger: 'change' }
                ],
                templateName: [
                    { required: true, message: '请输入模板名称', trigger: 'blur' }
                ],
                templateParameter: [
                    { required: true, message: '请输入模板参数', trigger: 'blur' }
                ]
            }
        }
      },
      methods: {
        // 查询所有站点cmsSite
        cmsSiteQueryAll:function() {
            cmsApi.site_list_all().then(res => {
              this.cmsSiteList = res.queryResult.list
            })
        },
        onSubmit:function(formname) {
            // 校验表单
            this.$refs[formname].validate((valid) => {
                if (valid) {// 校验通过
                    this.$confirm('确认提交?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'info'
                    }).then(() => {
                        cmsApi.addCmsTemplate(this.cmsTemplate).then(res => {
                            this.$message({
                                showClose: true,
                                message: res.message,
                                type: 'success'
                            })
                            // 清空数据
                            this.cmsTemplate = {}
                            // 回退页面
                            this.goBack()
                        }).catch(res => {
                            this.$message({
                                showClose: true,
                                message: '系统异常',
                                type: 'error'
                            })
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
                    path:'/cms/template/list', 
                    query: {
                        page:this.$route.query.page
                    }
                })
            } else {
                this.$router.push({
                    path:'/cms/template/list'
                })
            }
        },
        // 模板文件上传成功
        uploadOnSuccess:function(response, file, fileList) {
            if (response) {
                this.cmsTemplate.templateFileId = response
                this.$message({
                    showClose: true,
                    message: '模板文件上传成功',
                    type: 'success'
                })
            }
        },
        // 移除模板文件
        onRemove:function(file, fileList) {
            // 调用API 删除文件
            cmsApi.removeTemplateFileById(this.cmsTemplate.templateFileId).then(res => {
                this.$message({
                    showClose: true,
                    message: '删除成功',
                    type: 'success'
                })
            })
        }
      },
      created() {
        
      },
      mounted() {
          this.cmsSiteQueryAll()
      }
    }
  </script>