package com.debate.croll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.debate.croll.scheduler.manager.CrawlerRecoveryManager;

@EnableScheduling
@SpringBootApplication
public class CrollApplication {
	public static void main(String[] args) {

		// static 필드 생성 -> 빈 생성 후, @PostConstruct로 static 필드 초기화. -> 이후, rebootCrawl() 실행.
		// 현재는 NPE 문제 없지만, 의도를 모르는 다른 개발자가 사용할 경우 문제 발생.
		SpringApplication.run(CrollApplication.class, args);
		CrawlerRecoveryManager.rebootCrawl();
	}
}
