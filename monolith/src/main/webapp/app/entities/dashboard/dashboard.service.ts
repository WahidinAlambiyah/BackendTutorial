import axios from 'axios';

const baseApiUrl = 'api/dashboard-stats';

export default class DashboardService {
  public static getDashboardStats(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
