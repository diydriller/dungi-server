package com.dungi.storage.rdb.repository.summary;

import com.dungi.core.domain.summary.model.WeeklyTopUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeeklyTopUserJpaRepository extends JpaRepository<WeeklyTopUser, Long> {
    List<WeeklyTopUser> findAllByRoomIdAndYearAndWeekOfYear(Long roomId, Integer year, Integer weekOfYear);
}
