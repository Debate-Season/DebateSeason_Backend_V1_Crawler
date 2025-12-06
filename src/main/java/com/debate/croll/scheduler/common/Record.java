package com.debate.croll.scheduler.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

// 싱글톤에 기록을 하자.

@Slf4j
@Getter
@Setter
public class Record {

	private String name;
	private int point;
	private Type type;
	private String category="";
	
	// 1. Community 경우
	public Record(String name,int point,Type type){
		this.name=name; // 엠엘비파크
		this.point=point; // 2
		this.type=type; // Community


	}

	// 2. News인 경우
	public Record(String name,int category,int point,Type type){
		this.name=name; // 조선일보
		this.category=String.valueOf(category); // 101
		this.point=point; // 2
		this.type=type; // News
	}

	// ex) Reboot-Clien-1-Community
	// 이를 .txt로 저장을 한 다음에, 리부트를 할 경우, 불러와서 다음 꺼를 실행을 해야하는데...
	@Override
	public String toString() {
		// ex) 커뮤니티 : Reboot-BobaeDream-3(point)-Community
		// ex) 뉴스    : Reboot-조선일보-2(point)-News-101

		if(this.type.name().equals("Community")){
			return Status.Reboot+"-"+this.name+"-"+this.point + "-" +type.name();
		}
		else{
			return Status.Reboot+"-"+this.name+"-"+this.point + "-" +type.name()+"-"+this.category;
		}


	}

	public void recordFile(){

		// 굳이 내용을 쓸 필요가 있을까? 어차피 제목에 쓰면 되는데...
		String name = this +".txt";

		File file = new File("C:\\crawler-logs\\"+name);
		//File file = new File("/home/tarto123z/crawler-logs/"+name);

		try {
			if(file.createNewFile()){
				log.info("create file : "+name);
			}
			else{

				// 이미 있는 파일이라면, 날짜만 최신화만 시키면 된다. -> 날짜만 바꾸기 때문에, 파일에 접근하지 않아 비용이 저렴하다.
				Path source = Paths.get("C:\\crawler-logs\\"+name);
				Files.setLastModifiedTime(source, FileTime.fromMillis(System.currentTimeMillis()));

				log.warn("file already exists : "+name);
			}

		}
		catch (IOException e){
			log.error("fail to create file : "+name);
			log.error(e.getMessage());
		}

	}

}
