<template>
  <div class="home">

    <!-- 搜索 -->
    <div style="margin: 10px 0;">
      <el-form inline="inline" size="small">
        <el-form-item label="图书编号">
          <el-input v-model="isbn" placeholder="请输入图书编号" clearable>
            <template #prefix>
              <el-icon class="el-input__icon">
                <search />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="读者用户名">
          <el-input v-model="username" placeholder="请输入读者编号" clearable>
            <template #prefix>
              <el-icon class="el-input__icon">
                <search />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="margin-left: 1%" @click="load" size="small">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button size="small" type="danger" @click="clear">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据字段 -->
    <el-table :data="tableData" stripe border>
      <el-table-column prop="isbn" label="图书编号" sortable />
      <el-table-column prop="title" label="图书名称" />
      <el-table-column prop="nickName" label="读者昵称" sortable />
      <el-table-column prop="username" label="读者用户名" sortable />
      <el-table-column prop="borrowDate" label="借阅时间" sortable />
      <el-table-column prop="returnDate" label="归还时间" sortable />
      <el-table-column prop="status" label="状态">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === '1'" type="warning">未归还</el-tag>
          <el-tag v-else type="success">已归还</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin: 10px 0">
      <el-pagination
        v-model:currentPage="currentPage"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { getLibraryBorrowRecordAPI } from '@/api/borrowRecord.js';

const isbn = ref('');
const username = ref('');
const total = ref(10);
const currentPage = ref(1);
const pageSize = ref(10);
const tableData = ref([]);
const forms = ref([]);


const load = async () => {

  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    isbn: isbn.value,
    username: username.value,
  };
  const res = await getLibraryBorrowRecordAPI(params);
  tableData.value = res.data.data.records;
  total.value = res.data.data.total;
};

const clear = () => {
  isbn.value = '';
  title.value = '';
  search3.value = '';
  load();
};

const handleSizeChange = (newPageSize) => {
  pageSize.value = newPageSize;
  load();
};

const handleCurrentChange = (newPageNum) => {
  currentPage.value = newPageNum;
  load();
};

onMounted(() => {
  load();
});
</script>

<style scoped>
.home {
  padding: 10px;
  width: 100%;
}

</style>
