package com.test.schd;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.test.Utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class Test implements Serializable {
	private final static Logger LOGGER = Logger.getLogger(Test.class);
	private static final long serialVersionUID = -1875727803948972125L;

	public static int STANDARD_INTERVAL = 600;
	public static int[] CRAWLER_INTERVAL = {600, 900, 1200, 1800, 3600};
//	public static int[] COUNT = {800, 6500, 42000, 3500, 23000};
//	public static int[] COUNT = {399, 3227, 20791, 1792, 11785};
	public static int[] COUNT = {1000, 5000, 30000, 3000, 2000};
	
	public static LinkedBlockingQueue<Model> queue = new LinkedBlockingQueue<Model>();

	public static HashMap<String, Integer> map = new LinkedHashMap<>();
	public static HashMap<String, List<Model>> tmap = new LinkedHashMap<>();

	public static HashMap<String, Integer> nmap = new LinkedHashMap<>();
	public static HashMap<Integer, List<Model>> smap = new LinkedHashMap<>();

	public static List<String> list = new ArrayList<>();
	public static String KEY = "TEST_";
	public static final String queueName = "test";

	@Autowired
	private JedisPool pool;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	public void main(String[] args) throws IOException {
//		for(int i = 200; i >=0; i--) {
//			rabbitTemplate.receive(queueName, 100);
//		}
		init();
		ExecutorService sendService = Executors.newFixedThreadPool(3);
		ExecutorService receiveService = Executors.newFixedThreadPool(2);

		Jedis jedis = pool.getResource();
		for(Entry<Integer, List<Model>> entry: smap.entrySet()) {
			System.out.println(entry.getKey() + "-" + entry.getValue().size());
			jedis.hset(Utils.serialize(KEY), Utils.serialize(String.valueOf(entry.getKey())), Utils.serialize(entry.getValue()));
		}
		jedis.close();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
//				Gson gson = new Gson();
				Jedis jedis = pool.getResource();
				int i = 1;
				while(i <= CRAWLER_INTERVAL[CRAWLER_INTERVAL.length-1]) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						byte[] data = jedis.hget(Utils.serialize(KEY), Utils.serialize(String.valueOf(i)));
						List<Model> models = Utils.deserialize(data);
						for (int j = 0; j < models.size(); j++) {
//							System.out.println("producer: =============>"+models.get(j));
							SendMQ send = new SendMQ(models.get(j));
							sendService.execute(send);
//							rabbitTemplate.convertAndSend(queueName, gson.toJson(models.get(j)));
//							RmqHelper.publishMessage(queueName, gson.toJson(models.get(j)));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					i++;
					if(i==CRAWLER_INTERVAL[CRAWLER_INTERVAL.length-1]) {
						i=1;
					}
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				ReceiveMQ receive1 = new ReceiveMQ();
				receiveService.execute(receive1);
				ReceiveMQ receive2 = new ReceiveMQ();
				receiveService.execute(receive2);
			}
		}).start();

//		int s = 0;
//		for (int i = 0; i < COUNT.length; i++) {
//			s += (CRAWLER_INTERVAL[CRAWLER_INTERVAL.length-1] / CRAWLER_INTERVAL[i]) * COUNT[i];;
//		}
//		System.out.println(s);
	}

	public void init() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long current = System.currentTimeMillis();
		String key;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(current));
		for (int i = 0; i < CRAWLER_INTERVAL[CRAWLER_INTERVAL.length-1]; i++) {
			calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND)+1);
			key = format.format(calendar.getTime());
			tmap.put(key, new ArrayList<Model>());
			nmap.put(key, i+1);
			smap.put(i+1, new ArrayList<Model>());
			System.out.println(key);
			list.add(key);
		}
		System.out.println("==================================");
		for (int i = 0; i < CRAWLER_INTERVAL.length; i++) {
			int crawlerInterval = CRAWLER_INTERVAL[i];
			int counts = COUNT[i];
			calc(format, current, crawlerInterval, counts);
		}
	}

	public void calc(SimpleDateFormat format, long current, int crawlerInterval, int counts) {
		String key;
		Model model;
		Test t = new Test();
//		long j = current + 1000;
		long j = (current / 1000) * 1000 + 1000;
		int num = CRAWLER_INTERVAL[CRAWLER_INTERVAL.length-1]/crawlerInterval;
		while(num > 0) {
			for (int i = 0; i < counts; i++) {
				model = t.new Model();
				long addTime = (crawlerInterval * 1000) / counts;
				j = j + addTime;
				if(i==counts-1) {
					key = format.format(new Date((j/1000) * 1000 - 1000));
				}else {
					key = format.format(new Date(j));
				}
				model.setInterval(crawlerInterval);
				model.setMillions(j);
				model.setPushTime(key);
				queue.add(model);
				if(map.containsKey(key)) {
					map.put(key, map.get(key)+1);
				}else {
					map.put(key, 1);
				}
				try {
					tmap.get(key).add(model);
					smap.get(nmap.get(key)).add(model);
				} catch (Exception e) {
					e.printStackTrace();
				}
//				System.out.println(key);
			}
			num--;
		}
//		System.out.println(tmap);
//		for (int i = 0; i < CRAWLER_INTERVAL.length; i++) {
//			long current = cur - STANDARD_INTERVAL * 1000 + 60 * 1000;
//			long addTime = (STANDARD_INTERVAL * 1000) / COUNT[i];
//			System.out.println(addTime);
//			String key;
//			for (int j = 0; j < COUNT[i]; j++) {
//				key = format.get().format(new Date(current + (j * addTime)));
//				if(map.contains(key)) {
//					map.put(key, map.get(key)+1);
//				}else {
//					map.put(key, 1);
//				}
//				System.out.println(key + "-" + map.get(key));
////				System.out.println(CRAWLER_INTERVAL[i] + "," + (j+1) + "," + format.format(new Date(current + (j * addTime))));
//			}
//		}
	}

	class Model implements Serializable {
		private static final long serialVersionUID = -844657858860743071L;
		int interval;
		long millions;
		String pushTime;
		String content;

		public int getInterval() {
			return interval;
		}
		public void setInterval(int interval) {
			this.interval = interval;
		}
		public long getMillions() {
			return millions;
		}
		public void setMillions(long millions) {
			this.millions = millions;
		}
		public String getPushTime() {
			return pushTime;
		}
		public void setPushTime(String pushTime) {
			this.pushTime = pushTime;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}

	}

	class SendMQ implements Runnable {
		Model model;
		public SendMQ(Model model) {
			this.model = model;
		}

		@Override
		public void run() {
			long before = System.currentTimeMillis();
			rabbitTemplate.convertAndSend(queueName, model);
			LOGGER.info("R>>>>>>>>>>" + new Gson().toJson(model));
			long after = System.currentTimeMillis();
			if(after-before > 100l) {
				System.out.println("================================================"+(after-before));
			}else if(after-before > 50l) {
				System.out.println("================================================"+(after-before));
			}else if(after-before > 20l) {
				System.out.println(after-before);
			}
		}
	}

	class ReceiveMQ implements Runnable {
		public ReceiveMQ() {
		}

		public void run() {
			while(true) {
				try {
					double d = Math.random();
					if(d <= 0.01d) {
						Thread.sleep(30l);
					}
					Model model = (Model) rabbitTemplate.receiveAndConvert(queueName);
					if(null==model) {
						continue;
					}
					LOGGER.info("S<<<<<<<<<<" + new Gson().toJson(model));
				} catch (AmqpException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
}
