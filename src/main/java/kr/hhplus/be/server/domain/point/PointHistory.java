package kr.hhplus.be.server.domain.point;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.common.Base;
import kr.hhplus.be.server.domain.point.enums.PointTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "point_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Point point;

    @Comment("입력 받은 금액")
    private Long amount;

    @Comment("수정 후 포인트")
    private Long updatedPoint;

    @Enumerated(EnumType.STRING)
    private PointTransactionType type;

}
