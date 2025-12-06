package com.debate.croll.scheduler.manager;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.debate.croll.scheduler.common.Status;
import com.debate.croll.scheduler.common.Type;
import com.debate.croll.scheduler.community.CrawlerList;
import com.debate.croll.scheduler.community.template.AbstractCommunityCrawl;
import com.debate.croll.scheduler.community.url.NewsList;
import com.debate.croll.scheduler.news.News;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CrawlerRecoveryManager {


	// 1. 최신 파일 1개 불러오기. 이는 복구를 하기 위함이다.
	private static String getRecentFile(){
		File dir = new File("C:\\crawler-logs");
		//File dir = new File("/home/tarto123z/crawler-logs");

		File latestFile = null;

		if(dir.exists() && dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null && files.length > 0) {
				latestFile = Arrays.stream(files)
					.filter(File::isFile)
					.max(Comparator.comparingLong(File::lastModified))
					.orElse(null);
			}
		}

		if (latestFile != null) {
			log.info("Latest file: " + latestFile.getName());

			String fileName = latestFile.getName();

			return fileName.substring(0, fileName.lastIndexOf("."));
		} else {
			log.error("No file found."); // 없는 것도 처리해야 함.
			return null;
		}
	}

	private static String[] splitFileName(String fileName){

		String[] parts = fileName.split("-");

		return parts;
	}

	// 2. 1.에서 가져온 정보를 파일 이름을 바탕으로, 크롤링 시작하기
	public static void rebootCrawl() {

		List<AbstractCommunityCrawl> crawlerList = CrawlerList.list;

		// 복구를 위해, 최신 파일 1개 불러오기
		String fileName = getRecentFile();

		// -를 기준으로 파일이름 쪼개기.
		// Type이 Community인 경우
		/*
			parts[0] = "Reboot"
			parts[1] = "BobaeDream"
			parts[2] = "4" -> point
			parts[3] = "Community"

			----------

			parts[0] = "Reboot"
			parts[1] = "조선일보"
			parts[2] = "1" ->  point
			parts[3] = "News"
			parts[4] = "101" -> category
		 */
		String[] parts = splitFileName(Objects.requireNonNull(fileName));

		String type = parts[3];


		// News를 위한 변수
		News news = CrawlerList.NewsForReboot;
		List<String> pressNameList = NewsList.keys;
		LinkedHashMap<String,String> linkedNewsList = NewsList.newsList;
		List<Integer> categoryList = NewsList.category;

		// 목표 포인트.
		int targetIndex = 0;

		try{

			if(type.equals(Type.Community.name())){ // 커뮤니티 크롤링 도중에 끊기면, Community에서 끝까지 시작을 한다.

				for(AbstractCommunityCrawl e : crawlerList){

					if(!e.getCommunityName().equals(parts[1])){
						targetIndex++;
					}
					else{
						break;
					}
				}

				// 1개만 리부트를 하고,
				AbstractCommunityCrawl rebootCrawler = crawlerList.get(targetIndex);
				rebootCrawler.crawl(Status.Reboot,Integer.parseInt(parts[2])+1);

				Thread.sleep(15000); // cool down

				// 나머지는 정상적으로 시작을 함.
				int size = crawlerList.size();

				for(int i=targetIndex+1; i<size; i++){
					AbstractCommunityCrawl crawler= crawlerList.get(i);
					crawler.crawl(Status.Steady,-1);

					Thread.sleep(20000); // cool down
				}

				Thread.sleep(20000); // cool down

				// 2. 뉴스 크롤링
				log.info("Start News Crawling ~ ");
				
				for(String pressName : pressNameList) {

					String url = linkedNewsList.get(pressName);

					for (Integer i : categoryList) {

						news.crawl(Status.Steady,url,pressName,i,-1);
						Thread.sleep(1500);

					}
				}

			}
			else{ // 뉴스 크롤링 도중에 끊기면, 뉴스에서 시작을 한다.

				String pressNameForReboot = parts[1]; // "조선일보"
				int point = Integer.parseInt(parts[2])+1; // "1"
				int category = Integer.parseInt(parts[4]); // "101"



				// 1. 리부트 용도의 뉴스
				String urlForReboot = linkedNewsList.get(pressNameForReboot);

				// 그 카테고리만 크롤링 하고,
				news.crawl(Status.Reboot,urlForReboot,pressNameForReboot,category,point);

				// 나머지는 그 부분에서 이어서 진행을 한다.
				for(Integer c : categoryList){

					if(c > category){
						news.crawl(Status.Steady,urlForReboot,pressNameForReboot,c,-1);
					}

				}

				// 2. 이어서 가져오기.

				// 시작 포인트 찾기.
				for(String s : pressNameList){
					if (s.equals(pressNameForReboot)) {
						break;
					}
					else{
						targetIndex++;
					}
				}

				// 3. targetIndex에서 데이터 긁어오기.
				for(int a=targetIndex+1; a<pressNameList.size(); a++){

					String pressName = pressNameList.get(a);

					String url = linkedNewsList.get(pressName);

					for (Integer i : categoryList) {

						news.crawl(Status.Steady,url,pressName,i,-1);
						Thread.sleep(1500);

					}

				}




			}



		}
		catch (Exception e){

			log.error("fail to reboot : "+e.getMessage());

		}

	}
}
