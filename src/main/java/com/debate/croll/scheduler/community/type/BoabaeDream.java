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
public class BoabaeDream extends AbstractCommunityCrawl {

	private final MediaRepository mediaRepository;
	private final ChromeOptions options;

	//@Scheduled(fixedDelay = 86400000)
	public void crawl() throws InterruptedException {

		WebDriver driver = null;

		try{

			log.info("do crawling ~ ");

			driver = super.setWebDriver(options,"bobaedream"); // 부모 클래스의 기능을 사용한다.
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

			log.info("find element");

			int loop = CommunityConfig.loop;

			for (int i = 1; i <= loop; i++) { // 총 5회 실행을 하면서, 매번 필요한 요소를 찾는다.
				extractElement(driver,i);
				Thread.sleep(2000); // bot 의심 피하기
			}

		}
		catch (Exception e){
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

	// 가독성을 위해서 분리.
	public void extractElement(WebDriver driver,int i) {

		try{

			WebElement webElement = driver.findElement(
				By.cssSelector("#boardlist > tbody > tr:nth-child(+" + i + ")"));

			WebElement titleElement = webElement.findElement(By.cssSelector("td.pl14 > a.bsubject"));

			String title = titleElement.getText();
			String href = titleElement.getAttribute("href");

			String time = webElement.findElement(By.cssSelector("td.date")).getText();

			LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

			// 문자열에서 시와 분 파싱
			int hour = Integer.parseInt(time.split(":")[0]);
			int minute = Integer.parseInt(time.split(":")[1]);

			// 시:분만 21:42로 덮어쓰기, 초와 나노초는 유지
			LocalDateTime replaced = now.withHour(hour).withMinute(minute);

			Media boBaeDream = Media.builder()
				.title(title)
				.url(href)
				.src(null)
				.category("사회")
				.media("보배드림")
				.type("community")
				.count(0)
				.createdAt(replaced)
				.build();

			mediaRepository.save(boBaeDream);


		}
		catch (Exception e){

			log.error(e.getMessage());

			String errorPage = driver.getPageSource();
			log.error(errorPage);

			Sentry.captureException(e);
		}

	}

}
