package com.debate.croll.test.sentryTest;

import org.springframework.stereotype.Component;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SentryScheduler {

	//@Scheduled(fixedDelay = 1000000000)
	public void errorLogTest(){

		try {
			throw new Exception("This is a test.");
		} catch (Exception e) {

			// throw가 발생하면, Sentry.captureException을 통해서, 대시보드로 넘긴다.
			// 그런데, 누가 어디서 언제 왜 어떻게 발생시켰는지에 대한 데이터를 추가해야 한다.
			Sentry.captureException(e);
		}

	}


}
