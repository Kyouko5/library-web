<template>
  <div class="home">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in cards" :key="item.title">
        <el-card class="box-card">
          <div slot="header" class="clearfix">{{ item.title }}</div>
          <div class="text item">
            <svg class="icon" aria-hidden="true">
              <use :xlink:href="item.icon" style="width: 100px"></use>
            </svg>
            <span class="text">{{ item.data }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <!-- 为 ECharts 准备一个具备大小（宽高）的 DOM -->
    <div id="mainChart" ref="mainChart" style="width: 100%; height: 450px; margin-left: 5px"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from "element-plus"
import { dashboardAPI } from '@/api/dashboard.js';

const cards = ref([
  { title: '已借阅', data: 100, icon: '#iconlend-record-pro' },
  { title: '图书数', data: 100, icon: '#iconbook-pro' },
  { title: '用户数', data: 1000, icon: '#iconpopulation' }
])

const mainChart = ref(null)

onMounted(async () => {
  try {
    const res = await dashboardAPI();
    if (res.data.code === 200) {
      cards.value[0].data = res.data.data.lendRecordCount
      cards.value[1].data = res.data.data.bookCount
      cards.value[2].data = res.data.data.userCount
      const dailyLendRecords = res.data.data.dailyLendRecords;
      const dates = dailyLendRecords.map(record => record.date)
      const counts = dailyLendRecords.map(record => record.count)

      const myChart = echarts.init(mainChart.value)
      myChart.setOption({
        title: { text: '一周借阅统计' },
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: dates, axisTick: { alignWithLabel: true } },
        yAxis: { type: 'value' },
        series: [
          {
            type: 'line', label: { show: true }, data: counts, itemStyle: { color: '#5470c6' }
          }
        ]
      })

      window.addEventListener('resize', () => {
        myChart.resize()
      })
    } else {
      ElMessage.error(res.data.msg)
    }
  } catch (err) {
    ElMessage.error('请求失败，请检查网络连接')
  }
})
</script>

<style scoped>
.home {
  width: 100%;
}

.box-card {
  width: 80%;
  margin-bottom: 25px;
  margin-left: 10px;
}

.clearfix {
  text-align: center;
  font-size: 15px;
}

.text {
  text-align: center;
  font-size: 24px;
  font-weight: 700;
  vertical-align: super;
}

#mainChart {
  width: 100%;
  height: 450px;
  margin-top: 20px;
}

.icon {
  width: 50px;
  height: 50px;
  padding-top: 5px;
  padding-right: 10px;
}
</style>
