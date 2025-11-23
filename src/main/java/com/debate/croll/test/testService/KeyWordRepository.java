package com.debate.croll.test.testService;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.debate.croll.domain.entity.KeyWordState;
import com.debate.croll.domain.entity.Keyword;
import com.debate.croll.domain.entity.mapper.MediaKeywordDto;

@Repository
public interface KeyWordRepository extends JpaRepository<Keyword,Long> {

	// 1.keyword를 반환한다.
	@Query(value = "SELECT name FROM keyword WHERE NAME =:name", nativeQuery = true)
	String findKeyWord(@Param("name") String name);

	// 2. 키워드 이름으로 가져오기
	Keyword findByName(String name);


	// 3. 키워드가 처리가 되지 않은 미디어들을 가져온다.
	@Query(value = "SELECT m.title, m2.name FROM media m\n"
		+ "\tINNER JOIN \n"
		+ "\t( SELECT mk.media_id,k.name FROM (SELECT id,name FROM Keyword WHERE state IS NULL) k\n"
		+ "\t\tINNER JOIN media_keyword mk\n"
		+ "\t\tON k.id = mk.keyword_id ) m2\n"
		+ "\tON m.id = m2.media_id", nativeQuery = true)
	List<MediaKeywordDto> findMediaWithUnprocessedKeywords();

	// 4. 서버 다운 시, 필터 복구를 위한 용도.( 키워드만 가져온다. )
	List<Keyword> findByState(KeyWordState state);

}
