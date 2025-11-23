package com.debate.croll.test.testController;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Component;

import com.debate.croll.domain.entity.KeyWordState;
import com.debate.croll.domain.entity.Keyword;
import com.debate.croll.test.testService.KeyWordRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class TestFilterV1 { // 싱글톤으로 관리를 한다.

	// 초기화 및 백업 용도.
	private final KeyWordRepository keyWordRepository;

	private final HashMap<String,Integer> filter = new HashMap<>();

	// 초기화 콜백 메소드.( 재시작을 할 경우, DB에 가져온다. )
	@PostConstruct
	public void initFilter(){

		log.info("---< 불용어 필터 활성 >---");

		List<Keyword> list = keyWordRepository.findByState(KeyWordState.INVALID);

		for(Keyword k : list){
			filter.put(k.getName(),1);
		}

	}

	// filter에 불용어 추가하기.
	public void put(String keyword){

		// -1은 filter에 없을 경우 반환을 하는 값이다.
		int value = filter.getOrDefault(keyword,-1);

		if(value==-1){
			// -1은 filter에 없다는 것을 의미한다. 따라서, 새로 추가를 한다.
			filter.put(keyword,1);
		}

	}

	public boolean filtering(String keyword){

		Integer value = filter.get(keyword);

		if(value==null){

			// value가 null이란 말은 필터에 없는 단어를 의미한다.
			return false;

		}
		else if(value==1){

			return true;

		}
		else{

			throw new NoSuchElementException("filter에 등록된 값은 1이나, 해당 값은 [ "+value+" ] 입니다.");

		}

	}

	// 만약 keyword를 삭제하려고 한다면.
	public boolean remove(String keyword){

		Integer value = filter.remove(keyword);

		if(value == null){
			throw new NoSuchElementException("해당 키가 존재하지 않습니다: " +keyword);
		}
		else{
			return true;
		}

	}

}
