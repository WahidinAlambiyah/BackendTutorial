import axios from 'axios';

export default {
  getStats() {
    // Axios request to fetch the dashboard statistics from the backend
    return axios.get('/api/dashboard-stats');
  },
};
