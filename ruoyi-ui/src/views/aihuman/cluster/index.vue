<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="集群名称" prop="clusterName">
        <el-input
          v-model="queryParams.clusterName"
          placeholder="请输入集群名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="IP地址" prop="ipAddress">
        <el-input
          v-model="queryParams.ipAddress"
          placeholder="请输入IP地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="端口号" prop="port">
        <el-input
          v-model="queryParams.port"
          placeholder="请输入端口号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="GPU数量" prop="gpuCount">
        <el-input
          v-model="queryParams.gpuCount"
          placeholder="请输入GPU数量"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="GPU显存(GB)" prop="gpuMemory">
        <el-input
          v-model="queryParams.gpuMemory"
          placeholder="请输入GPU显存(GB)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否启用" prop="enabled">
        <el-input
          v-model="queryParams.enabled"
          placeholder="请输入是否启用"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="CPU核心数" prop="cpuCores">
        <el-input
          v-model="queryParams.cpuCores"
          placeholder="请输入CPU核心数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="内存大小(GB)" prop="memorySize">
        <el-input
          v-model="queryParams.memorySize"
          placeholder="请输入内存大小(GB)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="磁盘空间(GB)" prop="diskSpace">
        <el-input
          v-model="queryParams.diskSpace"
          placeholder="请输入磁盘空间(GB)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="GPU负载率" prop="loadAverage">
        <el-input
          v-model="queryParams.loadAverage"
          placeholder="请输入GPU负载率"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="GPU温度(℃)" prop="temperature">
        <el-input
          v-model="queryParams.temperature"
          placeholder="请输入GPU温度(℃)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最后心跳时间" prop="lastHeartbeat">
        <el-date-picker clearable
          v-model="queryParams.lastHeartbeat"
          type="date"
          value-format="yyyy-MM-dd HH:mm:ss"
          placeholder="请选择最后心跳时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['aihuman:cluster:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['aihuman:cluster:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['aihuman:cluster:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['aihuman:cluster:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="clusterList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="集群ID" align="center" prop="clusterId" />
      <el-table-column label="集群名称" align="center" prop="clusterName" />
      <el-table-column label="IP地址" align="center" prop="ipAddress" />
      <el-table-column label="端口号" align="center" prop="port" />
      <el-table-column label="GPU数量" align="center" prop="gpuCount" />
      <el-table-column label="GPU型号" align="center" prop="gpuType" />
      <el-table-column label="GPU显存(GB)" align="center" prop="gpuMemory" />
      <el-table-column label="集群状态" align="center" prop="status" />
      <el-table-column label="是否启用" align="center" prop="enabled" />
      <el-table-column label="CPU核心数" align="center" prop="cpuCores" />
      <el-table-column label="内存大小(GB)" align="center" prop="memorySize" />
      <el-table-column label="磁盘空间(GB)" align="center" prop="diskSpace" />
      <el-table-column label="GPU负载率" align="center" prop="loadAverage" />
      <el-table-column label="GPU温度(℃)" align="center" prop="temperature" />
      <el-table-column label="最后心跳时间" align="center" prop="lastHeartbeat" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastHeartbeat, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['aihuman:cluster:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['aihuman:cluster:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改GPU集群管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="集群名称" prop="clusterName">
          <el-input v-model="form.clusterName" placeholder="请输入集群名称" />
        </el-form-item>
        <el-form-item label="IP地址" prop="ipAddress">
          <el-input v-model="form.ipAddress" placeholder="请输入IP地址" />
        </el-form-item>
        <el-form-item label="端口号" prop="port">
          <el-input v-model="form.port" placeholder="请输入端口号" />
        </el-form-item>
        <el-form-item label="GPU数量" prop="gpuCount">
          <el-input v-model="form.gpuCount" placeholder="请输入GPU数量" />
        </el-form-item>
        <el-form-item label="GPU显存(GB)" prop="gpuMemory">
          <el-input v-model="form.gpuMemory" placeholder="请输入GPU显存(GB)" />
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-input v-model="form.enabled" placeholder="请输入是否启用" />
        </el-form-item>
        <el-form-item label="CPU核心数" prop="cpuCores">
          <el-input v-model="form.cpuCores" placeholder="请输入CPU核心数" />
        </el-form-item>
        <el-form-item label="内存大小(GB)" prop="memorySize">
          <el-input v-model="form.memorySize" placeholder="请输入内存大小(GB)" />
        </el-form-item>
        <el-form-item label="磁盘空间(GB)" prop="diskSpace">
          <el-input v-model="form.diskSpace" placeholder="请输入磁盘空间(GB)" />
        </el-form-item>
        <el-form-item label="GPU负载率" prop="loadAverage">
          <el-input v-model="form.loadAverage" placeholder="请输入GPU负载率" />
        </el-form-item>
        <el-form-item label="GPU温度(℃)" prop="temperature">
          <el-input v-model="form.temperature" placeholder="请输入GPU温度(℃)" />
        </el-form-item>
        <el-form-item label="最后心跳时间" prop="lastHeartbeat">
          <el-date-picker clearable
            v-model="form.lastHeartbeat"
            type="date"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择最后心跳时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCluster, getCluster, delCluster, addCluster, updateCluster } from "@/api/aihuman/cluster";

export default {
  name: "Cluster",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // GPU集群管理表格数据
      clusterList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        clusterName: null,
        ipAddress: null,
        port: null,
        gpuCount: null,
        gpuType: null,
        gpuMemory: null,
        status: null,
        enabled: null,
        cpuCores: null,
        memorySize: null,
        diskSpace: null,
        loadAverage: null,
        temperature: null,
        lastHeartbeat: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        clusterName: [
          { required: true, message: "集群名称不能为空", trigger: "blur" }
        ],
        ipAddress: [
          { required: true, message: "IP地址不能为空", trigger: "blur" }
        ],
        gpuCount: [
          { required: true, message: "GPU数量不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询GPU集群管理列表 */
    getList() {
      this.loading = true;
      listCluster(this.queryParams).then(response => {
        this.clusterList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        clusterId: null,
        clusterName: null,
        ipAddress: null,
        port: null,
        gpuCount: null,
        gpuType: null,
        gpuMemory: null,
        status: null,
        enabled: null,
        cpuCores: null,
        memorySize: null,
        diskSpace: null,
        loadAverage: null,
        temperature: null,
        lastHeartbeat: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.clusterId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加GPU集群管理";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const clusterId = row.clusterId || this.ids
      getCluster(clusterId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改GPU集群管理";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.clusterId != null) {
            updateCluster(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCluster(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const clusterIds = row.clusterId || this.ids;
      this.$modal.confirm('是否确认删除GPU集群管理编号为"' + clusterIds + '"的数据项？').then(function() {
        return delCluster(clusterIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('aihuman/cluster/export', {
        ...this.queryParams
      }, `cluster_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
