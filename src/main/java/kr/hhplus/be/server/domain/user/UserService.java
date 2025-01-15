package kr.hhplus.be.server.domain.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("유저가 존재하지 않습니다."));
        return user;
    }


}
