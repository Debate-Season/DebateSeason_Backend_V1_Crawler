package com.debate.croll.legacy.youtubelive.infrastructure;

import java.time.LocalDateTime;

import com.debate.croll.legacy.youtubelive.domain.YoutubeLiveDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "youtube_live")
public class YoutubeLiveEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "title", columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
	private String title;

	@Column(name = "supplier")
	private String supplier;

	@Column(name = "video_id")
	private String videoId;

	@Column(name = "category")
	private String category;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "src")
	private String scr;

	// 1. 도메인 -> JPA엔티티로
	// 공유 메소드이므로, static으로 등록. 객체는 아님.
	public static YoutubeLiveEntity toJpaEntity(YoutubeLiveDto youtubeLiveDto){

		return
		YoutubeLiveEntity.builder()
			.title(youtubeLiveDto.getTitle())
			.supplier(youtubeLiveDto.getSupplier())
			.videoId(youtubeLiveDto.getVideoId())
			.category(youtubeLiveDto.getCategory())
			.createdAt(youtubeLiveDto.getCreateAt())
			.build()
			;

	}
}
