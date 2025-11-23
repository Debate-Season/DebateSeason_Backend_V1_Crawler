package com.debate.croll.test.testController.DTO;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Information {

	private String name;
	private String type;
	private Map<String, Object> metadata;
	private List<Mention> mentions;

	@Data
	public static class Mention {
		private Text text;
		private String type;
		private double probability;
	}

	@Data
	public static class Text {
		private String content;
		private int beginOffset;
	}
}
