package com.lecture.group.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    private String name;
    private String description;
}
