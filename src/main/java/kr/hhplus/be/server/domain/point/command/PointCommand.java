package kr.hhplus.be.server.domain.point.command;

import lombok.Builder;

public class PointCommand {

    @Builder
    public record Use(
        Long userId,
        Long point
    ){
    }

    @Builder
    public record Charge(
        Long userId,
        Long point
    ){
    }


}
