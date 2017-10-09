package com.test.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisInvocationHandler implements InvocationHandler {

	private Object target;
	private JedisPool pool;
	public JedisInvocationHandler() {
		super();
	}

	public JedisInvocationHandler(Object target, JedisPool pool) {
		this.target = target;
		this.pool = pool;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Jedis jedis = pool.getResource();
		Object obj = method.invoke(target, args);
		jedis.close();
		return obj;
	}

}
