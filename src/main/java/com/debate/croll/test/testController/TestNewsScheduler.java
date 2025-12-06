package com.debate.croll.test.testController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.debate.croll.domain.entity.Keyword;
import com.debate.croll.domain.entity.Media;
import com.debate.croll.domain.entity.MediaKeyWord;
import com.debate.croll.repository.MediaRepository;
import com.debate.croll.test.testController.DTO.DocumentRequest;
import com.debate.croll.test.testController.DTO.EntityResponse;
import com.debate.croll.test.testService.KeyWordRepository;
import com.debate.croll.test.testService.MediaKeyWordRepository;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class TestNewsScheduler {

	// @Repository
	private final MediaRepository mediaRepository;
	private final KeyWordRepository keyWordRepository;
	private final MediaKeyWordRepository mediaKeyWordRepository;
	private final Map<String,String> newsList = new HashMap<>();
	private Set<String> keys; // 언론사 이름
	private final List<Integer> category = Arrays.asList(100,101,102,104); // category 번호

	private final ChromeOptions options;

	// 필터
	private final TestFilterV1 testFilterV1;


	
	@PostConstruct
	public void init() { // 의존성 주입 -> 초기화 때 실행

		log.info("NewsScheduler를 초기화를 합니다.");

		newsList.put("한국경제","https://media.naver.com/press/015");
		//newsList.put("매일경제","https://media.naver.com/press/009");
		//newsList.put("한계례","https://media.naver.com/press/028");
		//newsList.put("조선일보","https://media.naver.com/press/023");
		//newsList.put("전자신문","https://media.naver.com/press/030");
		//newsList.put("중앙일보","https://media.naver.com/press/025");
		//newsList.put("연합뉴스","https://media.naver.com/press/422");
		//newsList.put("YTN","https://media.naver.com/press/052");
		//newsList.put("MBC","https://media.naver.com/press/214");
		//newsList.put("SBS","https://media.naver.com/press/055");
		//newsList.put("KBS","https://media.naver.com/press/056");

		keys = newsList.keySet();
	}


	//@Scheduled(cron = "0 0 17 * * ?",zone = "Asia/Seoul")
	//@Scheduled(fixedDelay = 60000000)
	public void doCroll() throws InterruptedException {


		// 공통 Options
		WebDriverManager.chromedriver().setup();

		// 랜덤 User-Agent 리스트
		String[] userAgents = {
			"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36",
		};

		// 랜덤 User-Agent 선택
		Random rand = new Random();
		String userAgent = userAgents[rand.nextInt(userAgents.length)];

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new"); // headless 모드
		options.addArguments("user-agent=" + userAgent); // 랜덤 User-Agent 설정

		// 추가적인 헤더 설정 (필요시)
		options.addArguments("--disable-blink-features=AutomationControlled"); // 이거 false 나와야 자동화 회피 가능하다
		options.addArguments("--disable-gpu"); // GPU 비활성화 (성능 개선)

		for(String s : keys) {

				String url = newsList.get(s);

				for (Integer i : category) {


					WebDriver driver = new ChromeDriver(options); // 매번 새로 생성된 후, 다하고 버려야 한다. -> 일회용
					driver.get(url + "?sid=" + i.toString());

					try {
						for (int j = 1; j <= 2; j++) {

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

							// 3. time
							WebElement timeElement = driver.findElement(By.cssSelector(
								"#ct > div._tab_panel > div:nth-child(1) > ul > li:nth-child(" + j
									+ ") > a > span.press_edit_news_text > span.r_ico_b.r_modify"));
							String outdpated = timeElement.getText();

							// 4. img
							String image = null;
							try {
								WebElement imgElement = driver.findElement(By.cssSelector(
									"#ct > div._tab_panel > div:nth-child(1) > ul > li:nth-child(" + j
										+ ") > a.press_edit_news_link._es_pc_link > span.press_edit_news_thumb > img"));
								image = imgElement.getAttribute("src");
							} catch (NoSuchElementException e) { // 없으면 image는 null이다.

							}

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
							String categoryName = null;
							if (i == 100) {
								categoryName = "정치";
							} else if (i == 101) {
								categoryName = "경제";
							} else if (i == 102) {
								categoryName = "사회";
							} else if (i == 104) {
								categoryName = "세계";
							}

							// 1. 미디어 저장.
							Media news = Media.builder()
								.title(title) // 제목
								.url(href)
								.src(image)
								.category(categoryName)
								.media(s)
								.type("news")
								.count(0)
								.createdAt(time)
								.build();
							mediaRepository.save(news);

							//DocumentRequest.Document d = new DocumentRequest.Document("PLAIN_TEXT","이재명 정부 첫 주중대사에 '노태우 장남' 노재헌 임명");
							//DocumentRequest d2 = new DocumentRequest(d);

							DocumentRequest.Document d = new DocumentRequest.Document();
							d.setType("PLAIN_TEXT");
							d.setContent(title);

							DocumentRequest d2 = new DocumentRequest(d);

							WebClient webClient = WebClient.create("https://language.googleapis.com");
							EntityResponse list = webClient.post()
									 //.header("x-goog-api-key","{Google_API_KEY}")
								     .uri("/v2/documents:analyzeEntities?key={Google_API_KEY}")
								     .contentType(MediaType.APPLICATION_JSON)
								     .bodyValue(d2)
								     .retrieve()
								     .bodyToMono(EntityResponse.class)
								     .block();

							// 2. 키워드 저장.
							List<EntityResponse.Entity> eList = list.getEntities();

							for(EntityResponse.Entity e : eList){

								// NUMBER 타입은 키워드와 전혀 상관이 없다.
								if(e.getType().equals("NUMBER")){
									log.info("filtered, TYPE : NUMBER "+ e.getName());
									continue;
								}

								String name = e.getName();

								// 여기에 필터를 추가해서, 불필요한 키워드라고 판단이 된다면 거르자.
								boolean isFiltered = testFilterV1.filtering(name);
								if(isFiltered){
									log.info("filtered, NAME : "+ name);
									continue;
								}

								Keyword keyword;

								// 중복 키워드는 저장해서는 안된다.
								if(keyWordRepository.findKeyWord(name)!=null){ // 이미 등록된 키워드가 있다면, 그걸 쓰자.

									// DB에 등록된 keyword 불러오기
									keyword = keyWordRepository.findByName(e.getName());

								}
								else{
									keyword = Keyword.builder()
										.name(e.getName())
										.state(null)
										.type(e.getType())
										.build()
										;
									keyWordRepository.save(keyword);
								}

								MediaKeyWord mediaKeyWord = MediaKeyWord.builder()
									.media(news)
									.keyword(keyword)
									.build()
									;
								mediaKeyWordRepository.save(mediaKeyWord);

							}


							Thread.sleep(1000);// 분까지 겹치는 경우를 방지해서 일부러 1초 기다림
						}

					} catch (Exception e) {
						log.info("NewScheduler 예외 발생");
						throw new RuntimeException(e);
					} finally {
						Thread.sleep(5000); // 5s에 1번씩 가져옴.
						driver.quit();
					}

				}

			}


		log.info("done");


	}
}
