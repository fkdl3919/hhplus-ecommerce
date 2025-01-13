package kr.hhplus.be.server.domain.user.repository;

import java.util.Optional;
import kr.hhplus.be.server.domain.user.User;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);
}
