package com.test.func;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

//import com.mongodb.MongoClient;
import com.test.proxy.JedisInvocationHandler;
import com.test.proxy.JedisService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Controller
@RequestMapping("")
public class WebTest {
	static final Logger logger = LoggerFactory.getLogger(WebTest.class);
	@Autowired
	JedisPool pool;
	@Autowired
	JedisService service;
//	@Autowired(required=false)
//	MongoClient client;

	@RequestMapping(value="/test/{key}/{value}/{score}")
	public String test(@PathVariable String key, @PathVariable String value, @PathVariable double score) {
		logger.info("web test...");
		Jedis jedis = pool.getResource();
		try {
			jedis.zadd(key.getBytes(), score, value.getBytes());
			logger.info("{} {} {}", key ,value, score);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null!=jedis)
			jedis.close();
		}
		return "test";
	}

	@RequestMapping(value="/range/{key}/{max}/{min}")
	public String range(@PathVariable String key, @PathVariable long max, @PathVariable long min) {
		logger.info("web test...");
		Jedis jedis = pool.getResource();
		try {
			Set<String> sets = jedis.zrevrangeByScore(key, max, min);
			logger.info("{}", sets);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(null!=jedis)
			jedis.close();
		}
		return "test";
	}

	@RequestMapping("/jedis")
	public String jedis() {
		InvocationHandler handler = new JedisInvocationHandler(service, pool);
		JedisService serviceProxy = (JedisService) Proxy.newProxyInstance(JedisService.class.getClassLoader(), service.getClass().getInterfaces(), handler);
		serviceProxy.test("121112");
		return "test";
	}

//	@RequestMapping("/mongo")
//	public String mongo() {
//		MongoIterable<String> d = client.listDatabaseNames();
//		MongoCollection<Document> docs = client.getDatabase("test").getCollection("test");
//		BasicDBObject b = new BasicDBObject();
//		long c = docs.count(b);
//		logger.info("{}", c);
//		return "test";
//	}
}
