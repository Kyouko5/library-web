package com.kyouko.libraryweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kyouko.libraryweb.dto.BookDto;

import java.util.Collection;
import java.util.List;

public interface BookService {
    /**
     * 新建图书
     * @param bookDto
     */
    void addBook(BookDto bookDto);

    /**
     * 更新图书
     * @param id
     * @param bookDto
     */
    void updateBook(Long id, BookDto bookDto);


    /**
     * 删除书籍
     *
     * @param id 书籍的ID
     * @return 无返回值
     */
    void deleteBook(Long id);


    /**
     * 批量删除书籍
     *
     * @param ids 要删除的书籍ID列表
     * @return 无返回值
     */
    void batchDelete(List<Long> ids);

    /**
     * 查询图书详情
     * @param id
     * @return
     */
    BookDto getBook(Long id);

    /**
     * 查询所有图书
     * @param pageNum
     * @param pageSize
     * @param bookName
     * @param isbn
     * @return
     */
    IPage<BookDto> getAllBooks(Integer pageNum, Integer pageSize, String bookName, String isbn);

    /**
     * 减少库存
     * @param bookId
     * @return
     */
    boolean decreaseStock(Long bookId);

    /**
     * 增加库存
     * @param bookId
     * @return
     */
    boolean increaseStock(Long bookId);

    /**
     * 根据ID集合查询图书
     * @param idList
     * @return
     */
    List<BookDto> findAllByIdIn(Collection<Long> idList);
}
