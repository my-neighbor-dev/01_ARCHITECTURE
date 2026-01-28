package com.lecture.lecture.api;

import com.lecture.authorization.common.UserInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Lecture API 인터페이스
 */
@Tag(name = "Lecture", description = "강의 관련 API")
@RequestMapping("/api/lectures")
public interface LectureApi {
    
    @Operation(
        summary = "강의 조회",
        description = "강의 ID를 통해 강의 정보를 조회합니다. 자신이 만든 강의만 조회 가능합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "강의 조회 성공",
            content = @Content(schema = @Schema(implementation = LectureResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음 (다른 사용자의 강의)"
        )
    })
    @GetMapping("/{lectureId}")
    LectureResponse getLecture(@PathVariable("lectureId") Long lectureId);
    
    @Operation(
        summary = "강의 생성 (테스트용)",
        description = "테스트용 강의를 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "강의 생성 성공",
            content = @Content(schema = @Schema(implementation = LectureResponse.class))
        )
    })
    @PostMapping
    LectureResponse createLecture(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "강의 생성 요청",
            required = true
        )
        @RequestBody CreateLectureRequest request,
        @Parameter(hidden = true) UserInfo userInfo
    );
}
