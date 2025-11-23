package com.debate.croll.legacy.youtubelive.infrastructure;

import com.debate.croll.legacy.youtubelive.domain.YoutubeLiveDto;

public interface YoutubeLiveRepository {

	// 1.유튜브 라이브 저장
	void save(YoutubeLiveDto youtubeLiveDto);

	// 2. category에 맞게 데이터 가져오기
	YoutubeLiveEntity fetch(String category);

}
