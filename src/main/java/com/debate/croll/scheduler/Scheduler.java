package com.debate.croll.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.debate.croll.scheduler.community.CommunityList;
import com.debate.croll.scheduler.community.template.AbstractCommunityCrawl;
import com.debate.croll.scheduler.news.News;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component("CommunityScheduler")
public class Scheduler {

	private final News news;

	//@Scheduled(fixedDelay = 86400000)
	@Scheduled(cron = "0 0 17 * * ?",zone = "Asia/Seoul")
	public void crawl(){

		try{

			// 1. 커뮤니티 크롤링
			log.info("Start Community Crawling ~ ");

			List<AbstractCommunityCrawl> list = CommunityList.list;

			for(AbstractCommunityCrawl e : list){

				e.crawl();
				Thread.sleep(5000); // 네트워크 폭주를 방지하기 위한 설정.

			}

			// 2. 뉴스 크롤링
			Thread.sleep(10000); // Cool down
			log.info("Start News Crawling ~ ");
			news.crawl();


		}
		catch (Exception e){
			log.error(e.getMessage());
			Sentry.captureException(e);
		}

	}

}
