package com.dungi.storage.rdb.repository.room;

import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.room.model.Room;
import com.dungi.storage.dto.room.GetRoomUserDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomJpaRepository extends CrudRepository<Room, Long> {
    @Query("SELECT r " +
            " FROM Room r " +
            " WHERE r.id=:roomId AND r.deleteStatus=:status ")
    Optional<Room> getRoom(
            @Param("roomId") Long id,
            @Param("status") DeleteStatus status
    );

    @Query("SELECT DISTINCT r " +
            " FROM Room r JOIN r.userRoomList ur " +
            " WHERE ur.userId=:userId AND ur.deleteStatus=:status")
    List<Room> getAllRoomEnteredByUser(
            @Param("userId") Long userId,
            @Param("status") DeleteStatus status,
            Pageable pageable
    );

    @Query("SELECT new com.dungi.storage.dto.room.GetRoomUserDto(u.profileImg, u.nickname) " +
            " FROM User u INNER JOIN UserRoom ur ON u.id=ur.userId " +
            " WHERE ur.room=:room")
    List<GetRoomUserDto> getAllMemberInfo(
            @Param("room") Room room
    );

    @Query("SELECT COUNT(DISTINCT ur.userId)" +
            " FROM UserRoom ur" +
            " WHERE ur.room.id=:roomId AND ur.deleteStatus=:status")
    int getRoomMemberCnt(
            @Param("roomId") Long roomId,
            @Param("status") DeleteStatus status
    );
}
