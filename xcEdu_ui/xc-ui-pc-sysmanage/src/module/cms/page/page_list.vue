  <template>
    <div>
        <el-table :data="tableData" style="width: 100%">
            <el-table-column prop="pageName" label="页面名称" width="120"></el-table-column>
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
          total: 50,
          params: {
              page: 1,
              size: 20
          }
        }
      },
      methods: {
        // 分页查询
        changePage:function() {
            this.query()
        },
        // 查询
        query:function() {
            // 分页查询CMS页面
            console.log("当前页码：" + this.params.page + ", 当前记录数：" + this.params.size);
            cmsApi.page_list(this.params.page, this.params.size, this.params).then(res => {
                // 获取数据
                this.total = res.queryResult.total
                this.tableData = res.queryResult.list
            })
        }
      },
      mounted() {
          this.query()
      }
    }
  </script>