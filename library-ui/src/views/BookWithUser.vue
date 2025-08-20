<template>
  <div class="home">
    <!-- 搜索 -->
    <div class="search-container">
      <el-form inline size="small">
        <el-form-item label="图书编号">
          <el-input v-model="isbn" placeholder="请输入图书编号" clearable>
            <template #prefix><el-icon class="el-input__icon"><search/></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="search-btn" @click="load" size="small">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="danger" class="reset-btn" @click="handleClear" size="small">重置</el-button>
        </el-form-item>
      </el-form>
    </div>


    <!-- 数据表格 -->
    <el-table :data="tableData" stripe border>
      <el-table-column prop="isbn" label="图书编号" sortable></el-table-column>
      <el-table-column prop="title" label="图书名称"></el-table-column>
      <el-table-column prop="borrowDate" label="借阅时间"></el-table-column>
      <el-table-column prop="returnDate" label="归还时间"></el-table-column>
      <el-table-column prop="deadDate" label="最迟归还日期"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template v-slot="scope">
          <el-tag v-if="scope.row.status === '1'" type="warning">未归还</el-tag>
          <el-tag v-else type="success">已归还</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="renewTimes" label="可续借次数"></el-table-column>
      <el-table-column fixed="right" label="操作">
        <template #default="scope">
          <el-popconfirm title="确认续借(续借一次延长30天)?" @confirm="handleRenew(scope.row)" :disabled="scope.row.renewTimes === 0 || scope.row.status === '2'">
            <template #reference>
              <el-button type="danger" size="small" :disabled="scope.row.renewTimes === 0 || scope.row.status === '2'">续借</el-button>
            </template>
          </el-popconfirm>
          <el-popconfirm title="确认还书?" @confirm="handleReturn(scope.row.bookId)" :disabled="scope.row.status !== '1'">
            <template #reference>
              <el-button type="primary" size="small" :disabled="scope.row.status !== '1'">
                还书
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:currentPage="currentPage"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      >
      </el-pagination>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { ElButton, ElMessage, ElPopconfirm } from 'element-plus';
import { useUserStore } from '@/stores/userStore.js';
import { renewBooksAPI, returnBooksAPI } from '@/api/userBooks.js';
import { getUserBorrowRecordAPI } from '@/api/borrowRecord.js';

const isbn = ref('');
const username = ref('');
const total = ref(10);
const currentPage = ref(1);
const pageSize = ref(10);
const tableData = ref([]);

const user = useUserStore().userInfo;


const load = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    isbn: isbn.value,
    username: username.value,
    userId: user.id,
  };
  const res = await getUserBorrowRecordAPI(params);
  tableData.value = res.data.data.records;
  total.value = res.data.data.total;
};

// 处理还书
const handleReturn = async (bookId) => {
  // 处理还书逻辑
  const userId = user.id;
  try {
    const res = await returnBooksAPI({ bookId, userId });
    if (res.data.code === 200) {
      ElMessage.success('还书成功');
      await load();
    } else {
      ElMessage.error(res.data.message);
    }
  } catch (error) {
    ElMessage.error(error.response.data.message);
  }

};

const handleClear = () => {
  isbn.value = '';
  username.value = '';
  load();
};


const handleRenew = async (row) => {
  const data = {
    userId: row.userId,
    bookId: row.bookId
  }
  try {
    const response = await renewBooksAPI(data);
    if (response.data.code === 200) {
      ElMessage.success('续借成功');
      await load();
    } else {
      ElMessage.error(response.data.message);
    }
  } catch (error) {
    ElMessage.error('续借失败');
  }
};

const handleSizeChange = (pageSize) => {
  pageSize.value = pageSize;
  load();
};

const handleCurrentChange = (pageNum) => {
  currentPage.value = pageNum;
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
