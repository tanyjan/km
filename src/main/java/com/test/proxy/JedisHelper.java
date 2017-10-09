package com.test.proxy;

import org.springframework.stereotype.Service;

@Service
public class JedisHelper implements JedisService {
	public JedisHelper() {
		
	}

	@Override
	public void test(String str) {
		System.out.println(str);
	}
}
