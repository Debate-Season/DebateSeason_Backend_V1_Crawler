package com.debate.croll.scheduler.news;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import com.debate.croll.domain.entity.Media;
import com.debate.croll.repository.MediaRepository;

import io.sentry.Sentry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component("NewsScheduler")
@Slf4j
public class News {

	private final MediaRepository mediaRepository;
	private final Map<String,String> newsList = new HashMap<>();
	private Set<String> keys; // 언론사 이름
	private final List<Integer> category = Arrays.asList(100,101,102,104); // category 번호

	private final ChromeOptions options;
	
	@PostConstruct
	public void init() { // 의존성 주입 -> 초기화 때 실행

		log.info("NewsScheduler를 초기화를 합니다.");

		newsList.put("한국경제","https://media.naver.com/press/015");
		newsList.put("매일경제","https://media.naver.com/press/009");
		newsList.put("한계례","https://media.naver.com/press/028");
		newsList.put("조선일보","https://media.naver.com/press/023");
		newsList.put("전자신문","https://media.naver.com/press/030");
		newsList.put("중앙일보","https://media.naver.com/press/025");
		newsList.put("연합뉴스","https://media.naver.com/press/422");
		newsList.put("YTN","https://media.naver.com/press/052");
		newsList.put("MBC","https://media.naver.com/press/214");
		newsList.put("SBS","https://media.naver.com/press/055");
		newsList.put("KBS","https://media.naver.com/press/056");

		keys = newsList.keySet();
	}


	//@Scheduled(cron = "0 0 17 * * ?",zone = "Asia/Seoul")
	//@Scheduled(fixedDelay = 86400000)
	public void crawl() {

		try{

			for(String name : keys) {

				String url = newsList.get(name);

				for (Integer i : category) {

					extractElement(url,name,i);
					Thread.sleep(1500);

				}
			}

		}
		catch (Exception e){

			log.error(e.getMessage());
			Sentry.captureException(e);

		}


	}

	public void extractElement(String url, String name, Integer i) {

		WebDriver driver = null; // 매번 새로 생성된 후, 다하고 버려야 한다. -> 일회용

		// 1. driver 예외를 잡기 위한 처리.
		try{

			driver = new ChromeDriver(options); // driver 생성 실패 시, 에러를 잡기 위함.
			driver.get(url + "?sid=" + i.toString());

			for (int j = 1; j <= 2; j++) {

				extractElement2(driver,name,i,j);
				Thread.sleep(1000);// 분까지 겹치는 경우를 방지해서 일부러 1초 기다림

			}

		}
		catch (Exception e){

			log.error(e.getMessage());
			Sentry.captureException(e);

		}
		finally {

			if (driver != null) {
				log.info("Done News Crawling ~ ");
				driver.quit();
			}

		}

	}


	public void extractElement2(WebDriver driver, String name, Integer i, int j){

		// 웹 페이지에서 Element를 가져올 때, 에러를 잡기 위한 설정.

		try{

			LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

			// 1. Link
			WebElement aTag = driver.findElement(
				By.cssSelector("#ct > div._tab_panel > div:nth-child(1) > ul > li:nth-child(" + j
					+ ") > a.press_edit_news_link._es_pc_link"));
			String href = aTag.getAttribute("href");

			// 2. title
			WebElement titleElement = driver.findElement(By.cssSelector(
				"#ct > div._tab_panel > div:nth-child(1) > ul > li:nth-child(" + j
					+ ") > a.press_edit_news_link._es_pc_link > span.press_edit_news_text > span.press_edit_news_title"));
			String title = titleElement.getText();

			// 3. img
			String image;
			try {
				WebElement imgElement = driver.findElement(By.cssSelector(
					"#ct > div._tab_panel > div:nth-child(1) > ul > li:nth-child(" + j
						+ ") > a.press_edit_news_link._es_pc_link > span.press_edit_news_thumb > img"));
				image = imgElement.getAttribute("src");
			} catch (NoSuchElementException e) { // 없으면 image는 null이다.
				image = null;
			}

			// 4. time
			WebElement timeElement = driver.findElement(By.cssSelector(
				"#ct > div._tab_panel > div:nth-child(1) > ul > li:nth-child(" + j
					+ ") > a > span.press_edit_news_text > span.r_ico_b.r_modify"));
			String outdpated = timeElement.getText();

			LocalDateTime time = null;
			// 분전(min),시간전(hour),일전(day)

			if (outdpated.contains("분전")) {
				outdpated = outdpated.replace("분전", "");
				time = now.minusMinutes(Integer.parseInt(outdpated));
			} else if (outdpated.contains("시간전")) {
				outdpated = outdpated.replace("시간전", "");
				time = now.minusHours(Integer.parseInt(outdpated));

			} else if (outdpated.contains("일전")) {
				outdpated = outdpated.replace("일전", "");
				time = now.minusDays(Integer.parseInt(outdpated));

			}

			// 카테고리 넣기
			// 정치 https://media.naver.com/press/422?sid=100
			// 경제 https://media.naver.com/press/422?sid=101
			// 사회 https://media.naver.com/press/422?sid=102
			// IT https://media.naver.com/press/422?sid=105
			String categoryName;

			categoryName = switch (i) {
				case 100 -> "정치";
				case 101 -> "경제";
				case 102 -> "사회";
				case 104 -> "세계";
				default -> null; // 또는 "" / 필요하면 기본값
			};

			Media news = Media.builder()
				.title(title)
				.url(href)
				.src(image)
				.category(categoryName)
				.media(name)
				.type("news")
				.count(0)
				.createdAt(time)
				.build();
			mediaRepository.save(news);

		}
		catch ( Exception e ){

			log.error(e.getMessage());
			Sentry.captureException(e);
		}

	}
}
