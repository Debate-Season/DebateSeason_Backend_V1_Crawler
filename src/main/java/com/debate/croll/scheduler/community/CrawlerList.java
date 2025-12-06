package com.debate.croll.scheduler.community;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.debate.croll.scheduler.community.template.AbstractCommunityCrawl;
import com.debate.croll.scheduler.community.type.BobaeDream;
import com.debate.croll.scheduler.community.type.Clien;
import com.debate.croll.scheduler.community.type.FmKorea;
import com.debate.croll.scheduler.community.type.HumorUniv;
import com.debate.croll.scheduler.community.type.MlbPark;
import com.debate.croll.scheduler.community.type.Ppompu;
import com.debate.croll.scheduler.community.type.RuliWeb;
import com.debate.croll.scheduler.community.type.TodayHumor;
import com.debate.croll.scheduler.news.News;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrawlerList {

	// 커뮤니티 목록
	private final BobaeDream bobaeDream;
	private final Clien clien;
	private final FmKorea fmKorea;
	private final MlbPark mlbPark;
	private final RuliWeb ruliWeb;
	private final TodayHumor todayHumor;

	private final HumorUniv humorUniv;
	private final Ppompu ppompu;

	// 뉴스 목록
	private final News news;


	// 이거는 CrawlerRecoveryManager도 쓰기 때문에, CommunityList에서 초기화 -> Mananger로 주입하는 전략. null 회피.
	public static List<AbstractCommunityCrawl> list = new ArrayList<>();

	public static News NewsForReboot;

	// 초기화
	@PostConstruct
	public void init(){

		list.add(bobaeDream);
		list.add(clien);
		list.add(fmKorea);
		list.add(mlbPark);
		list.add(ruliWeb);
		list.add(todayHumor);

		//list.add(ppompu); //네트워크 연결 문제 -> 직접 주입
		//list.add(humorUniv); //WAF -> 직접 주입

		NewsForReboot = news;

	}

}
