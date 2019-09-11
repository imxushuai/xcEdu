  <template>
    <div>
        <!-- 站点ID下拉选择 -->
        <el-select v-model="params.siteId" placeholder="请选择站点">
          <el-option
            v-for="item in cmsSiteList"
            :key="item.siteId"
            :label="item.siteName"
            :value="item.siteId">
          </el-option>
        </el-select>
        <!-- 页面别名输入框 -->
        <span style="margin-left: 10px;">页面别名：</span>  
        <el-input v-model="params.pageAliase" placeholder="" style="width: 200px"></el-input>
        <el-button type="primary" icon="el-icon-search" @click="query" style="margin-top: 20px">查询</el-button>
        <!-- 新增页面按钮 -->
        <router-link class="mui-tab-item" :to="{path:'/cms/page/add/',query:{ page: this.params.page, siteId: this.params.siteId}}">
          <el-button type="primary" icon="el-icon-plus" style="margin-top: 20px">新增页面</el-button>
        </router-link>
        <!-- 页面展示表格 -->
        <el-table :data="tableData" stripe style="width: 100%;margin-top:20px">
            <el-table-column type="index" width="70"></el-table-column>
            <el-table-column prop="pageName" label="页面名称" width="400"></el-table-column>
            <el-table-column prop="pageAliase" label="别名" width="120"></el-table-column>
            <el-table-column prop="pageType" label="页面类型" width="100" :formatter="pageTypeFormatter"></el-table-column>
            <el-table-column prop="pageWebPath" label="访问路径" width="250"></el-table-column>
            <el-table-column prop="pagePhysicalPath" label="物理路径" width="250"></el-table-column>
            <el-table-column prop="pageCreateTime" label="创建时间"></el-table-column>
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
          cmsSiteList: [],
          total: 50,
          params: {
              page: 1,
              size: 10,
              siteId: '',
              pageAliase:''
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
            // 分页查询CMS页面
            console.log("当前页码：" + this.params.page + ", 当前记录数：" + this.params.size);
            cmsApi.page_list(this.params.page, this.params.size, this.params).then(res => {
                // 获取数据
                this.total = res.queryResult.total
                this.tableData = res.queryResult.list
            })
        },
        // 查询所有站点cmsSite
        cmsSiteQueryAll:function() {
            cmsApi.site_list_all().then(res => {
              this.cmsSiteList = res.queryResult.list
            })
        },
        // 编辑cms页面
        edit:function(index, data) {
          this.$router.push({
                    path:'/cms/page/edit', 
                    query: {
                        page:this.params.page,
                        siteId:this.params.siteId,
                        pageId:data.pageId
                    }
                })
        },
        // 删除cms页面
        del:function(index, data) {
          this.$confirm('确认删除该条记录?', '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
          }).then(() => {
              // 删除
              cmsApi.deleteCmsPageById(data.pageId).then(res => {
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
        },
        // 页面类型数据格式化
        pageTypeFormatter:function(row, column, cellValue, index) {
          if (row.pageType === '1') {
            return '动态'
          } else if (row.pageType === '0') {
            return '静态'
          }
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