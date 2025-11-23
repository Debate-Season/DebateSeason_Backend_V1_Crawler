package com.debate.croll.scheduler.community.type;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
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
public class FmKorea extends AbstractCommunityCrawl {

	private final MediaRepository mediaRepository;
	private final ChromeOptions options;
	private LocalDate today;

	//@Scheduled(fixedDelay = 86400000)
	public void crawl() throws InterruptedException {

		WebDriver driver = null;

		try{

			log.info("do crawling ~ ");

			driver = super.setWebDriver(options,"fmkorea");
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

			log.info("find element");

			int loop = CommunityConfig.loop;

			// 오늘 YYYY-MM-DD
			today = LocalDate.now();

			for (int i = 1; i <= loop; i++) {

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

				today = null; // 크롤링 작업을 끝나고, 날짜를 갱신한다.

				driver.quit();
				Thread.sleep(3000); // 의도적인 컨텍스트 스위칭 유발로, 다른 스레드 작업 처리를 위한 목적.
				log.info("successfully shut driver");
			}

		}



	}

	public void extractElement(WebDriver driver,int i) {

		try{

			WebElement titleElement = driver.findElement(By.cssSelector(
				"#bd_4180795_0 > div > div.fm_best_widget._bd_pc > ul > li:nth-child(" + i + ") > div > h3 > a"));
			WebElement timeElement = driver.findElement(By.cssSelector(
				"#bd_4180795_0 > div > div.fm_best_widget._bd_pc > ul > li:nth-child(" + i
					+ ") > div > div:nth-child(5) > span.regdate"));

			String image = null;

			// 이미지가 null이면 null인 상태로 넘어간다.
			try {
				WebElement imgElement = driver.findElement(By.cssSelector(
					"#bd_4180795_0 > div > div.fm_best_widget._bd_pc > ul > li:nth-child(" + i
						+ ") > div > a:nth-child(2) > img"));

				image = imgElement.getAttribute("src");
			} catch (NoSuchElementException e) {// 이미지가 없는 경우, NoSuchElementException 발생.

			}

			String timeString = today + " " + timeElement.getText();

			// Create a DateTimeFormatter with the appropriate pattern
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

			// Parse the string to a LocalDateTime object
			LocalDateTime dateTime = LocalDateTime.parse(timeString, formatter);

			Media fmKorea = Media.builder()
				.title(titleElement.getText())
				.url(titleElement.getAttribute("href"))
				.src(image)
				.category("정치")
				.media("에펨코리아")
				.type("community")
				.count(0)
				.createdAt(dateTime)
				.build();

			mediaRepository.save(fmKorea);

		}
		catch (Exception e){

			log.error(e.getMessage());

			String errorPage = driver.getPageSource();
			log.error(errorPage);

			Sentry.captureException(e);
		}



	}


}
