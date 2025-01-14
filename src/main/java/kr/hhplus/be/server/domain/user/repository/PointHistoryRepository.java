package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.point.PointHistory;

public interface PointHistoryRepository {

    PointHistory save(PointHistory pointHistory);
}
