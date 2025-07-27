package com.dungi.core.domain.summary.model;

import com.dungi.core.domain.common.model.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Entity
@Table(
        name = "weekly_todo_count",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"room_id", "user_id", "year", "week_of_year", "day_of_week"})
        }
)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyTodoCount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "year")
    private Integer year;

    @Column(name = "week_of_year")
    private Integer weekOfYear;

    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    @Column(name = "todo_count")
    private Long todoCount;

    public void addTodoCount() {
        this.todoCount++;
    }
}
