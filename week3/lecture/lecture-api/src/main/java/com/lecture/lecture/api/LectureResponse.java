package com.lecture.lecture.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LectureResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final Long createdBy;
}
