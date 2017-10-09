package com.test.schd.prod;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.Utils;
import com.test.schd.SourceFeads;
import com.test.schd.init.InitializeXpathRedisTask;
import com.test.schd.init.StartService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class GetXpathFromRedisTask implements StartService {
	protected final static Logger logger = LoggerFactory.getLogger(GetXpathFromRedisTask.class);

	@Autowired
	private RabbitTemplate template;
	@Autowired
	private JedisPool pool;

	protected long startTime = System.currentTimeMillis();

	public void StartRun() {
		logger.info("Start get xpath from redis then push to mq");
		int count = 0;
		while (true) {
			sleep(1000);
			Jedis jedis = pool.getResource();
			try {
				byte[] bytes = jedis.hget(Utils.serialize(InitializeXpathRedisTask.REDIS_XPATH_HASH_KEY), Utils.serialize(0+""));
				List<SourceFeads> list = Utils.deserialize(bytes);
				sendSoureFeedToMQ(list);
				System.out.println(">>>>>>>>>>>>>>>>>>" + count);
				count++;
			} catch (Exception e) {
				logger.error("the main thread error{}", e);
				continue;
			}finally {
				jedis.close();
			}
			if(count==3600-1) {
				count = 0;
			}
			if(count==0) {
				long distance = (System.currentTimeMillis() - startTime) / 1000;
				logger.info("loop into the head---use time:{}", distance);
			}
		}

	}

	protected void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			logger.info("the program stoped because of{}", e.toString());
			e.printStackTrace();
		}
	}

	public void sendSoureFeedToMQ(List<SourceFeads> list) {
		long start = System.currentTimeMillis();
		for (SourceFeads sourceFeads : list) {
			template.convertAndSend(CRAWLER_SEED_LIST_PAGE, sourceFeads);
		}
		logger.info("send data to mq cost: " + (System.currentTimeMillis()-start) +"/" + list.size() + "* ms");
	}
	public final static String CRAWLER_SEED_LIST_PAGE = "CRAWLER_SEED_LIST_PAGE";
}
