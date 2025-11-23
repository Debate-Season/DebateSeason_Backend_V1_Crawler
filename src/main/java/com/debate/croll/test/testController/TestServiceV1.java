package com.debate.croll.test.testController;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.debate.croll.domain.entity.KeyWordState;
import com.debate.croll.domain.entity.Keyword;
import com.debate.croll.test.testService.KeyWordRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TestServiceV1 {

	private final KeyWordRepository keyWordRepository;
	private final TestFilterV1 testFilterV1;

	/*
	private final Filter filter;

	public HashMap<String, Set<String>> categorizeNews(String [] filteredNews){

		// 1. 들어온 News 데이터들을 key별로 분류화를 시킨다.
		return filter.doCategorize(filteredNews);

	}

	 */

	// DirtyChecking
	@Transactional
	public String updateKeyword(Map<String,String> keywordsWithOpinion){

		for(String s : keywordsWithOpinion.keySet()){

			// keyword 엔티티
			Keyword keyword = keyWordRepository.findByName(s);

			if (keywordsWithOpinion.get(s).equals("positive")) {
				keyword.setState(KeyWordState.VALID);
			}
			else {
				// 부정으로 평가를 받은 단어만 필터에 저장을 한다.
				testFilterV1.put(s);
				keyword.setState(KeyWordState.INVALID);

			}

		}

		return "Well Done";

	}

}
