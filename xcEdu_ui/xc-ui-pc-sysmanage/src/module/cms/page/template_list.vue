  <template>
    <div>
        <el-button type="primary" icon="el-icon-search" @click="query" style="margin-top: 20px">查询</el-button>
        <!-- 新增站点按钮 -->
        <router-link class="mui-tab-item" :to="{path:'/cms/template/add/'}">
          <el-button type="primary" icon="el-icon-plus" style="margin-top: 20px">新增模板</el-button>
        </router-link>
        <!-- 站点展示表格 -->
        <el-table :data="tableData" style="width: 100%;margin-top:20px">
            <el-table-column type="index" width="70"></el-table-column>
            <el-table-column prop="siteId" label="站点ID" width="300"></el-table-column>
            <el-table-column prop="templateName" label="模板名称" width="320"></el-table-column>
            <el-table-column prop="templateParameter" label="模板参数" width="400"></el-table-column>
            <el-table-column prop="templateFileId" label="模板文件ID"></el-table-column>
            <el-table-column align="center"  label="操作" fixed="right" width="200px">
              <template slot-scope="scope">
                <el-button
                  size="small"
                  type="text"
                  @click="edit(scope.$index, scope.row)">编辑</el-button>
                <el-button
                  size="small"
                  type="text"
                  @click="del(scope.$index, scope.row)">删除</el-button>
              </template>
            </el-table-column>
        </el-table>
        <!-- 分页栏 -->
        <el-pagination
            @current-change="changePage"
            :current-page.sync="params.page"
            :page-size="params.size"
            layout="total, prev, pager, next"
            :total="total"
            style="float: right">
        </el-pagination>
    </div>
  </template>

  <script>
    import * as cmsApi from '../api/cms'
    export default {
      data() {
        return {
          tableData: [],
          total: 50,
          params: {
              page: 1,
              size: 10,
          }
        }
      },
      methods: {
        // cmsTemplate分页查询
        changePage:function() {
            this.query()
        },
        // 分页查询cmsTemplate
        query:function() {
            // 分页查询cmsTemplate
            cmsApi.template_list(this.params.page, this.params.size).then(res => {
                // 获取数据
                this.total = res.queryResult.total
                this.tableData = res.queryResult.list
            })
        },
        // 编辑cmsTemplate
        edit:function(index, data) {
          this.$router.push({
                    path:'/cms/template/edit', 
                    query: {
                        page:this.params.page,
                        templateId:data.templateId
                    }
                })
        },
        // 删除cmsTemplate
        del:function(index, data) {
          this.$confirm('确认删除该条记录?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
          }).then(() => {
              // 删除
              cmsApi.deleteCmsTemplateById(data.templateId).then(res => {
                // 提示消息
                this.$message({
                  showClose: true,
                  message: res.message,
                  type: 'success'
                })
              })
              
              // 重新查询数据
              this.query()
          })
        }
      },
      mounted() {
          this.query()
      },
      created() {
        this.params.page = Number.parseInt(this.$route.query.page || 1)
      }
    }
  </script>