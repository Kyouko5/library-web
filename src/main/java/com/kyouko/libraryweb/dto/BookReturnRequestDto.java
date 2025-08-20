package com.kyouko.libraryweb.dto;

import lombok.Data;

@Data
public class BookReturnRequestDto {
    private Long userId;
    private Long bookId;
}
