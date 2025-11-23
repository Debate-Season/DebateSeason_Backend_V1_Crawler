package com.debate.croll.legacy.youtubelive.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter//?
@AllArgsConstructor
@Builder
public class YoutubeLive {
	private Integer id;

	private String title;

	private String supplier;

	private String videoId;

	private String category;

	private LocalDateTime createAt;

	private String src;

	// DTO 객체를 만든다.
	// 도메인 엔티티 그대로 넘겼는데, 속성값을 바꾸는 것이라면???, 또한 아직 도메인 엔티티 그대로 써야한다면???
	public YoutubeLiveDto createDto() {

		// 새로운 객체 생성.
		return YoutubeLiveDto.builder()
			.id(id)
			.title(title)
			.supplier(supplier)
			.videoId(videoId)
			.category(category)
			.createAt(createAt)
			.src(src)
			.build()
			;

	}




}
