<template>
  <div>
    <h2>{{ t('dashboard.title') }}</h2>
    <div class="dashboard-stats">
      <div class="stat-item">
        <h3>{{ dashboardStats.totalEvents }}</h3>
        <p>{{ t('dashboard.totalEvents') }}</p>
      </div>
      <div class="stat-item">
        <h3>{{ dashboardStats.totalParticipants }}</h3>
        <p>{{ t('dashboard.totalParticipants') }}</p>
      </div>
    </div>

    <!-- Chart Section -->
    <chart-component v-if="chartData" :data="chartData"></chart-component>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import DashboardService from './dashboard.service';
import ChartComponent from './chartComponent.vue';

export default defineComponent({
  name: 'Dashboard',
  components: {
    ChartComponent,
  },
  setup() {
    const { t } = useI18n();
    const dashboardStats = ref({
      totalEvents: 0,
      totalParticipants: 0,
      upcomingEvents: 0,
      ongoingEvents: 0,
      completedEvents: 0,
    });

    const chartData = ref(null);

    const fetchDashboardStats = async () => {
  try {
    const response = await DashboardService.getDashboardStats();
    dashboardStats.value = response.data;

    // Prepare chart data
    chartData.value = {
      labels: [
        t('dashboard.status.ongoing'), 
        t('dashboard.status.completed'), 
        t('dashboard.status.upcoming')
      ],
      datasets: [
        {
          label: t('dashboard.eventStatus'),
          backgroundColor: ['#ff6384', '#36a2eb', '#cc65fe'],
          data: [
            dashboardStats.value.ongoingEvents,
            dashboardStats.value.completedEvents,
            dashboardStats.value.upcomingEvents,
          ],
        },
      ],
    };

    console.log('CHART DATA',chartData.value);
  } catch (error) {
    console.error('Error fetching dashboard stats:', error);
  }
};


    onMounted(() => {
      fetchDashboardStats();
    });

    return {
      t,
      dashboardStats,
      chartData,
    };
  },
});
</script>

<style scoped>
/* Styling for the dashboard */
.dashboard-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 10px;
}
</style>
