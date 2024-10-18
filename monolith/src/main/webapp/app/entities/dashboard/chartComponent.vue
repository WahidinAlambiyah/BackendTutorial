<template>
    <div>
      <canvas ref="chartCanvas"></canvas>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, onMounted, ref, watch } from 'vue';
  import Chart from 'chart.js/auto'; // Import Chart.js
  
  export default defineComponent({
    name: 'ChartComponent',
    props: {
      data: {
        type: Object,
        required: true, // The chart data will be passed as a prop
      },
      options: {
        type: Object,
        default: () => ({}), // Optional chart options
      },
    },
    setup(props) {
      const chartCanvas = ref<HTMLCanvasElement | null>(null);
      let chartInstance: Chart | null = null;
  
      // Function to initialize the chart
      const initChart = () => {
        if (chartCanvas.value && props.data) {
          if (chartInstance) {
            chartInstance.destroy(); // Destroy the previous instance before re-rendering
          }
  
          chartInstance = new Chart(chartCanvas.value, {
            type: 'pie', // Chart type can be 'bar', 'line', 'pie', etc.
            data: props.data, // Chart data passed as a prop
            options: props.options, // Optional chart options passed as a prop
          });
        }
      };
  
      // Watch for changes in the data and re-initialize the chart when it changes
      watch(
        () => props.data,
        (newData) => {
          if (newData) {
            initChart(); // Re-initialize chart on data change
          }
        },
        { immediate: true }
      );
  
      // Initialize chart when the component is mounted
      onMounted(() => {
        initChart();
      });
  
      return {
        chartCanvas,
      };
    },
  });
  </script>
  
  <style scoped>
  canvas {
    max-width: 100%;
  }
  </style>
  