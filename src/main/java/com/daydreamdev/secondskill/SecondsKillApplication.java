package com.daydreamdev.secondskill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableKafka
@ComponentScan("com.daydreamdev.secondskill")
public class SecondsKillApplication {

    /**
     * @author G.Fukang
     * @date: 6/7 20:49
     */
    public static void main(String[] args) {
        SpringApplication.run(SecondsKillApplication.class, args);
/*		new SpringApplicationBuilder(SecondsKillApplication.class).
				listeners(new ApplicationPidFileWriter())
				.run(args);
		Consumer consumer = SpringBeanFactory.getBean(Consumer.class);
		new Thread(consumer, "消费者").start();
		log.info("消费者线程启动成功!");*/
    }

}
