package com.dungi.core.domain.room.model;

import com.dungi.core.domain.common.model.BaseEntity;
import com.dungi.core.domain.common.value.DeleteStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@Table(name = "room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    private String name;

    private String color;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    private DeleteStatus deleteStatus;

    @OneToMany(mappedBy = "room", cascade = CascadeType.PERSIST)
    private List<UserRoom> userRoomList = new ArrayList<>();

    public Room(String name, String color) {
        this.color = color;
        this.name = name;
        this.deleteStatus = DeleteStatus.NOT_DELETED;
    }

    public void deactivate() {
        this.deleteStatus = DeleteStatus.DELETED;
    }
}
