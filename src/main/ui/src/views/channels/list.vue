<template>
  <div class="app-container case-list-container">
    <el-card class="box-card">
      <div class="case-info">
        <el-tag>连接数量：{{ caseInfo.services }}</el-tag>

        <span style="float: right;padding: 4px 10px">
           <el-input
             v-model="search"
             size="mini"
             style="width: 220px"
             placeholder="输入业务唯一标识搜索"
           />
           <el-button class="filter-item" size="mini" type="primary" icon="el-icon-search"
                      @click="getList">搜索</el-button>
        </span>
      </div>
      <!--表格渲染-->
      <el-table
        v-loading="listLoading"
        :data="tableData.filter(data => !search || data.channelId.toLowerCase().includes(search.toLowerCase()))"
        border
        size="small"
        highlight-current-row
        style="width: 100%;"
      >
        <el-table-column :show-overflow-tooltip="true" prop="channelId" label="channelId"/>
        <el-table-column :show-overflow-tooltip="true" prop="remoteAddress" label="remoteAddress"/>
        <el-table-column :show-overflow-tooltip="true" prop="serverAddress" label="serverAddress"/>
        <el-table-column :show-overflow-tooltip="true" prop="fileName" label="fileName"/>
        <el-table-column :show-overflow-tooltip="true" prop="state" label="state">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.state === 0 ? 'success' : 'primary'"
              disable-transitions>{{ scope.row.state === 0 ? '正在传输数据' : '连接已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :show-overflow-tooltip="true" prop="data" label="data"/>
        <el-table-column :show-overflow-tooltip="true" label="createDate" prop="createDate"  :formatter="dateFormat"/>
        <el-table-column :show-overflow-tooltip="true" label="modifyDate" prop="modifyDate"  :formatter="dateFormat"/>

      </el-table>
    </el-card>
  </div>
</template>

<script>
import {getChannels, fetchPv, createArticle, updateArticle} from '@/api/list'
import moment from 'moment'

// arr to obj, such as { CN : "China", US : "USA" }
export default {
  name: 'Channels',
  data() {
    return {
      caseInfo: {
        services: 1,
        instances: 1
      },
      search: '',
      tableData: [],
      listLoading: true,
      searchForm: {
        date: null
        // curPage: 1,
        // pageSize: 30
      },
      // totalCount: 1,
      dialogVisible: false,
      drawerVisible: false,
      sort: 'id_desc',
      pickerOptions: {
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            picker.$emit('pick', new Date())
          }
        }, {
          text: '昨天',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24)
            picker.$emit('pick', date)
          }
        }, {
          text: '一周前',
          onClick(picker) {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', date)
          }
        }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    dateFormat (column) {
      if (column == undefined) {
        return "";
      }
      return moment(column).format("YYYY-MM-DD HH:mm:ss");
    },
    getList() {
      this.listLoading = true
      getChannels().then(response => {
        this.tableData = response.data
        this.total = this.tableData.length
        this.caseInfo.services = this.tableData.length
        this.caseInfo.instances = this.tableData.length
        console.log(response)

      }).finally(() => {
        this.listLoading = false
      })

    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleModifyStatus(row, status) {
      this.$message({
        message: '操作Success',
        type: 'success'
      })
      row.status = status
    },
    sortChange(data) {
      const {prop, order} = data
      if (prop === 'id') {
        this.sortByID(order)
      }
    },
    sortByID(order) {
      if (order === 'ascending') {
        this.listQuery.sort = '+id'
      } else {
        this.listQuery.sort = '-id'
      }
      this.handleFilter()
    },
    resetTemp() {
      this.temp = {
        id: undefined,
        importance: 1,
        remark: '',
        timestamp: new Date(),
        title: '',
        status: 'published',
        type: ''
      }
    },
    handleCreate() {
      this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.temp.id = parseInt(Math.random() * 100) + 1024 // mock a id
          this.temp.author = 'vue-element-admin'
          createArticle(this.temp).then(() => {
            this.list.unshift(this.temp)
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: 'Created Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      this.temp.timestamp = new Date(this.temp.timestamp)
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          tempData.timestamp = +new Date(tempData.timestamp) // change Thu Nov 30 2017 16:41:05 GMT+0800 (CST) to 1512031311464
          updateArticle(tempData).then(() => {
            const index = this.list.findIndex(v => v.id === this.temp.id)
            this.list.splice(index, 1, this.temp)
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: 'Update Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDelete(row, index) {
      this.$notify({
        title: 'Success',
        message: 'Delete Successfully',
        type: 'success',
        duration: 2000
      })
      this.list.splice(index, 1)
    },
    handleFetchPv(pv) {
      fetchPv(pv).then(response => {
        this.pvData = response.data.pvData
        this.dialogPvVisible = true
      })
    },
    handleDownload() {
      this.downloadLoading = true
      import('@/vendor/Export2Excel').then(excel => {
        const tHeader = ['timestamp', 'title', 'type', 'importance', 'status']
        const filterVal = ['timestamp', 'title', 'type', 'importance', 'status']
        const data = this.formatJson(filterVal)
        excel.export_json_to_excel({
          header: tHeader,
          data,
          filename: 'table-list'
        })
        this.downloadLoading = false
      })
    },
    goCmd(row) {
      console.log(row)
      this.$router.push({name: 'WebShell', query: {id: row.appId}})
    },
    openUrl(url) {
      if (url != '') {
        window.open(url, '_blank');
      } else {
        this.$message({
          showClose: true,
          message: '视频正在处理中，请稍后。。。',
          type: 'success'
        });
      }
    },
    getSortClass: function (key) {
      const sort = this.listQuery.sort
      return sort === `+${key}` ? 'ascending' : 'descending'
    }
  }
}
</script>
