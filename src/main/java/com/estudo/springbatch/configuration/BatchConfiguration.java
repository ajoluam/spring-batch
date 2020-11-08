package com.estudo.springbatch.configuration;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.estudo.springbatch.domain.Pessoa;
import com.estudo.springbatch.processor.UpperCaseProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	//JOB
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
	  return jobBuilderFactory.get("importUserJob")
	    .incrementer(new RunIdIncrementer())
	    .listener(listener) //Possui métodos BEFOREJOB e AFTERJOB a serem implementados
	    .flow(step1)
	    .end()
	    .build();
	}

	
	//STEP - Observe que para o meu chunck terei um processamento de 10 itens, seguindo a ordem READER-PROCESSOR-WRITER
	@Bean
	public Step step1(JdbcBatchItemWriter<Pessoa> writer) {
	  return stepBuilderFactory.get("step1")
	    .<Pessoa, Pessoa> chunk(4)
	    .reader(reader())
	    .processor(processor())
	    .writer(writer)
	    .build();
	}

	// READER
	@Bean
	public FlatFileItemReader<Pessoa> reader() {

		FlatFileItemReader<Pessoa> pessoaReader = new FlatFileItemReaderBuilder<Pessoa>()
				.name("pessoaItemReader") // nome da instancia do reader
				.resource(new ClassPathResource("sample-data.csv")) // recurso que será usado como Input
				.delimited() // implementação que divide a string de entrada em um delimitador configurável.
								// Essa implementação também suporta o uso de um caractere de escape para
								// delimitadores de escape e terminações de linha.
				.names(new String[] { "primeiroNome", "ultimoNome" }) // Os nomes de cada um dos campos dentro dos
																	  // campos que são retornados na ordem, eles
																	  // ocorrem dentro do arquivo delimitado.
																	  // Requeridos.
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Pessoa>() {
					{
						setTargetType(Pessoa.class);
					}
				})
				.build();

		return pessoaReader;
	}

	// PROCESSOR
	@Bean
	public UpperCaseProcessor processor() {
		return new UpperCaseProcessor();
	}
	
	//WRITER - A cada step é salvo no JobRepository a chunck com 10 itens 
	@Bean
	public JdbcBatchItemWriter<Pessoa> writer(DataSource dataSource) {
		
		JdbcBatchItemWriter<Pessoa> jdbcItemWriter = new JdbcBatchItemWriterBuilder<Pessoa>()
			    .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			    .sql("INSERT INTO pessoa (primeiro_nome, ultimo_nome) VALUES (:primeiroNome, :ultimoNome)")
			    .dataSource(dataSource)
			    .build();
				
	  return jdbcItemWriter;
	}

}
