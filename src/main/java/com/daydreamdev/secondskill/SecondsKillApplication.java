package com.daydreamdev.secondskill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class SecondsKillApplication {
	
	/**
	 * @author G.Fukang
	 * @date: 6/7 20:49
	 */
	public static void main(String[] args) {
		SpringApplication.run(SecondsKillApplication.class, args);
	}

}
