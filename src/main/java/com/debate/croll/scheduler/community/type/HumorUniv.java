package com.debate.croll.scheduler.community.type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;

import com.debate.croll.common.CommunityConfig;
import com.debate.croll.domain.entity.Media;
import com.debate.croll.repository.MediaRepository;
import com.debate.croll.scheduler.community.template.AbstractCommunityCrawl;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class HumorUniv extends AbstractCommunityCrawl {

	private final MediaRepository mediaRepository;
	private final ChromeOptions options;
	private final int start = 3;

	//@Scheduled(fixedDelay = 86400000)
	public void crawl() throws InterruptedException {

		WebDriver driver = null;

		try{

			log.info("do crawling ~ ");

			driver = super.setWebDriver(options,"humoruniv");
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));

			log.info("find element");

			int loop = CommunityConfig.loop;

			for (int i = 0; i < loop; i++) {

				extractElement(driver,i);
				Thread.sleep(2000); // 의심을 피하기 위한 설정.

			}

		}
		catch (Exception e){

			// 1. Webdriver 예외 처리.
			log.error(e.getMessage());
			Sentry.captureException(e);

		}
		finally {

			if (driver != null) {

				driver.quit();
				Thread.sleep(3000); // 의도적인 컨텍스트 스위칭 유발로, 다른 스레드 작업 처리를 위한 목적.
				log.info("successfully shut driver");
			}

		}

	}

	public void extractElement(WebDriver driver,int i) {

		try {

			WebElement webElement = driver.findElement(
				By.cssSelector("#list_body > ul > a:nth-child(" + (start + i * 2) + ")"));

			//
			WebElement idElement = webElement.findElement(By.cssSelector("li"));
			String id = idElement.getAttribute("id");

			String numberOnly = id.replaceAll("[^0-9]", ""); // 숫자가 아닌 문자를 모두 제거

			String image = driver.findElement(
					By.cssSelector("#" + id + "> table > tbody > tr > td:nth-child(1) > div > img"))
				.getAttribute("src");
			String title = driver.findElement(By.cssSelector("#title_chk_pds-" + numberOnly)).getText();
			//#title_chk_pds-1366802

			String time = driver.findElement(
				By.cssSelector("#" + id + "> table > tbody > tr > td:nth-child(2) > div > span.extra")).getText();

			// href
			String href = driver.findElement(
				By.cssSelector("#list_body > ul > a:nth-child(" + (start + i * 2) + ")")).getAttribute("href");

			// 2. 시간 부분만 추출
			String timePart = time.split(" ")[1]; // "07:31"

			// 3. 시:분 파싱
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
			LocalTime parsedTime = LocalTime.parse(timePart, timeFormatter);

			// 4. 현재 날짜에 시:분만 교체
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime updatedDateTime = now
				.withHour(parsedTime.getHour())
				.withMinute(parsedTime.getMinute())
				.withSecond(0)
				.withNano(0);

			Media humorUniv = Media.builder()
				.title(title)
				.url(href)
				.src(image)
				.category("사회")
				.media("웃긴대학")
				.type("community")
				.count(0)
				.createdAt(updatedDateTime)
				.build();

			mediaRepository.save(humorUniv);

		}
		catch (Exception e){

			log.error(e.getMessage());

			String errorPage = driver.getPageSource();
			log.error(errorPage);

			Sentry.captureException(e);
		}

	}

}
