package com.debate.croll.legacy.youtubelive.infrastructure;

import org.springframework.stereotype.Repository;

import com.debate.croll.legacy.youtubelive.domain.YoutubeLiveDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class YoutubeLiveImpl implements YoutubeLiveRepository{

	private final YoutubeLiveJpaRepository youtubeLiveJpaRepository;
	
	// 1. 유튜브 라이브 저장하기
	@Override
	public void save(YoutubeLiveDto youtubeLiveDto) {
		YoutubeLiveEntity youtubeLiveJpaEntity = YoutubeLiveEntity.builder()
			// .id는 채번하는데 왜햠?
			.title(youtubeLiveDto.getTitle())
			.title(youtubeLiveDto.getTitle())
			.supplier(youtubeLiveDto.getSupplier())
			.videoId(youtubeLiveDto.getVideoId())
			.category(youtubeLiveDto.getCategory())
			.createdAt(youtubeLiveDto.getCreateAt())
			.scr(youtubeLiveDto.getSrc())
			.build()
			;
		youtubeLiveJpaRepository.save(youtubeLiveJpaEntity);
	}

	@Override
	public YoutubeLiveEntity fetch(String category) {
		YoutubeLiveEntity youtubeLiveEntity = youtubeLiveJpaRepository.findByCategory(category);

		// null일 경우는 없지만, null이면 알려야 한다.
		if(youtubeLiveEntity==null){

			// null이라면, 해당 카테고리의 데이터가 없음 -> 새로 넣어줘야 한다.
			return null;
		}
		return youtubeLiveEntity;
	}


}
