package com.debate.croll.legacy.youtubelive.Mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Thumbnails {
	@JsonProperty("default")
	private Thumbnail defaultThumbnail; // "default" is a reserved word in Java
	private Thumbnail medium;
	private Thumbnail high;

	// Getters and setters
}
