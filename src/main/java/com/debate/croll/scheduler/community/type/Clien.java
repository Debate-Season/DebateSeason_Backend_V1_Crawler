package com.debate.croll.scheduler.community.type;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class Clien extends AbstractCommunityCrawl {

	private final MediaRepository mediaRepository;
	private final ChromeOptions options;

	//@Scheduled(fixedDelay = 86400000)
	public void crawl() throws InterruptedException {

		WebDriver driver = null;

		try{

			log.info("do crawling ~ ");

			driver = super.setWebDriver(options,"clien");
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

			log.info("find element");

			int loop = CommunityConfig.loop;

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

				driver.quit();
				Thread.sleep(3000); // 의도적인 컨텍스트 스위칭 유발로, 다른 스레드 작업 처리를 위한 목적.
				log.info("successfully shut driver");

			}

		}

	}

	public void extractElement(WebDriver driver,int i) {

		try{

			// body > div.nav_container > div.nav_body > div.nav_content > div.content_list > div.list_content > div:nth-child(2)
			// body > div.nav_container > div.nav_body > div.nav_content > div.content_list > div.list_content > div:nth-child(1)
			WebElement webElement =
				driver.findElement(By.cssSelector(
					"body > div.nav_container > div.nav_body > div.nav_content > div.content_list > div.list_content > div:nth-child("
						+ i + ")"));

			// body > div.nav_container > div.nav_body > div.nav_content > div.content_list > div.list_content > div:nth-child(1) > div.list_title
			WebElement hrefElement = webElement.findElement(By.cssSelector("div.list_title"));

			// body > div.nav_container > div.nav_body > div.nav_content > div.content_list > div.list_content > div:nth-child(1) > div.list_title > a.list_subject
			String url = hrefElement.findElement(By.cssSelector("a.list_subject")).getAttribute("href");
			// body > div.nav_container > div.nav_body > div.nav_content > div.content_list > div.list_content > div:nth-child(1) > div.list_title > a.list_subject > span.subject_fixed
			String title = hrefElement.findElement(By.cssSelector("a.list_subject > span.subject_fixed")).getText();

			// body > div.nav_container > div.nav_body > div.nav_content > div.content_list > div.list_content > div:nth-child(1) > div.list_time > span > span
			String time = webElement.findElement(By.cssSelector("div.list_time > span")).getText();

			LocalDate today = LocalDate.now();

			String reformTime = time.replace("-", ":");

			String date = today + " " + reformTime;

			// Create a DateTimeFormatter with the appropriate pattern
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			// Parse the string to a LocalDateTime object
			LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

			Media Clien = Media.builder()
				.title(title)
				.url(url)
				.src(null)// 이미지
				.category("사회")
				.media("클리앙")
				.type("community")
				.count(0)
				.createdAt(dateTime)
				.build();

			mediaRepository.save(Clien);

		}
		catch (Exception e){

			log.error(e.getMessage());

			String errorPage = driver.getPageSource();
			log.error(errorPage);

			Sentry.captureException(e);
		}

	}
}
