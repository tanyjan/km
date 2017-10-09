package com.test.proxy;

public interface JedisService {
	public void test(String str);

	public default void test() {
		System.out.println("test");
	}
}
