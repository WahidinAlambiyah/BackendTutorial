<template>
    <div class="container">
      <h2>{{ t$('dashboard.title') }}</h2>
  
      <div class="row">
        <div class="col-md-6">
          <b-card class="text-center mb-4" title="Total Events" bg-variant="primary" text-variant="white">
            <p class="display-4">{{ totalEvents }}</p>
          </b-card>
        </div>
        <div class="col-md-6">
          <b-card class="text-center mb-4" title="Ongoing Events" bg-variant="info" text-variant="white">
            <p class="display-4">{{ ongoingEvents }}</p>
          </b-card>
        </div>
      </div>
  
      <div class="row">
        <div class="col-md-6">
          <b-card class="text-center mb-4" title="Total Participants" bg-variant="success" text-variant="white">
            <p class="display-4">{{ totalParticipants }}</p>
          </b-card>
        </div>
        <div class="col-md-6">
          <b-card class="text-center mb-4" title="Completed Events" bg-variant="danger" text-variant="white">
            <p class="display-4">{{ completedEvents }}</p>
          </b-card>
        </div>
      </div>
  
      <!-- Only show the chart if data is loaded -->
      <div class="row mt-4">
        <div class="col-md-12">
          <b-card title="Events Overview">
            <chart-component v-if="!loading" :data="chartData"></chart-component>
            <!-- Display a loading message while data is being fetched -->
            <div v-else class="text-center">Loading chart...</div>
          </b-card>
        </div>
      </div>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue';
  import { useI18n } from 'vue-i18n';
  import ChartComponent from '@/entities/dashboard/chartComponent.vue'; // Import ChartComponent
  import TrxEventService from '@/entities/trx-event/trx-event.service'; // Import your Event Service
  
  export default defineComponent({
    name: 'Dashboard',
    components: {
      ChartComponent, // Register ChartComponent
    },
    setup() {
      const { t: t$ } = useI18n();
      const trxEventService = new TrxEventService();
  
      // Reactive variables for statistics
      const totalEvents = ref(0);
      const ongoingEvents = ref(0);
      const completedEvents = ref(0);
      const totalParticipants = ref(0);
      const loading = ref(true); // Add a loading state
  
      // Chart data for events overview
      const chartData = ref({
        labels: ['Ongoing', 'Completed', 'Upcoming'],
        datasets: [
          {
            label: 'Event Status',
            backgroundColor: ['#ff6384', '#36a2eb', '#cc65fe'],
            data: [0, 0, 0],
          },
        ],
      });
  
      const loadDashboardData = async () => {
        try {
          // Fetch total events
          const eventsResponse = await trxEventService.retrieve();
          const events = eventsResponse.data;
          totalEvents.value = events.length;
  
          // Calculate ongoing, completed, and upcoming events
          ongoingEvents.value = events.filter(event => event.status === 'ONGOING').length;
          completedEvents.value = events.filter(event => event.status === 'COMPLETED').length;
          chartData.value.datasets[0].data = [
            ongoingEvents.value,
            completedEvents.value,
            events.filter(event => event.status === 'UPCOMING').length,
          ];
  
          // Calculate total participants
          totalParticipants.value = events.reduce((acc, event) => acc + event.capacity, 0);
  
          // Data has been loaded, disable the loading state
          loading.value = false;
        } catch (error) {
          console.error('Error loading dashboard data:', error);
        }
      };
  
      onMounted(() => {
        loadDashboardData();
      });
  
      return {
        t$,
        totalEvents,
        ongoingEvents,
        completedEvents,
        totalParticipants,
        chartData,
        loading, // Return loading state to template
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
  