package ru.otus.springlibrary.configuration;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.springlibrary.domain.AuthorMongo;
import ru.otus.springlibrary.domain.AuthorSQL;

import javax.sql.DataSource;
import java.util.HashMap;

@EnableBatchProcessing
@Configuration
@AllArgsConstructor
public class BatchConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BatchConfig.class);

    private final JobBuilderFactory jobBuilderFactory;

    private final StepBuilderFactory stepBuilderFactory;

    private final MongoTemplate mongoTemplate;

    private final DataSource dataSource;

    @Bean
    public ItemReader reader() {
        return new MongoItemReaderBuilder<AuthorMongo>()
                .name("reviewItemReader")
                .template(mongoTemplate)
                .targetType(AuthorMongo.class)
                .jsonQuery("{}")
                .sorts(new HashMap<>())
                .build();
    }

    @Bean
    public ItemProcessor processor() {

        return (ItemProcessor<AuthorMongo, AuthorSQL>) authorMongo -> new AuthorSQL(authorMongo.getId().getDate().getTime(), authorMongo.getFirstName(), authorMongo.getLastName());
    }

    @Bean
    public ItemWriter writer() {
        return new JdbcBatchItemWriterBuilder<AuthorSQL>()
                .dataSource(dataSource)
                .sql("INSERT INTO test.authors (id, first_name, last_name) VALUES (?, ?, ?);")
                .itemPreparedStatementSetter((author, preparedStatement) -> {
                    preparedStatement.setLong(1, author.getId());
                    preparedStatement.setString(2, author.getFirstName());
                    preparedStatement.setString(3, author.getLastName());
                })
                .build();
    }

    @Bean
    public Job importUserJob(Step step) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public Step migrateAuthorsFromMongoToH2(ItemWriter<? super Object> writer, ItemReader<AuthorMongo> reader,
                                            ItemProcessor<? super Object, AuthorMongo> processor) {
        StepBuilder stepBuilder = stepBuilderFactory.get("migrateAuthorsFromMongoToH2");
        return stepBuilder
                .chunk(5)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
