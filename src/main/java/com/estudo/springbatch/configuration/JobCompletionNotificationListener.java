package com.estudo.springbatch.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.estudo.springbatch.domain.Pessoa;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {
	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	//AFTERJOB - verificaremos no JobRepository se tudo deu certo e as pessoas foram salvas , mas somente se
	//o status do meu JobExecution for Completed
	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Tá na hora de verificar se deu certo!!!");

			jdbcTemplate
					.query("SELECT primeiro_nome, ultimo_nome FROM pessoa",
							(rs, row) -> new Pessoa(rs.getString(1), rs.getString(2)))
					.forEach(pessoa -> log.info("Found <" + pessoa + "> in the database."));
		}
	}

}
