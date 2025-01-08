package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.PointHistory;
import kr.hhplus.be.server.domain.user.PointHistory.PointHistoryBuilder;

public interface PointHistoryRepository {

    PointHistory save(PointHistory pointHistory);
}
