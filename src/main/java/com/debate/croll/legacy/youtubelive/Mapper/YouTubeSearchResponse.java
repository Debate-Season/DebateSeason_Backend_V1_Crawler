package com.debate.croll.legacy.youtubelive.Mapper;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YouTubeSearchResponse {
	private String kind;
	private String etag;
	private String nextPageToken;
	private String regionCode;
	private PageInfo pageInfo;
	private List<Item> items;

	// Getters and setters
}

@Getter
@Setter
class PageInfo {
	private int totalResults;
	private int resultsPerPage;

	// Getters and setters
}

