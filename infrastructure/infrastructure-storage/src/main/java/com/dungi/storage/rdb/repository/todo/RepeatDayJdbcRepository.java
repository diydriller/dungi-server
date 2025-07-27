package com.dungi.storage.rdb.repository.todo;

import com.dungi.core.domain.todo.model.RepeatDay;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RepeatDayJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<RepeatDay> repeatDayList) {
        String sql = "INSERT INTO repeat_day (day, todo_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, repeatDayList.get(i).getDay());
                        ps.setLong(2, repeatDayList.get(i).getRepeatTodo().getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return repeatDayList.size();
                    }
                });
    }
}
