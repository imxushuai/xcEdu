  <template>
    <div>
        <el-button type="primary" icon="el-icon-search" @click="query" style="margin-top: 20px">查询</el-button>
        <!-- 新增站点按钮 -->
        <router-link class="mui-tab-item" :to="{path:'/cms/site/add/'}">
          <el-button type="primary" icon="el-icon-search" style="margin-top: 20px">新增站点</el-button>
        </router-link>
        <!-- 站点展示表格 -->
        <el-table :data="tableData" style="width: 100%;margin-top:20px">
            <el-table-column type="index" width="70"></el-table-column>
            <el-table-column prop="siteName" label="站点名称" width="200"></el-table-column>
            <el-table-column prop="siteDomain" label="站点域" width="320"></el-table-column>
            <el-table-column prop="sitePort" label="站点端口" width="100"></el-table-column>
            <el-table-column prop="siteWebPath" label="访问路径" width="250"></el-table-column>
            <el-table-column prop="siteCreateTime" label="创建时间"></el-table-column>
            <el-table-column label="操作">
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
              size: 20,
          }
        }
      },
      methods: {
        // cmsPage分页查询
        changePage:function() {
            this.query()
        },
        // 分页查询CmsPage
        query:function() {
            // 分页查询CMS站点
            console.log("当前页码：" + this.params.page + ", 当前记录数：" + this.params.size);
            cmsApi.site_list(this.params.page, this.params.size).then(res => {
                // 获取数据
                this.total = res.queryResult.total
                this.tableData = res.queryResult.list
            })
        },
        // 编辑cms站点
        edit:function(index, data) {
          this.$router.push({
                    path:'/cms/site/edit', 
                    query: {
                        page:this.params.page,
                        siteId:data.siteId
                    }
                })
        },
        // 删除cms站点
        del:function(index, data) {
          this.$confirm('确认删除该条记录?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
          }).then(() => {
              // 删除
              cmsApi.deleteCmsSiteById(data.siteId).then(res => {
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
          this.cmsSiteQueryAll()
      },
      created() {
        this.params.page = Number.parseInt(this.$route.query.page || 1)
        this.params.siteId = this.$route.query.siteId || ''
      }
    }
  </script>