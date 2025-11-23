package com.debate.croll.test.testController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

class Data{
	Set<String> intersection;
	double accurate ;
}

// 불필요한 단어들을 모은 자료구조.
class UseLessWordList{
	public static Set<String> wordSet = new HashSet<>();

}

@Slf4j
@Component
public class Filter {

	HashMap<String, Set<String>> keywordMap; // 키워드를 저장하는 저장소.

	public static Data jaccardSimilarity(String[] listA, String[] listB) {

		Set<String> setA = new HashSet<>(List.of(listA));
		Set<String> setB = new HashSet<>(List.of(listB));

		// 교집합 크기
		Set<String> intersection = new HashSet<>(setA);
		intersection.retainAll(setB);

		// 합집합 크기
		Set<String> union = new HashSet<>(setA);
		union.addAll(setB);

		Data d = new Data();
		d.intersection = intersection;
		d.accurate = (double) intersection.size() / union.size();

		return d;
	}

	public void refresh_keyWordMap(){

		keywordMap=null;
	}

	public HashMap<String, Set<String>> doCategorize(String [] filterdNews){

		keywordMap = new HashMap<>();

		int size = filterdNews.length;
		int [] markingList = new int[size];

		Set<String> elseList = new HashSet<>();

		for(int i=0; i<size; i++){

			for(int j=i+1; j<size; j++){

				Data d = jaccardSimilarity(filterdNews[i].split(" "),filterdNews[j].split(" "));

				if(d.accurate>0){

					Set<String> set = d.intersection;

					for(String s : set){

						if(keywordMap.get(s)==null){
							// 새로 추가를 한다.
							Set<String> set2 = new HashSet<>();
							set2.add(filterdNews[i]);
							set2.add(filterdNews[j]);

							markingList[i] = -1;
							markingList[j] = -1;

							keywordMap.put(s,set2);

							elseList.add(filterdNews[i]);
							elseList.add(filterdNews[j]);
						}
						else{
							// 이미 있는 노드다.
							Set<String> set2 = keywordMap.get(s);
							set2.add(filterdNews[j]);

							markingList[j] = -1;

							elseList.add(filterdNews[j]);
						}


					}

				}
			}
		}


		// 나머지 추가
		for(int i=0 ; i<size; i++){
			if(markingList[i]!=-1){
				elseList.add(filterdNews[i]);
			}
		}

		keywordMap.put("etc",elseList);

		// 전처리를 한다.
		preProcessing_keyWords();



		return keywordMap;

	}

	// 전처리를 이용해서 불필요한 노드들은 사전에 삭제해버리자.
	public void preProcessing_keyWords(){

		Set<String> useLessWords = UseLessWordList.wordSet;

		for(String s : useLessWords){
			keywordMap.remove(s);
		}
	}

	public HashMap<String, Set<String>> postProcessing_keyWords(Map<String,String> keyWords){

		Set<String> keySet = keyWords.keySet();

		for(String k : keySet){

			String status = keyWords.get(k);

			try{
				// 1. 불용 키워드라면,
				if(status.equals("부정")){

					if(!keywordMap.get(k).isEmpty()){ // 불용 키워드가 있다면, keyWordMap에 삭제 및 학습
						
						keywordMap.remove(k);
						UseLessWordList.wordSet.add(k); // 불용 키워드 학습.
					}

				}

				// 2. 쓸모있는 키워드라면, -> 나중에 유사어로 묶을듯.

			}
			catch (NullPointerException e){ // 불용 키워드가 이미 삭제 되었다면, by-pass.
				log.warn("카테고리 ["+k+"]는 이미 삭제되었습니다.");

			}
			catch (Exception e){ // 나머지 알 수 없는 오류에 대한 에러 처리.
				log.error(e.getMessage());

			}

		}

		// 3. 이거 발생하면, 제대로 작동 안한다는 것을 의미함.
		return keywordMap;

	}

}
