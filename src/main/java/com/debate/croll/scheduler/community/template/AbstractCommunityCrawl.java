package com.debate.croll.scheduler.community.template;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.debate.croll.scheduler.common.Status;
import com.debate.croll.scheduler.community.url.CommunityUrlList;

public abstract class AbstractCommunityCrawl { // 클래스의 일관성을 유지하기 위함.


	// 커뮤니티 이름 반환 하기.
	public abstract String getCommunityName();

	// 이거는 커뮤니티마다 세부 내용이 다 다르다.
	// state는 1) 정상 or 2) 리부트인지에 따라 다르다.
	public abstract void crawl(Status status,int point) throws InterruptedException;

	public abstract void extractElement(WebDriver driver,int i); // 실제 웹 브라우저에 접근해서 element를 가져온다.

	// 초기화 방법은 항상 동일하므로, public 메소드로 설정
	public WebDriver setWebDriver(ChromeOptions options, String name){

		String url = CommunityUrlList.getUrl(name); // readOnly이기 때문에 thread-safe핟.
		WebDriver driver = new ChromeDriver(options);

		driver.get(url);

		return driver;

	}

}
