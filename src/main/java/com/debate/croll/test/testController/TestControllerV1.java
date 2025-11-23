package com.debate.croll.test.testController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.debate.croll.domain.entity.mapper.MediaKeywordDto;
import com.debate.croll.test.testService.KeyWordRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Controller
public class TestControllerV1 {

	private final KeyWordRepository keyWordRepository;
	private final TestServiceV1 testServiceV1;

	@GetMapping("/")
	public String getKeywords(){

		//filter.getFilterdData();

		return "index";
	}

	@GetMapping("/insert")
	public void insertData(){

		// 1. Google Natural API를 이용한 필터링된 데이터.
		// 여기선 불필요한 조사는 다 지운 상태
		String [] filteredNews = {
			"교착 미 관세 협상 도요타 현대차",
			"가성비 경쟁 현대 기아 차 북미시장 한 일 격차",
			"여한구 통상 교섭본부장 방미 3 천 500 억달러 투자 세부 조율",
			"尹 내란 재판 10 회 연속 불출석 궐석재판 진행",
			"尹 내란재판 9회 불출석…중계 관련 신청 시점 검토중",
			"북측 적대 존중 체제 통일 행위 흡수 李대통령",
			"李대통령 대통령 국장 복귀 지능",
			"李대통령 2030 청년 타운홀 미팅 목소리"
		};


		// filteredNews의 상태값을 저장한다.
		// DB 저장시, state는 undone으로 저장을 한다.
		/*
		for(String s : filteredNews){
			Test_tmpNewsContainerV1.filteredNewsState.put("undone",s);
		}

		 */

	}

	/*
	@GetMapping("/croll")
	public String doCroll(Model model){

		// DB에서 undone인 값만 불러온다.
		String [] filteredNews = {
			"교착 미 관세 협상 도요타 현대차",
			"가성비 경쟁 현대 기아 차 북미시장 한 일 격차",
			"여한구 통상 교섭본부장 방미 3 천 500 억달러 투자 세부 조율",
			"尹 내란 재판 10 회 연속 불출석 궐석재판 진행",
			"尹 내란재판 9회 불출석…중계 관련 신청 시점 검토중",
			"북측 적대 존중 체제 통일 행위 흡수 李대통령",
			"李대통령 대통령 국장 복귀 지능",
			"李대통령 2030 청년 타운홀 미팅 목소리"
		};


		// 여기는 알람을 받은 후, ADMIN이 직접 관리를 한다.
		HashMap<String, Set<String>> getCateogorizedMap = testServiceV1.categorizeNews(filteredNews);
		model.addAttribute("map",getCateogorizedMap);

		return "test";
	}

	 */

	@GetMapping("/api/v1/keywords/unprocessed")
	public String getKeywords_unprocessed(Model model){

		List<MediaKeywordDto> keywordList = keyWordRepository.findMediaWithUnprocessedKeywords();

		HashMap<String,List<String>> keyWordMediaMap = new HashMap<>();

		for(MediaKeywordDto m : keywordList){

			String keyword = m.getName();
			String mediaTitle = m.getTitle();

			if (keyWordMediaMap.get(keyword)==null) { // 그 keyword가 없다면,

				// 새로 만들어서 저장을 한다.
				keyWordMediaMap.put(keyword, new ArrayList<>(
					Arrays.asList(mediaTitle)));
			}
			else{ // keyword가 이미 있다면, 추가를 한다.

				List<String> mediaSet = keyWordMediaMap.get(keyword);
				mediaSet.add(mediaTitle);
			}
		}

		model.addAttribute("map",keyWordMediaMap);

		return "test";


	}

}

