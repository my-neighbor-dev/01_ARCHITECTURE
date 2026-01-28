package com.lecture.user.domain;

import lombok.Getter;

@Getter
public class User {
    private Long id;
    private String email;
    private String name;
    private String password;
    private String phoneNumber;
    
    public User(Long id, String email, String name, String password, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }
}
