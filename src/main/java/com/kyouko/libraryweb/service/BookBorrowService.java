package com.kyouko.libraryweb.service;

import com.kyouko.libraryweb.domain.BorrowRecord;
import com.kyouko.libraryweb.dto.BookBorrowRequestDto;
import com.kyouko.libraryweb.dto.BookReturnRequestDto;
import com.kyouko.libraryweb.dto.UserBorrowBooksInfoDto;

import java.util.List;

public interface BookBorrowService {
    /**
     * 借书
     * @param bookBorrowRequestDto
     * @return
     */
    boolean borrowBook(BookBorrowRequestDto bookBorrowRequestDto);

    /**
     * 还书
     * @param bookReturnRequestDto
     * @return
     */
    boolean returnBook(BookReturnRequestDto bookReturnRequestDto);

    /**
     * 续借图书
     * @param bookBorrowRequestDto
     * @return
     */
    boolean renewBook(BookBorrowRequestDto bookBorrowRequestDto);

    /**
     * 查询借阅记录
     * @param bookIds
     * @param userId
     * @return
     */
    List<BorrowRecord> findBorrowBooks(List<Long> bookIds, Long userId);

    /**
     * 查询用户借阅信息
     * @param userId
     * @return
     */
    UserBorrowBooksInfoDto getUserBorrowBooksInfo(Long userId);


}
