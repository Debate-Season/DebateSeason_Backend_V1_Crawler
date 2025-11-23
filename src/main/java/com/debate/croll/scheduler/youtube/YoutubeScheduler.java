package com.debate.croll.scheduler.youtube;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.debate.croll.domain.entity.Media;
import com.debate.croll.repository.MediaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class YoutubeScheduler {

	private final MediaRepository mediaRepository;
	private final ChromeOptions options;
	//@Scheduled(cron = "0 0 17 * * ?",zone = "Asia/Seoul")
	//@Scheduled(fixedDelay = 60000000)
	public void doCroll() throws InterruptedException {

		Thread.sleep(5000);

		LocalDateTime now = LocalDateTime.now().withNano(0);// 현재 크롤링 시간

		WebDriver driver = new ChromeDriver(options);
		driver.get("https://www.youtube.com/feed/trending");


		// #thumbnail > yt-image > img

		// 컨테이너 통째로 가져오는 법 찾아 볼 것
		List<WebElement> elementList = driver.findElements(By.cssSelector("#grid-container"));


		//System.out.println(elementList.get(0).getText());//findElement(By.cssSelector("#thumbnail > yt-image > img")).getAttribute("src"));
		// #thumbnail > yt-image > img
		WebElement targetWebElement = elementList.get(1);

		List<WebElement> tmpList = targetWebElement.findElements(By.cssSelector("#grid-container > ytd-video-renderer"));

		int count = 0;

		try{
			// #thumbnail
			// #thumbnail > yt-image > img
			for(WebElement e: tmpList){

				if(count>=5){
					break;
				}

				String title = e.findElement(By.cssSelector("#video-title")).getText();
				String url = e.findElement(By.cssSelector("#thumbnail")).getAttribute("href");

				/*
				String src = e.findElement(By.cssSelector("#thumbnail > yt-image > img")).getAttribute("src");
				System.out.println("scr: "+src);

				 */
				WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", e);

				String src = wait.until(driver2 -> {
					try {
						WebElement img = e.findElement(By.cssSelector("#thumbnail > yt-image > img"));
						String s = img.getAttribute("src");
						return (s != null && !s.isEmpty()) ? s : null;
					} catch (Exception ex) {
						return null;
					}
				});

				String media = e.findElement(By.cssSelector("#text > a")).getText();


				Media youtube = Media.builder()
					.title(title)
					.url(url)
					.src(src)
					.category("사회")
					.media(media)
					.type("youtube")
					.count(0)
					.createdAt(now)
					.build()
					;
				mediaRepository.save(youtube);

				count++;

				Thread.sleep(1000);
			}

		}
		catch (Exception e){
			log.info("YoutubeShcheduler 예외 발생");
			throw new RuntimeException(e);
		}
		finally {
			Thread.sleep(15000); // 15s에 1번씩 가져옴.
			driver.quit();
		}

	}
}
