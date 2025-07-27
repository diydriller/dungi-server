package com.dungi.core.domain.todo.model;

import com.dungi.core.domain.common.model.BaseEntity;
import com.dungi.core.domain.common.value.DeleteStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@Setter(value = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "todo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorColumn
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @Column(name="todo_item")
    private String todoItem;

    private LocalDateTime deadline;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "users_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    private DeleteStatus deleteStatus;
}

