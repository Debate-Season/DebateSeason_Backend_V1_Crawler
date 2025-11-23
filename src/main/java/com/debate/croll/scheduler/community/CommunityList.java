package com.debate.croll.scheduler.community;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.debate.croll.scheduler.community.template.AbstractCommunityCrawl;
import com.debate.croll.scheduler.community.type.BoabaeDream;
import com.debate.croll.scheduler.community.type.Clien;
import com.debate.croll.scheduler.community.type.FmKorea;
import com.debate.croll.scheduler.community.type.HumorUniv;
import com.debate.croll.scheduler.community.type.MlbPark;
import com.debate.croll.scheduler.community.type.Ppompu;
import com.debate.croll.scheduler.community.type.RuliWeb;
import com.debate.croll.scheduler.community.type.TodayHumor;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommunityList {

	private final BoabaeDream boabaeDream;
	private final Clien clien;
	private final FmKorea fmKorea;
	private final HumorUniv humorUniv;
	private final MlbPark mlbPark;
	private final Ppompu ppompu;
	private final RuliWeb ruliWeb;
	private final TodayHumor todayHumor;

	// 초기화
	@PostConstruct
	public void init(){

		list.add(ppompu);
		list.add(boabaeDream);
		list.add(clien);
		list.add(fmKorea);
		list.add(mlbPark);
		list.add(ruliWeb);
		list.add(todayHumor);
		//list.add(humorUniv);

	}

	public static List<AbstractCommunityCrawl> list = new ArrayList<>();


}
