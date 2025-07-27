package com.dungi.core.domain.summary.model;

import com.dungi.core.domain.common.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Entity
@Table(name = "weekly_top_user")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyTopUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long roomId;

    private Integer year;

    private Integer weekOfYear;
}
