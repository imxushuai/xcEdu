  <template>
    <div style="margin-top:10px; width:40%">
        <el-form :model="cmsPage" :rules="rules" ref="cmsPageForm" label-width="100px" class="demo-ruleForm">
            <el-form-item label="站点ID" prop="siteId">
                <!-- 站点ID下拉选择 -->
                <el-select v-model="cmsPage.siteId" placeholder="请选择站点">
                    <el-option
                        v-for="item in cmsSiteList"
                        :key="item.siteId"
                        :label="item.siteName"
                        :value="item.siteId">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="模板ID" prop="templateId">
                <!-- 模板ID下拉选择 -->
                <el-select v-model="cmsPage.templateId" placeholder="请选择模板">
                    <el-option
                        v-for="item in cmsTemplateList"
                        :key="item.templateId"
                        :label="item.templateName"
                        :value="item.templateId">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="页面名称" prop="pageName">
                <el-input v-model="cmsPage.pageName"></el-input>
            </el-form-item>
            <el-form-item label="别名" prop="pageAliase">
                <el-input v-model="cmsPage.pageAliase"></el-input>
            </el-form-item>
            <el-form-item label="访问路径" prop="pageWebPath">
                <el-input v-model="cmsPage.pageWebPath"></el-input>
            </el-form-item>
            <el-form-item label="物理路径" prop="pagePhysicalPath">
                <el-input v-model="cmsPage.pagePhysicalPath"></el-input>
            </el-form-item>
            <el-form-item label="DataUrl" prop="dataUrl">
                <el-input v-model="cmsPage.dataUrl"></el-input>
            </el-form-item>
            <el-form-item label="类型" prop="pageType">
                <el-radio-group v-model="cmsPage.pageType">
                <el-radio label="0">静态</el-radio>
                <el-radio label="1">动态</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item label="创建日期" prop="pageCreateTime">
                <el-date-picker
                    v-model="cmsPage.pageCreateTime"
                    type="datetime"
                    placeholder="选择日期时间">
                </el-date-picker>
            </el-form-item>
            <el-form-item>
                <el-button @click="goBack">返回</el-button>
                <el-button type="primary" @click="onSubmit('cmsPageForm')">提交</el-button>
            </el-form-item>
        </el-form>
    </div>
  </template>

  <script>
    import * as cmsApi from '../api/cms'
    export default {
      data() {
        return {
            cmsPage: {
                siteId:'', 
                templateId:'', 
                pageName: '', 
                pageAliase: '', 
                pageWebPath: '', 
                pageParameter:'', 
                pagePhysicalPath:'', 
                dataUrl:'',
                pageType:'', 
                pageCreateTime: new Date()
            },
            cmsSiteList:[],
            cmsTemplateList:[],
            rules: {
                siteId: [
                    { required: true, message: '请选择站点', trigger: 'change'}
                ],
                templateId: [
                    { required: true, message: '请选择模板', trigger: 'change' }
                ],
                pageCreateTime: [
                    { type: 'date', required: true, message: '请选择创建时间', trigger: 'change' }
                ],
                pageName: [
                    { required: true, message: '请输入页面名称', trigger: 'blur' }
                ],
                pageWebPath: [
                    { required: true, message: '请输入页面名称', trigger: 'blur' }
                ],
                pagePhysicalPath: [
                    { required: true, message: '请输入页面名称', trigger: 'blur' }
                ],
                dataUrl: [
                    { required: true, message: '请输入DataUrl', trigger: 'blur' }
                ],
                pageType: [
                    { required: true, message: '请选择页面类型', trigger: 'change' }
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
        // 查询所有模板cmsTemplate
        cmsTemplateQueryAll:function() {
            cmsApi.template_list_all().then(res => {
              this.cmsTemplateList = res.queryResult.list
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
                        cmsApi.add(this.cmsPage).then(res => {
                            this.$message({
                                showClose: true,
                                message: res.message,
                                type: 'success'
                            })
                            // 清空数据
                            this.cmsPage = {}
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
                    path:'/cms/page/list', 
                    query: {
                        page:this.$route.query.page,
                        siteId:this.$route.query.siteId
                    }
                })
            } else {
                this.$router.push({
                    path:'/cms/page/list'
                })
            }
        }
      },
      created() {
        
      },
      mounted() {
          this.cmsSiteQueryAll()
          this.cmsTemplateQueryAll()
      }
    }
  </script>