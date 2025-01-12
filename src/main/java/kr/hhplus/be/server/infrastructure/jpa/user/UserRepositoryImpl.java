package kr.hhplus.be.server.infrastructure.jpa.user;

import java.util.Optional;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

}
