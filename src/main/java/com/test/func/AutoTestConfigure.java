package com.test.func;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class AutoTestConfigure {

	@ConditionalOnClass(TestBean.class)
	@Bean
	public TestBean testBean(TestService testService) {
		return new TestBean(testService);
	}

	@Bean
	public EmbeddedServletContainerFactory tomcat() {
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		return tomcat;
	}

//	@ConditionalOnProperty(prefix="mongodb", value={"host", "port"})
//	@Bean
//	public MongoClient mongodb(Environment env) {
//		MongoClient client = null;
//		try {
//			String host = env.getProperty("mongodb.host");
//			int port = env.getProperty("mongodb.port", Integer.class, 27017);
//			client = new MongoClient(host, port);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return client;
//	}

	@ConditionalOnProperty(prefix="redis", value={"host", "port"})
	@Bean
	public JedisPool jedis(Environment env) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(env.getProperty("redis.maxTotal", Integer.class, 10));
		config.setMaxWaitMillis(env.getProperty("redis.maxWaitMillis", Integer.class, 100000));
		String host = env.getProperty("redis.host");
		int port = env.getProperty("redis.port", Integer.class, 6379);
		JedisPool pool = new JedisPool(config, host, port, 10000);
		return pool;
	}
}
