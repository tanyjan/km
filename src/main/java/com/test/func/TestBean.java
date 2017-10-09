package com.test.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBean implements Runnable, Service {
	static final Logger logger = LoggerFactory.getLogger(TestBean.class);
	TestService testService;


	public TestBean(TestService testService) {
		this.testService = testService;
//		new Thread(this).start();
		testService.test();
	}

	@Override
	public void run() {
		logger.info("run...");
		try {
			while(true) {
				try {
					Thread.sleep(1000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//			System.out.println("run...");
				testService.test();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
		logger.info("run finished...");
	}

}
