<template>
  <div class="home">
    <!-- 搜索 -->
    <div class="search-bar">
      <el-form inline size="small">
        <el-form-item label="图书编号">
          <el-input v-model="isbn" placeholder="请输入图书编号" clearable></el-input>
        </el-form-item>
        <el-form-item label="图书名称">
          <el-input v-model="title" placeholder="请输入图书名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="load" size="small">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button size="small" type="danger" @click="clear">重置</el-button>
        </el-form-item>
        <el-form-item style="float: right" v-if="numOfOutDateBook !== 0">
          <el-popconfirm
            confirm-button-text="查看"
            cancel-button-text="取消"
            icon="el-icon-info"
            icon-color="red"
            title="您有图书已逾期，请尽快归还"
            @confirm="toLook"
          >
            <template #reference>
              <el-button  type="warning">逾期通知</el-button>
            </template>
          </el-popconfirm>
        </el-form-item>
      </el-form>
    </div>
    <!-- 按钮 -->
    <div class="button-bar">
      <el-button type="primary" @click="add" v-if="isAdmin" size="small">上架</el-button>
      <el-popconfirm title="确认删除?" @confirm="deleteBatch" v-if="isAdmin">
        <template #reference>
          <el-button type="danger" size="small">批量删除</el-button>
        </template>
      </el-popconfirm>
    </div>

    <!-- 数据字段 -->
    <el-table :data="tableData" stripe border @selection-change="handleSelectionChange">
      <el-table-column v-if="isAdmin" type="selection" width="55"></el-table-column>
      <el-table-column prop="isbn" label="图书编号" sortable></el-table-column>
      <el-table-column prop="title" label="图书名称"></el-table-column>
      <el-table-column prop="author" label="作者"></el-table-column>
      <el-table-column prop="publisher" label="出版社"></el-table-column>
      <el-table-column prop="publishTime" label="出版时间" sortable></el-table-column>
      <el-table-column prop="stock" label="库存" sortable></el-table-column>
      <el-table-column prop="status" label="状态" v-if="isReader">
        <template #default="{ row }">
          <el-tag v-if="row.status === '1'" type="warning">已借阅</el-tag>
          <el-tag v-else type="success">未借阅</el-tag>
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)" v-if="isAdmin">修改</el-button>
          <el-popconfirm title="确认删除?" @confirm="handleDelete(row.id)" v-if="isAdmin">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
          <el-button size="small" @click="handleLend(row.id)" v-if="isReader"
                     :disabled="row.status === '1' || row.stock === 0">借阅
          </el-button>
          <el-popconfirm title="确认还书?" @confirm="handleReturn(row.id)"
                         v-if="isReader" :disabled="row.status !== '1'">
            <template #reference>
              <el-button type="danger" size="small" :disabled="row.status !== '1'">
                还书
              </el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 逾期详情对话框 -->
    <el-dialog v-model="outDateDialogVisible" v-if="numOfOutDateBook !== 0" title="逾期详情" width="50%">
      <el-table :data="outDateBook" style="width: 100%">
        <el-table-column prop="isbn" label="图书编号"></el-table-column>
        <el-table-column prop="title" label="书名"></el-table-column>
        <el-table-column prop="borrowDate" label="借阅日期"></el-table-column>
        <el-table-column prop="deadDate" label="截至日期"></el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="outDateDialogVisible = false">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 分页 -->
    <div class="pagination-bar">
      <el-pagination
        v-model:currentPage="currentPage"
        :page-sizes="[5, 10, 20]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      ></el-pagination>
    </div>
    <!-- 修改书籍信息对话框 -->
    <el-dialog v-model="editBookDialogVisible" :title="form.id ? '修改书籍信息': '上架书籍信息'" width="30%">
      <el-form :model="form" label-width="120px">
        <el-form-item label="图书编号">
          <el-input class="form-input" v-model="form.isbn"></el-input>
        </el-form-item>
        <el-form-item label="图书名称">
          <el-input class="form-input" v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="价格">
          <el-input class="form-input" v-model="form.price"></el-input>
        </el-form-item>
        <el-form-item label="作者">
          <el-input class="form-input" v-model="form.author"></el-input>
        </el-form-item>
        <el-form-item label="出版社">
          <el-input class="form-input" v-model="form.publisher"></el-input>
        </el-form-item>
        <el-form-item label="出版时间">
          <el-date-picker class="form-input" v-model="form.publishTime" type="date" value-format="YYYY-MM-DD"
                          clearable>
          </el-date-picker>
        </el-form-item>
        <el-form-item label="库存">
          <el-input class="form-input" v-model="form.stock"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editBookDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {
  ref,
  reactive,
  onMounted,
  computed,
} from 'vue';
import {
  ElButton,
  ElForm,
  ElFormItem,
  ElInput,
  ElTag,
  ElTable,
  ElTableColumn,
  ElPagination,
  ElDialog,
  ElPopconfirm,
  ElMessage,
} from 'element-plus';
import { useUserStore } from '@/stores/userStore.js';
import { getBooksAPI, deleteBookAPI, batchDeleteBooksAPI, updateBookAPI, saveBookAPI } from '@/api/book';
import { borrowBooksAPI, returnBooksAPI, getUserBooksAPI, getUserBorrowInfoAPI } from '@/api/userBooks';
// 用户信息
const userStore = useUserStore();
const user = ref({});
user.value = userStore.userInfo;
// 获取管理员用户角色
const isAdmin = computed(() => user.value.role === '1');
// 获取读者用户角色
const isReader = computed(() => user.value.role === '2');

