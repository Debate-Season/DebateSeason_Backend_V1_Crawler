package com.debate.croll.scheduler.community.type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
public class MlbPark extends AbstractCommunityCrawl {

	private final MediaRepository mediaRepository;
	private final ChromeOptions options;

	//@Scheduled(fixedDelay = 86400000)
	public void crawl() throws InterruptedException {

		WebDriver driver = null;

		try{

			log.info("do crawling ~ ");

			driver = super.setWebDriver(options,"mlbpark");
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

		try {

			WebElement webElement = driver.findElement(By.cssSelector(
				"#container > div.contents > div.left_cont > div > div.tab_contents > div.tbl_box > table > tbody > tr:nth-child("
					+ i + ")"));

			//String id = webElement.findElement(By.cssSelector("td:nth-child(1)")).getText();

			WebElement titleElement = webElement.findElement(By.cssSelector("td:nth-child(2) > a"));
			String title = titleElement.getText();

			String href = titleElement.getAttribute("href");

			//String date = webElement.findElement(By.cssSelector("td:nth-child(4) > span")).getText();

			LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

			Media mlbPark = Media.builder()
				.title(title)
				.url(href)
				.src(null)// 이미지 원래 없음.
				.category("사회")
				.media("엠엘비파크")
				.type("community")
				.count(0)
				.createdAt(now)
				.build();

			mediaRepository.save(mlbPark);

		}
		catch (Exception e) {

			log.error(e.getMessage());

			String errorPage = driver.getPageSource();
			log.error(errorPage);

			Sentry.captureException(e);

		}


	}

}
