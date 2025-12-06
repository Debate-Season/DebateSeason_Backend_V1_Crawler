package com.debate.croll.scheduler;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.debate.croll.scheduler.common.Status;
import com.debate.croll.scheduler.community.CrawlerList;
import com.debate.croll.scheduler.community.template.AbstractCommunityCrawl;
import com.debate.croll.scheduler.community.url.NewsList;
import com.debate.croll.scheduler.news.News;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class Scheduler {

	private final News news;
	private final NewsList newsList;
	//@Scheduled(initialDelay = 15000,fixedDelay = 86400000)
	//@Scheduled(cron = "0 0 17 * * ?",zone = "Asia/Seoul")
	@Scheduled(cron = "0 18 13 * * ?", zone = "Asia/Seoul")
	public void crawl(){

		try{
			// 1. 커뮤니티 크롤링
			log.info("Start Community Crawling ~ ");
			List<AbstractCommunityCrawl> list = CrawlerList.list;

			for(AbstractCommunityCrawl e : list){

				e.crawl(Status.Steady,-1); // 정상적인 작동을 하므로, Steady라고 주입을 한다.

				Thread.sleep(5000); // 네트워크 폭주를 방지하기 위한 설정.

			}

			// Cool down
			Thread.sleep(10000);

			// 2. 뉴스 크롤링
			log.info("Start News Crawling ~ ");

			List<String> keys = NewsList.keys;
			LinkedHashMap<String,String> linkedNewsList = NewsList.newsList;
			List<Integer> category = NewsList.category;

			for(String pressName : keys) {

				String url = linkedNewsList.get(pressName);

				for (Integer i : category) {

					news.crawl(Status.Steady,url,pressName,i,-1);
					Thread.sleep(1500);

				}
			}



			//news.crawl(Status.Steady,null,-1);

		}
		catch (Exception e){

			String msg = "Scheduler.crawl() : "+e.getMessage();

			log.error(msg);
			Exception exception = new Exception(msg);

			Sentry.captureException(exception);

		}

	}

}
