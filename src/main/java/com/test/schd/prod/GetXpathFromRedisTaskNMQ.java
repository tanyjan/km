package com.test.schd.prod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.Utils;
import com.test.schd.Request;
import com.test.schd.SourceFeads;
import com.test.schd.init.InitializeXpathRedisTask;
import com.test.schd.init.StartService;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class GetXpathFromRedisTaskNMQ implements StartService {
	protected final static Logger logger = LoggerFactory.getLogger(GetXpathFromRedisTask.class);

	@Autowired
	private JedisPool pool;

	protected long startTime = System.currentTimeMillis();

	public static int rate = 0;
	public void StartRun() {
		logger.info("Start get xpath from redis then push to mq");
		int count = 0;
		while (true) {
			sleep(1000);
			Jedis jedis = pool.getResource();
			try {
				byte[] bytes = jedis.hget(Utils.serialize(InitializeXpathRedisTask.REDIS_XPATH_HASH_KEY), Utils.serialize(0+""));
				List<SourceFeads> list = Utils.deserialize(bytes);
				for (SourceFeads sourceFeads : list) {
					sourceFeads.setXpath(new String(new byte[1204 * 10]));
					sourceFeads.setRate(rate);
					rate++;
				}
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
				rate = 0;
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
		Jedis jedis = pool.getResource();
		Gson gson = new Gson();
		try {
			for (SourceFeads sourceFeads : list) {
//				template.convertAndSend(CRAWLER_SEED_LIST_PAGE, sourceFeads);
//				jedis.lpush(Utils.serialize(CRAWLER_SEED_LIST_PAGE), Utils.serialize(sourceFeads));
				sourceFeads.setSource_feeds_url("http://baidu.com");
				Map<String, Object> ownExtras = new HashMap<>();
				//2017/08/29 by tanxiang 时间线设置移植到上文方法处理
				sourceFeads.setLast_push_time(System.currentTimeMillis() + "");
		
				String data = gson.toJson(sourceFeads);
				ownExtras = gson.fromJson(data, new TypeToken<Map<String, Object>>() {
				}.getType());
				Request request = new Request(sourceFeads.getSource_feeds_url());
				
				request.setDeep(0);
				request.setExtras(ownExtras);
				jedis.lpush(CRAWLER_SEED_LIST_PAGE, gson.toJson(request));
			}
		} finally {
			jedis.close();
		}
		logger.info("send data to redis cost: " + (System.currentTimeMillis()-start) +"/" + list.size() + "* ms");
	}

	public final static String CRAWLER_SEED_LIST_PAGE = "CRAWLER_SEED_LIST_PAGE";
}
