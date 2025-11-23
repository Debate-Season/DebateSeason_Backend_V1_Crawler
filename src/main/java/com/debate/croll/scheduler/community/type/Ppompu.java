package com.debate.croll.scheduler.community.type;

import java.time.Duration;
import java.time.LocalDateTime;

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
public class Ppompu extends AbstractCommunityCrawl {

	private final MediaRepository mediaRepository;
	private final ChromeOptions options;

	//@Scheduled(fixedDelay = 86400000)
	public void crawl() throws InterruptedException {

		WebDriver driver = null;

		try{

			log.info("do crawling ~ ");

			driver = super.setWebDriver(options,"ppomppu");
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

			log.info("find element");

			int loop = CommunityConfig.loop;

			for(int i=4; i<4+loop; i++){
				extractElement(driver,i);
				Thread.sleep(500);
			}

		}
		catch (Exception e){

			// 1. Webdriver 예외 처리.
			log.error(e.getMessage());
			Sentry.captureException(e);

		}
		finally {

			if (driver != null) {

				driver.quit();// 다 하고 무조건 자원 반납.
				Thread.sleep(3000); // 의도적인 컨텍스트 스위칭 유발로, 다른 스레드 작업 처리를 위한 목적.
				log.info("successfully shut driver");
			}

		}


	}

	public void extractElement(WebDriver driver, int i) {

		// body > div.wrapper > div.contents > div.container > div > div.board_box > table > tbody > tr:nth-child(4)
		// body > div.wrapper > div.contents > div.container > div > div.board_box > table > tbody > tr:nth-child(5)

		try {
			//#revolution_main_table > tbody > tr:nth-child(11) > td:nth-child(2) > img.baseList-img
			WebElement e = driver.findElement(By.cssSelector("body > div.wrapper > div.contents > div.container > div > div.board_box > table > tbody > tr:nth-child("+i+")"));

			WebElement imageElement = e.findElement(By.cssSelector("td.baseList-space.title > a > img"));
			String image = imageElement.getAttribute("src") != null ? imageElement.getAttribute("src") : null;

			String url = e.findElement(By.cssSelector("td.baseList-space.title > a")).getAttribute("href");

			String title = e.findElement(By.cssSelector("td.baseList-space.title > div > div > a:nth-child(2)")).getText();

			String beforeTime = e.findElement(By.cssSelector("td:nth-child(5)")).getText();

			// time 가공
			LocalDateTime now = LocalDateTime.now().withNano(0);

			// beforeTime 파싱 (hh:mm:ss)
			String[] parts = beforeTime.split(":");
			int hh = Integer.parseInt(parts[0]);
			int mm = Integer.parseInt(parts[1]);
			int ss = Integer.parseInt(parts[2]);

			// 날짜는 today 유지, 시간만 교체
			LocalDateTime localDateTime = now
				.withHour(hh)
				.withMinute(mm)
				.withSecond(ss);


			// time 가공 - 연원일만 그대로 유지
			/* Legacy
			LocalDateTime now = LocalDateTime.now().withNano(0);

			int hour = now.getHour();
			int minute = now.getMinute();
			int second = now.getSecond();

			String hourMinuteSec = String.format("%02d:%02d:%02d", hour, minute, second);

			String updatedTimeString = now.toString().replace(hourMinuteSec, beforeTime);

			LocalDateTime localDateTime = LocalDateTime.parse(updatedTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

			 */

			Media ppomPu = Media.builder()
				.title(title)
				.url(url)
				.src(image)
				.category("사회")
				.media("뽐뿌")
				.type("community")
				.count(0)
				.createdAt(localDateTime)
				.build();

				mediaRepository.save(ppomPu);


		}
		catch (Exception e){

			log.error(e.getMessage());

			String errorPage = driver.getPageSource();
			log.error(errorPage);

			Sentry.captureException(e);
		}

	}

}
