package com.dungi.batchserver.batch.writer;

import org.springframework.batch.item.database.JpaItemWriter;

import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

public class JpaItemListWriter<T> extends JpaItemWriter<List<T>> {
    private final JpaItemWriter<T> jpaItemWriter;

    public JpaItemListWriter(JpaItemWriter<T> jpaItemWriter, EntityManagerFactory entityManagerFactory) {
        this.jpaItemWriter = jpaItemWriter;
        setEntityManagerFactory(entityManagerFactory);
    }

    @Override
    public void write(List<? extends List<T>> items) {
        List<T> totalList = new ArrayList<>();
        for (List<T> list : items) {
            totalList.addAll(list);
        }
        jpaItemWriter.write(totalList);
    }
}
