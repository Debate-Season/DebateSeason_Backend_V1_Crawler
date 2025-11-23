package com.debate.croll.test.testController.DTO;

import lombok.Data;

@Data
public class DocumentRequest {
	private Document document;

	public DocumentRequest(Document document){
		this.document = document;
	}

	@Data
	public static class Document {
		private String type;
		private String content;
	}
}
