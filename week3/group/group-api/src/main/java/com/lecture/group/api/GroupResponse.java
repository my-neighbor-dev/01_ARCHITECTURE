package com.lecture.group.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupResponse {
    private final Long id;
    private final String name;
    private final String description;
}
