package com.lecture.user.domain;

import com.lecture.authorization.common.ResourceOwnership;
import lombok.Getter;

@Getter
public class User implements ResourceOwnership {
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
    
    @Override
    public Long getOwnershipId() {
        return id;  // 사용자 ID가 소유권 ID
    }
}
