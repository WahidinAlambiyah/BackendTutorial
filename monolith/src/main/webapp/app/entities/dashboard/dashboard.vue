<template>
  <div class="container">
    <h2>{{ t$('dashboard.title') }}</h2>

    <!-- Event Statistics -->
    <div class="row">
      <div class="col-md-6">
        <b-card class="text-center mb-4" title="Total Events" bg-variant="primary" text-variant="white">
          <p class="display-4">{{ stats.totalEvents }}</p>
        </b-card>
      </div>
      <div class="col-md-6">
        <b-card class="text-center mb-4" title="Total Participants" bg-variant="success" text-variant="white">
          <p class="display-4">{{ stats.totalParticipants }}</p>
        </b-card>
      </div>
    </div>

    <!-- Chart Section -->
    <div class="row mt-4">
      <div class="col-md-12">
        <b-card title="Events Overview">
          <chart-component v-if="!loading" :data="chartData"></chart-component>
          <div v-else class="text-center">Loading chart...</div>
        </b-card>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import ChartComponent from '@/entities/dashboard/chartComponent.vue'; // Assuming you have a ChartComponent.vue
import dashboardService from './dashboard.service'; // Import the dashboard service

export default defineComponent({
  name: 'Dashboard',
  components: {
    ChartComponent,
  },
  setup() {
    const { t: t$ } = useI18n();

    // Reactive variables for the statistics
    const stats = ref({
      totalEvents: 0,
      totalParticipants: 0,
      upcomingEvents: 0,
      ongoingEvents: 0,
      completedEvents: 0,
    });

    const loading = ref(true); // To show loading indicator while fetching data

    // Chart data for displaying event statistics
    const chartData = ref({
      labels: ['Ongoing', 'Completed', 'Upcoming'],
      datasets: [
        {
          label: 'Event Status',
          backgroundColor: ['#ff6384', '#36a2eb', '#cc65fe'],
          data: [0, 0, 0], // Default values before data is loaded
        },
      ],
    });

    // Fetch dashboard stats from backend via service
    const fetchDashboardStats = async () => {
      try {
        const response = await dashboardService.getStats(); // Fetch data using dashboard service
        const data = response.data;

        // Update statistics
        stats.value.totalEvents = data.totalEvents;
        stats.value.totalParticipants = data.totalParticipants;
        stats.value.upcomingEvents = data.upcomingEvents;
        stats.value.ongoingEvents = data.ongoingEvents;
        stats.value.completedEvents = data.completedEvents;

        // Update chart data
        chartData.value.datasets[0].data = [
          stats.value.ongoingEvents,
          stats.value.completedEvents,
          stats.value.upcomingEvents,
        ];

        loading.value = false; // Data loaded, hide the loading indicator
      } catch (error) {
        console.error('Error fetching dashboard stats:', error);
      }
    };

    // Fetch data when component is mounted
    onMounted(() => {
      fetchDashboardStats();
    });

    return {
      t$,
      stats,
      chartData,
      loading,
    };
  },
});
</script>

<style scoped>
.display-4 {
  font-size: 3.5rem;
  font-weight: bold;
}

h2 {
  margin-bottom: 2rem;
}

.text-center {
  text-align: center;
}
</style>
