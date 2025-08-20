import request from '@/utils/request.js';

export const dashboardAPI = () => {
  return request({
    url: '/api/dashboard/stats'
  });
};
