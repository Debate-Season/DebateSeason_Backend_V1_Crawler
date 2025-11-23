package com.debate.croll.legacy.youtubelive.Mapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Snippet {
	private String publishedAt;
	private String channelId;
	private String title;
	private String description;

	private Thumbnails thumbnails;

	private String channelTitle;
	private String liveBroadcastContent;
	private String publishTime;

	// Getters and setters
}
