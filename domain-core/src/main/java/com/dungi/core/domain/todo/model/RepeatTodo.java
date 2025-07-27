package com.dungi.core.domain.todo.model;

import com.dungi.common.exception.BaseException;
import com.dungi.core.domain.common.value.DeleteStatus;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.dungi.common.response.BaseResponseStatus.INVALID_VALUE;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "R")
public class RepeatTodo extends Todo {

    @OneToMany(mappedBy = "repeatTodo")
    private List<RepeatDay> repeatDayList = new ArrayList<>();

    @Builder
    public RepeatTodo (
            Long roomId,
            Long userId,
            LocalDateTime deadline,
            String todoItem
    ){
        if(roomId == null) throw new BaseException(INVALID_VALUE);
        if(userId == null) throw new BaseException(INVALID_VALUE);
        if(deadline == null) throw new BaseException(INVALID_VALUE);
        if(StringUtils.isEmpty(todoItem)) throw new BaseException(INVALID_VALUE);

        super.setRoomId(roomId);
        super.setUserId(userId);
        super.setDeadline(deadline);
        super.setTodoItem(todoItem);
        super.setDeleteStatus(DeleteStatus.NOT_DELETED);
    }
}
