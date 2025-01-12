package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.application.user.dto.UserInfo;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.domain.user.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public Long getUserPoint(long id) {
        return userService.userPoint(id);
    }

    @Transactional
    public void chargePoint(long id, long amount) {
        userService.chargePoint(id, amount);
    }

}
