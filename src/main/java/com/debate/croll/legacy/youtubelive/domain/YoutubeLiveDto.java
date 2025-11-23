package com.debate.croll.legacy.youtubelive.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// Setter가 들어가는 순간 바뀔 수 있다.
// 그래서 도메인 엔티티에게 모든 책임을 위임한다.
@Getter
@AllArgsConstructor
@Builder
public class YoutubeLiveDto {

	private final Integer id;

	private final String title;

	private final String supplier;

	private final String videoId;

	private final String category;

	private final LocalDateTime createAt;

	private final String src;

}
