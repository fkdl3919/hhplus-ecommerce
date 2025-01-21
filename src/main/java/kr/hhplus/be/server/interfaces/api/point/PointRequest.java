package kr.hhplus.be.server.interfaces.api.point;

import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.point.command.PointCommand;
import kr.hhplus.be.server.interfaces.api.order.OrderRequest;

public record PointRequest(
    long userId,
    long amount
) {

    public static PointCommand.Charge toCharge(PointRequest request) {
        return PointCommand.Charge.builder().userId(request.userId).point(request.amount).build();
    }

}