// 组件的逻辑部分
const title = ref('');
const isbn = ref('');
const tableData = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const editBookDialogVisible = ref(false);
const outDateDialogVisible = ref(true);
const form = reactive({
  id: '',
  isbn: '',
  title: '',
  price: '',
  author: '',
  publisher: '',
  publishTime: '',
  stock: 0,
});
const selectedIds = ref([]);

// 模拟异步加载数据
const load = async () => {
  // 管理员读书列表
  if (user.value.role === '1') {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      title: title.value,
      isbn: isbn.value,
    };
    const res = await getBooksAPI(params);
    tableData.value = res.data.data.records;
    total.value = res.data.data.total;
  } else {
    // 读者读书列表
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      title: title.value,
      isbn: isbn.value,
      userId: user.value.id,
    };
    const res = await getUserBooksAPI(params);
    tableData.value = res.data.data.records;
    total.value = res.data.data.total;

    // 逾期书籍
    const userBorrowInfo = await getUserBorrowInfoAPI(user.value.id);
    borrowNum.value = userBorrowInfo.data.data.borrowNum;

    outDateBook.value = userBorrowInfo.data.data.delayedBooks ? userBorrowInfo.data.data.delayedBooks : [];
  }

};

// 模拟逾期数据
const borrowNum = ref(0);
const outDateBook = ref([]);
const numOfOutDateBook = computed(() => {
  return outDateBook.value.length;
});


// 清空搜索条件
const clear = () => {
  title.value = '';
  isbn.value = '';
  load();
};

// 处理分页大小改变事件
const handleSizeChange = (val) => {
  pageSize.value = val;
  load(); // 重新加载数据
};

// 处理当前页改变事件
const handleCurrentChange = (val) => {
  currentPage.value = val;
  load(); // 重新加载数据
};


// 删除选中的书籍
const handleDelete = async (id) => {
  const res = await deleteBookAPI(id);
  if (res.data.code === 200) {
    ElMessage.success('删除成功');
  } else {
    ElMessage.error('删除失败');
  }
  await load();
};

// 处理表格行选中事件
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(row => row.id);
};

// 批量删除书籍
const deleteBatch = async () => {
  // 处理批量删除逻辑
  if (!selectedIds.value.length) {
    ElMessage.warning('请选择数据！');
    return;
  }
  try {
    const response = await batchDeleteBooksAPI(selectedIds.value);
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


// 打开上架书籍对话框
const add = () => {
  editBookDialogVisible.value = true;
  Object.keys(form).forEach(key => {
    form[key] = '';
  });
};

// 处理保存上架书籍
const save = async () => {
  // 更新逻辑
  if (form.id) {
    try {
      const res = await updateBookAPI(form);
      if (res.data.code === 200) {
        ElMessage.success('更新成功');
        await load();
      } else {
        ElMessage.error(res.data.message);
      }
    } catch (error) {
      ElMessage.error(error.response.data.message);
    }
    editBookDialogVisible.value = false;
  } else {
    try {
      const res = await saveBookAPI(form);
      if (res.data.code === 200) {
        ElMessage.success('上架成功');
        await load();
      } else {
        ElMessage.error(res.data.message);
      }
    } catch (error) {
      ElMessage.error(error.response.data.message);
    }
    await load();
    editBookDialogVisible.value = false;
  }
};

// 处理修改书籍信息
const handleEdit = (row) => {
  // 设置表单数据
  form.id = row.id;
  form.isbn = row.isbn;
  form.title = row.title;
  form.price = row.price;
  form.author = row.author;
  form.stock = row.stock;
  form.publisher = row.publisher;
  form.publishTime = row.publishTime;
  editBookDialogVisible.value = true;
};

// 处理借阅书籍
const handleLend = async (bookId) => {

  if (borrowNum.value === 5) {
    ElMessage.warning('您不能再借阅更多的书籍了');
    return;
  }
  if (numOfOutDateBook.value > 0) {
    ElMessage.warning('在您归还逾期书籍前不能再借阅书籍');
    return;
  }

  // 处理借阅逻辑
  const userId = user.value.id;
  try {
    const res = await borrowBooksAPI({ bookId, userId });
    if (res.data.code === 200) {
      ElMessage.success('借阅成功');
      await load();
    } else {
      ElMessage.error(res.data.message);
    }
  } catch (error) {
    ElMessage.error(error.response.data.message);
  }
};

// 处理还书
const handleReturn = async (bookId) => {
  // 处理还书逻辑
  const userId = user.value.id;
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

// 处理查看逾期详情
const toLook = () => {
  // 处理查看逾期详情逻辑
  outDateDialogVisible.value = true;
};

// 组件加载时初始化数据
onMounted(() => {
  load(); // 初始化加载数据
});

</script>
<style scoped>
.home {
  width: 100%;
  padding: 10px;
}

.search-bar {
  margin: 10px 0;
}

.search-bar .el-form-item {
  margin-right: 10px;
}

.button-bar {
  margin: 10px 0;
}

.pagination-bar {
  margin: 10px 0;
}

.form-input {
  width: 80%;
}

.dialog-footer {
  text-align: right;
}
</style>
