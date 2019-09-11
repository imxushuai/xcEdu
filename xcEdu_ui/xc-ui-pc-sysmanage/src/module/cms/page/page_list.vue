  <template>
    <div>
        <!-- 站点ID下拉选择 -->
        <el-select v-model="params.siteId" placeholder="请选择站点">
          <el-option
            v-for="item in cmsSiteList"
            :key="item.siteId"
            :label="item.siteName"
            :value="params.siteId">
          </el-option>
        </el-select>
        <!-- 页面别名输入框 -->
        <span style="margin-left: 10px;">页面别名：</span>  
        <el-input v-model="params.pageAliase" placeholder="" style="width: 200px"></el-input>
        <el-button type="primary" icon="el-icon-search" @click="query" style="margin-top: 20px">查询</el-button>
        <el-table :data="tableData" style="width: 100%">
            <el-table-column prop="pageName" label="页面名称" width="400"></el-table-column>
            <el-table-column prop="pageAliase" label="别名" width="120"></el-table-column>
            <el-table-column prop="pageType" label="页面类型" width="150"></el-table-column>
            <el-table-column prop="pageWebPath" label="访问路径" width="250"></el-table-column>
            <el-table-column prop="pagePhysicalPath" label="物理路径" min-width="250"></el-table-column>
            <el-table-column prop="pageCreateTime" label="创建时间"></el-table-column>
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
              size: 20,
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
        }
      },
      mounted() {
          this.query()
          this.cmsSiteQueryAll()
      }
    }
  </script>