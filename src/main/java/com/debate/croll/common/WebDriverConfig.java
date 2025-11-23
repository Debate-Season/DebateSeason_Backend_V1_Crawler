package com.debate.croll.common;

import java.util.Random;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.bonigarcia.wdm.WebDriverManager;

@Configuration
public class WebDriverConfig {

	@Bean
	public ChromeOptions chromeOptions(){

		// 공통 Options
		WebDriverManager.chromedriver()
			.setup();

		// 랜덤 User-Agent 리스트
		String[] userAgents = {
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
		};

		// 랜덤 User-Agent 선택
		Random rand = new Random();
		String userAgent = userAgents[rand.nextInt(userAgents.length)];

		ChromeOptions options = new ChromeOptions();

		options.addArguments("--headless=new"); // headless 모드
		options.addArguments("--user-agent=" + userAgent); // 랜덤 User-Agent 설정
		options.addArguments("--no-sandbox"); // 추가적인 권한 설정
		options.addArguments("--disable-dev-shm-usage"); // Docker 환경에서 공유 메모리 부족 문제 해결


		// 추가적인 헤더 설정 (필요시)
		options.addArguments("--disable-blink-features=AutomationControlled"); // 이거 false 나와야 자동화 회피 가능하다
		options.addArguments("--disable-gpu"); // GPU 비활성화 (성능 개선)


		return options;
	}
}
