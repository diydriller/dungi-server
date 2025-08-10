package com.dungi.batchserver.batch.job;

import com.dungi.core.domain.common.value.DeleteStatus;
import com.dungi.core.domain.room.model.Room;
import com.dungi.core.domain.summary.model.WeeklyTopUser;
import com.dungi.core.integration.store.summary.WeeklyStatisticStore;
import com.dungi.core.integration.store.user.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class JobConfig {
    private final JobBuilderFactory job;
    private final EntityManagerFactory entityManagerFactory;
    private final WeeklyStatisticStore weeklyStatisticStore;
    private final UserStore userStore;
    private final StepBuilderFactory steps;
    private final DataSource dataSource;

    private final static int CHUNK_SIZE = 10;

    @Bean(name = "decideWeeklyTopUserJob")
    public Job decideWeeklyTopUserJob() {
        return job.get("decideWeeklyTopUserJob")
                .start(decideWeeklyTopUserStep())
                .build();
    }

    @Bean
    public Step decideWeeklyTopUserStep() {
        return steps.get("decideWeeklyTopUserStep")
                .<Room, Room>chunk(CHUNK_SIZE)
                .reader(decideWeeklyTopUserReader())
                .writer(decideWeeklyTopUserListWriter(weeklyTopUserWriter(dataSource)))
                .build();
    }

    @Bean
    public JpaCursorItemReader<Room> decideWeeklyTopUserReader() {
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("deleteStatus", DeleteStatus.NOT_DELETED);

        return new JpaCursorItemReaderBuilder<Room>()
                .queryString("SELECT r FROM Room r WHERE r.deleteStatus = :deleteStatus ORDER BY r.id ASC")
                .entityManagerFactory(entityManagerFactory)
                .parameterValues(parameterValues)
                .name("decideWeeklyTopUserCursorReader")
                .build();
    }

    @Bean
    public ItemWriter<Room> decideWeeklyTopUserListWriter(JdbcBatchItemWriter<WeeklyTopUser> weeklyTopUserWriter) {
        return items -> {
            List<WeeklyTopUser> flatList = items.parallelStream()
                    .map(room -> {
                        var lastWeekDate = LocalDate.now().minusWeeks(1);
                        var weekFields = WeekFields.ISO;
                        var year = lastWeekDate.getYear();
                        var weekOfYear = lastWeekDate.get(weekFields.weekOfYear());

                        return weeklyStatisticStore.decideAndGetWeeklyTopUserInRoom(room.getId(), year, weekOfYear).stream()
                                .map(weeklyTodoCount ->
                                        WeeklyTopUser.builder()
                                                .userId(weeklyTodoCount.getUserId())
                                                .roomId(weeklyTodoCount.getRoomId())
                                                .weekOfYear(weeklyTodoCount.getWeekOfYear())
                                                .year(weeklyTodoCount.getYear())
                                                .build()
                                ).collect(Collectors.toList());
                    }).flatMap(List::stream)
                    .collect(Collectors.toList());

            weeklyTopUserWriter.write(flatList);
        };
    }

    @Bean
    public JdbcBatchItemWriter<WeeklyTopUser> weeklyTopUserWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<WeeklyTopUser>()
                .dataSource(dataSource)
                .sql("INSERT INTO weekly_top_user (created_time, modified_time, room_id, user_id, week_of_year, year) VALUES (now(), now(), :roomId, :userId, :weekOfYear, :year)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}

