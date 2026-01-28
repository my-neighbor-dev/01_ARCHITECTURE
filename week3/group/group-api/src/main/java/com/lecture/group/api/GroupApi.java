package com.lecture.group.api;

import io.swagger.v3.oas.annotations.Operation;
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
 * Group API 인터페이스
 */
@Tag(name = "Group", description = "그룹 관련 API")
@RequestMapping("/api/groups")
public interface GroupApi {
    
    @Operation(
        summary = "그룹 조회",
        description = "그룹 ID를 통해 그룹 정보를 조회합니다. 자신이 속한 그룹만 조회 가능합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "그룹 조회 성공",
            content = @Content(schema = @Schema(implementation = GroupResponse.class))
        ),
        @ApiResponse(
            responseCode = "403",
            description = "접근 권한 없음 (다른 그룹)"
        )
    })
    @GetMapping("/{groupId}")
    GroupResponse getGroup(@PathVariable("groupId") Long groupId);
    
    @Operation(
        summary = "그룹 생성 (테스트용)",
        description = "테스트용 그룹을 생성합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "그룹 생성 성공",
            content = @Content(schema = @Schema(implementation = GroupResponse.class))
        )
    })
    @PostMapping
    GroupResponse createGroup(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "그룹 생성 요청",
            required = true
        )
        @RequestBody CreateGroupRequest request
    );
    
    @Operation(
        summary = "유저를 그룹에 추가 (테스트용)",
        description = "유저를 그룹에 매핑합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "유저 추가 성공"
        )
    })
    @PostMapping("/{groupId}/users")
    void addUserToGroup(
        @PathVariable("groupId") Long groupId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "유저 추가 요청",
            required = true
        )
        @RequestBody AddUserToGroupRequest request
    );
}
