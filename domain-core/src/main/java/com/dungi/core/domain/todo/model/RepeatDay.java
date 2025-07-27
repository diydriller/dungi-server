package com.dungi.core.domain.todo.model;

import com.dungi.common.exception.BaseException;
import lombok.*;

import javax.persistence.*;

import java.time.DayOfWeek;

import static com.dungi.common.response.BaseResponseStatus.INVALID_VALUE;


@Getter
@Entity
@Table(name = "repeat_day")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RepeatDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="repeat_day_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="todo_id")
    private RepeatTodo repeatTodo;

    public RepeatDay(DayOfWeek dayOfWeek){
        if(dayOfWeek == null) throw new BaseException(INVALID_VALUE);

        this.dayOfWeek = dayOfWeek;
    }

    public void setRepeatTodo(RepeatTodo repeatTodo){
        this.repeatTodo = repeatTodo;
        repeatTodo.getRepeatDayList().add(this);
    }
}
