package com.dungi.storage.rdb.repository.vote;

import com.dungi.core.domain.vote.model.VoteItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VoteItemJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveAll(List<VoteItem> voteItemList) {
        String sql = "INSERT INTO vote_item (choice, vote_id, created_time, modified_time) VALUES (?, ?, now(), now())";

        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, voteItemList.get(i).getChoice());
                        ps.setLong(2, voteItemList.get(i).getVote().getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return voteItemList.size();
                    }
                });
    }
}
