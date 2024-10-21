<template>
  <canvas id="myChart"></canvas>
</template>

<script lang="ts">
import { defineComponent, watch, onMounted, ref } from 'vue';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

export default defineComponent({
  name: 'ChartComponent',
  props: {
    data: {
      type: Object,
      required: true,
    },
  },
  setup(props) {
    let chartInstance = ref<Chart | null>(null);

    const createChart = () => {
      const ctx = document.getElementById('myChart') as HTMLCanvasElement;

      if (ctx && chartInstance.value === null) {
        chartInstance.value = new Chart(ctx.getContext('2d')!, {
          type: 'bar', // Chart type
          data: props.data, // Ensure `props.data` is in the correct format
          options: {
            responsive: true,
            plugins: {
              legend: {
                position: 'top',
              },
              title: {
                display: true,
                text: 'Event Status Overview',
              },
            },
          },
        });
      }
    };

    watch(
      () => props.data,
      () => {
        if (chartInstance.value) {
          chartInstance.value.data = props.data;
          chartInstance.value.update();
        }
      }
    );

    onMounted(() => {
      createChart();
    });

    return {
      chartInstance,
    };
  },
});
</script>

<style scoped>
/* Add any chart styling if necessary */
canvas {
  max-width: 600px;
  margin: 0 auto;
}
</style>
