package com.lecture.lecture.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLectureRequest {
    private String title;
    private String description;
}
