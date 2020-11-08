package com.estudo.springbatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.estudo.springbatch.domain.Pessoa;

public class UpperCaseProcessor implements ItemProcessor<Pessoa, Pessoa> {
	
	private static final Logger logger = LoggerFactory.getLogger(UpperCaseProcessor.class);


	@Override
	public Pessoa process(Pessoa pessoa) throws Exception {
	
		String primeiroNome =  pessoa.getPrimeiroNome().toUpperCase();
		String ultimoNome =  pessoa.getUltimoNome().toUpperCase();
		
		Pessoa pessoaNomeUpper = new Pessoa(primeiroNome, ultimoNome);
		
		logger.info("Convertendo : " + pessoa + " para : " + pessoaNomeUpper);
		
		return pessoaNomeUpper;
	}

}
