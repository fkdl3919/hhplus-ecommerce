package kr.hhplus.be.server.domain.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.repository.PointRepository;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserUnitTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointRepository pointRepository;


    @Test
    @DisplayName("case - 유저가 존재하지 않을 경우 EntityNotFoundException 발생")
    public void userPointTest1(){
        // given
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.userPoint(userId));

        // then
        assertEquals("유저가 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("case - 유저의 포인트가 존재하지 않을 경우 포인트가 0인 포인트 객체 반환")
    public void userPointTest2(){
        // given
        long userId = 1L;

        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pointRepository.findPointByUserIdWithLock(user.getId()))
            .thenReturn(Optional.of(Point.emptyPoint(user)));

        // when
        Long point = userService.userPoint(userId);

        // then
        assertEquals(0, point);

    }

}
