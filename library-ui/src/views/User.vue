<template>
  <div class="home">
    <!-- 搜索 -->
    <div>
      <el-form inline size="small">
        <el-form-item label="昵称">
          <el-input v-model="searchName" placeholder="请输入姓名" clearable>
            <template #prefix>
              <el-icon class="el-input__icon">
                <search />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="电话号码">
          <el-input v-model="searchPhone" placeholder="请输入电话号码" clearable>
            <template #prefix>
              <el-icon class="el-input__icon">
                <search />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="searchEmail" placeholder="请输入地址" clearable>
            <template #prefix>
              <el-icon class="el-input__icon">
                <search />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="margin-left: 1%" @click="loadReaders" size="small">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button size="small" type="danger" @click="clearSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 按钮 -->
    <div style="margin: 10px 0;">
      <el-popconfirm title="确认删除?" @confirm="deleteSelectedReaders">
        <template #reference>
          <el-button type="danger" size="small">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>
    <!-- 数据字段 -->
    <el-table :data="readers" stripe border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="nickName" label="昵称" />
      <el-table-column prop="phone" label="电话号码" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="address" label="地址" />
      <el-table-column fixed="right" label="操作">
        <template v-slot="scope">
          <el-button size="small" @click="editReader(scope.row)">编辑</el-button>
          <el-popconfirm title="确认删除?" @confirm="deleteReader(scope.row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <div>
      <el-pagination
        v-model:currentPage="currentPage"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalReaders"
        @size-change="handlePageSizeChange"
        @current-change="handleCurrentPageChange"
      ></el-pagination>

      <el-dialog v-model="dialogVisible" title="编辑读者信息" width="30%">
        <el-form :model="form" label-width="120px">
          <el-form-item label="用户名">
            <el-input style="width: 80%" v-model="form.username"></el-input>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input style="width: 80%" v-model="form.nickName"></el-input>
          </el-form-item>
          <el-form-item label="电话号码">
            <el-input style="width: 80%" v-model="form.phone"></el-input>
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input style="width: 80%" v-model="form.email"></el-input>
          </el-form-item>
          <el-form-item label="地址">
            <el-input type="textarea" style="width: 80%" v-model="form.address"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveReader">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import { batchDeleteUsersAPI, deleteUserAPI, getUsersAPI, updateUserAPI } from '@/api/user.js';

const searchName = ref('');
const searchPhone = ref('');
const searchEmail = ref('');

const form = reactive({});
const dialogVisible = ref(false);

const totalReaders = ref(10);
const currentPage = ref(1);
const pageSize = ref(10);
const readers = ref([]);

const user = reactive({});
const selectedReaderIds = ref([]);

const handleSelectionChange = (val) => {
  selectedReaderIds.value = val.map((v) => v.id);
};

const deleteSelectedReaders = async () => {
  if (!selectedReaderIds.value.length) {
    ElMessage.warning('请选择数据！');
    return;
  }
  try {
    const response = await batchDeleteUsersAPI(selectedIds.value);
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      await load();
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    ElMessage.error('批量删除失败');
  }
};

// 前后端接口对接实例
const loadReaders = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    name: searchName.value,
    phone: searchPhone.value,
    email: searchEmail.value,
  };
  const res = await getUsersAPI(params);
  readers.value = res.data.data.records;
  totalReaders.value = res.data.data.total;
};

const clearSearch = () => {
  searchName.value = '';
  searchPhone.value = '';
  searchEmail.value = '';
  loadReaders();
};

const deleteReader = async (id) => {
  const res = await deleteUserAPI(id);
  if (res.data.code === 200) {
    ElMessage.success('删除成功');
  } else {
    ElMessage.error('删除失败');
  }
  await loadReaders();

};

const saveReader = async () => {
  try {
    const res = await updateUserAPI(form);
    if (res.data.code === 200) {
      ElMessage.success('更新用户信息成功');
      // 刷新用户信息
      await loadReaders();
      dialogVisible.value = false
    } else {
      ElMessage.error(res.data.message);
    }
  } catch (error) {
    ElMessage.error(error.response.data.message);
  }
};


const editReader = (row) => {
  Object.assign(form, row);
  dialogVisible.value = true;
};

const handlePageSizeChange = (newSize) => {
  pageSize.value = newSize;
  loadReaders();
};

const handleCurrentPageChange = (newPage) => {
  currentPage.value = newPage;
  loadReaders();
};

onMounted(() => {
  loadReaders();
});
</script>

<style scoped>
.home {
  width: 100%;
  padding: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
