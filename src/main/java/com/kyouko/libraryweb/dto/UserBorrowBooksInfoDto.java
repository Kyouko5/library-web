package com.kyouko.libraryweb.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserBorrowBooksInfoDto {
    /**
     * 借书数量
     */
    private int borrowNum;

    /**
     * 已逾期书籍
     */
    private List<UserBorrowBookDto> delayedBooks;
}
