import request from '@/utils/request.js';

export const getUserBorrowRecordAPI = (params) => {
  return request({
    url: '/api/borrow/user/record',
    params
  })
}


export const getLibraryBorrowRecordAPI = (params) => {
  return request({
    url: '/api/borrow/record',
    params
  })
}
