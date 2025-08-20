import request from '@/utils/request.js';

export const getBooksAPI = ({ pageNum, pageSize, title, isbn }) => {
  return request({
    url: '/api/books',
    params: {
      pageNum,
      pageSize,
      title,
      isbn,
    },
  });
};

export const deleteBookAPI = (id) => {
  return request.delete(`/api/books/${id}`)
}

export const batchDeleteBooksAPI = (ids) => {
  return request.post('/api/books/deleteBatch', ids)
}

export const updateBookAPI = (form) => {
  return request.put(`/api/books/${form.id}`, form)
}

export const saveBookAPI = (form) => {
  return request.post('/api/books', form)
}

