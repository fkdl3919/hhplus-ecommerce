package kr.hhplus.be.server.interfaces.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public record PagingResponse<T>(
    @Schema(description = "아이템 목록")
    List<T> item,

    @Schema(description = "페이징 정보")
    Pageable pageInfo,

    @Schema(description = "총 아이템 수")
    long totalCount,

    @Schema(description = "총 페이지 수")
    int totalPage
) {

    public static PagingResponse from(PageImpl page) {
        return new PagingResponse(page.getContent(), page.getPageable(), page.getTotalElements(), page.getTotalPages());
    }

}
