package com.test.func;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {
	@Autowired
	private TestAnonService service;

	public void test() {
		service.testAnonService();
	}
}
