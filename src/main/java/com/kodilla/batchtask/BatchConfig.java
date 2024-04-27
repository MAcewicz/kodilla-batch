package com.kodilla.batchtask;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;


    public BatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public FlatFileItemReader<Person> reader() {
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("task_input.csv"));

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("name", "surname", "birthday");

        BeanWrapperFieldSetMapper<Person> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(Person.class);

        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);
        reader.setLineMapper(lineMapper);

        return reader;
    }

    @Bean
    public PersonProcessor processor() {
        return new PersonProcessor();
    }

    @Bean
    public FlatFileItemWriter<PersonProcessed> writer() {
        BeanWrapperFieldExtractor<PersonProcessed> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"name", "surname", "age"});

        DelimitedLineAggregator<PersonProcessed> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        FlatFileItemWriter<PersonProcessed> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("task_output.csv"));
        writer.setShouldDeleteIfExists(true);
        writer.setLineAggregator(aggregator);

        return writer;
    }

    @Bean
    Step getAge(

            ItemReader<Person> reader,
            ItemProcessor<Person, PersonProcessed> processor,
            ItemWriter<PersonProcessed> writer) {

        return stepBuilderFactory.get("getAge")
                .<Person, PersonProcessed>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    Job calculateAge(Step priceChange) {
        return jobBuilderFactory.get("calculateAge")
                .incrementer(new RunIdIncrementer())
                .flow(priceChange)
                .end()
                .build();
    }

}
