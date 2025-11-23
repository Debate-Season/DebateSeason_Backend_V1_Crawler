package com.debate.croll.legacy.youtubelive.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeLiveJpaRepository extends JpaRepository<YoutubeLiveEntity,Integer> {

	// 1.category가 일치하는 엔티티 하나를 가져옴
	YoutubeLiveEntity findByCategory(String category);
	
}
