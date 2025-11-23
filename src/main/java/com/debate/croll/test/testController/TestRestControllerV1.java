package com.debate.croll.test.testController;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TestRestControllerV1 {

	private final Filter filter;
	private final TestServiceV1 testServiceV1;


	/*
	@PostMapping("/testApi")
	public String postProcessing_keyWords(@RequestBody Map<String,String> keyWords){

		HashMap<String, Set<String>> filteredKeyword =  filter.postProcessing_keyWords(keyWords);

		if(filteredKeyword==null){

			// View로 에러 사인을 전달한다.
			log.error("/testApi : keyWord 후처리 공정 실패, filteredKeyword is null");
			return "Not OK";
		}

		testServiceV1.save(filteredKeyword);


		Set<String> afterWordList = UseLessWordList.wordSet;

		System.out.println("후처리 단어");
		for(String s : afterWordList){
			System.out.println(s);
		}

		// 다 끝나고 나서, done으로 status를 바꾼다.
		return "OK!!!!!";
	}

	 */

	@PostMapping("/testApi")
	public String postProcessing_Keywords(@RequestBody Map<String,String> keyWords){

		// key는 키워드
		return testServiceV1.updateKeyword(keyWords);


	}

}
