package com.kyouko.libraryweb.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
    private String role;
    private String email;
    private String phone;
    private String address;
    private String nickName;
}
