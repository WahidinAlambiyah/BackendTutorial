<template>
    <div class="container">
      <h2>{{ t$('dashboard.title') }}</h2>
  
      <!-- Event Status Filter -->
      <div class="row mb-3">
        <div class="col-md-6">
          <label for="eventStatusFilter">{{ t$('dashboard.filter.status') }}</label>
          <select v-model="selectedStatus" @change="filterEvents" class="form-control" id="eventStatusFilter">
            <option value="">{{ t$('dashboard.filter.allStatus') }}</option>
            <option value="UPCOMING">{{ t$('dashboard.status.upcoming') }}</option>
            <option value="ONGOING">{{ t$('dashboard.status.ongoing') }}</option>
            <option value="COMPLETED">{{ t$('dashboard.status.completed') }}</option>
          </select>
        </div>
  
        <!-- Sorting Options -->
        <div class="col-md-6">
          <label for="sortBy">{{ t$('dashboard.sort.sortBy') }}</label>
          <select v-model="selectedSort" @change="sortEvents" class="form-control" id="sortBy">
            <option value="date">{{ t$('dashboard.sort.date') }}</option>
            <option value="capacity">{{ t$('dashboard.sort.capacity') }}</option>
          </select>
        </div>
      </div>
  
      <div class="row">
        <!-- Event Statistics (Total Events, Participants, etc.) -->
        <div class="col-md-6">
          <b-card class="text-center mb-4" title="Total Events" bg-variant="primary" text-variant="white">
            <p class="display-4">{{ filteredEvents.length }}</p>
          </b-card>
        </div>
        <div class="col-md-6">
          <b-card class="text-center mb-4" title="Total Participants" bg-variant="success" text-variant="white">
            <p class="display-4">{{ totalParticipants }}</p>
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
  import { defineComponent, ref, onMounted, watch } from 'vue';
  import { useI18n } from 'vue-i18n';
  import ChartComponent from '@/entities/dashboard/ChartComponent.vue'; // Import ChartComponent
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
      const events = ref([]);
      const filteredEvents = ref([]);
      const totalParticipants = ref(0);
      const loading = ref(true); // Add a loading state
  
      // Variables for filtering and sorting
      const selectedStatus = ref(''); // For event status filter
      const selectedSort = ref('date'); // Default sort by date
  
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
  
      // Load and fetch events from the backend
      const loadEvents = async () => {
        try {
          const eventsResponse = await trxEventService.retrieve();
          events.value = eventsResponse.data;
          filteredEvents.value = events.value; // Initialize with all events
          calculateParticipants();
          updateChart();
          loading.value = false;
        } catch (error) {
          console.error('Error loading dashboard data:', error);
        }
      };
  
      // Calculate total participants
      const calculateParticipants = () => {
        totalParticipants.value = filteredEvents.value.reduce((acc, event) => acc + event.capacity, 0);
      };
  
      // Filter events by selected status
      const filterEvents = () => {
        if (!selectedStatus.value) {
          filteredEvents.value = events.value;
        } else {
          filteredEvents.value = events.value.filter(event => event.status === selectedStatus.value);
        }
        calculateParticipants(); // Recalculate total participants
        updateChart(); // Update chart after filtering
      };
  
      // Sort events by the selected option
      const sortEvents = () => {
        if (selectedSort.value === 'date') {
          filteredEvents.value.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
        } else if (selectedSort.value === 'capacity') {
          filteredEvents.value.sort((a, b) => b.capacity - a.capacity); // Sort by capacity (descending)
        }
      };
  
      // Update the chart data based on the filtered events
      const updateChart = () => {
        const ongoing = filteredEvents.value.filter(event => event.status === 'ONGOING').length;
        const completed = filteredEvents.value.filter(event => event.status === 'COMPLETED').length;
        const upcoming = filteredEvents.value.filter(event => event.status === 'UPCOMING').length;
        chartData.value.datasets[0].data = [ongoing, completed, upcoming];
      };
  
      onMounted(() => {
        loadEvents(); // Load events when the component is mounted
      });
  
      watch([selectedSort, selectedStatus], () => {
        filterEvents(); // Apply filtering
        sortEvents(); // Apply sorting
      });
  
      return {
        t$,
        filteredEvents,
        totalParticipants,
        selectedStatus,
        selectedSort,
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
  