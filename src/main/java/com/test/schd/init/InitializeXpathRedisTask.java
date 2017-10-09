package com.test.schd.init;


import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.Utils;
import com.test.schd.SourceFeads;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class InitializeXpathRedisTask implements StartService {
	protected final static Logger logger = LoggerFactory.getLogger(InitializeXpathRedisTask.class);

	public Map<Integer, List<SourceFeads>> m = new HashMap<Integer, List<SourceFeads>>();
	public int[] COUNT = {399, 3227, 20791, 1792, 11785};
//	public static int[] COUNT = {111, 0, 0, 0, 0};
	public List<SourceFeads> list = new ArrayList<>();

	public void init() {
		SourceFeads e;
		e = new SourceFeads();
		e.setCrawler_interval(600);
		list.add(e);
		e = new SourceFeads();
		e.setCrawler_interval(900);
		list.add(e);
		e = new SourceFeads();
		e.setCrawler_interval(1200);
		list.add(e);
		e = new SourceFeads();
		e.setCrawler_interval(1800);
		list.add(e);
		e = new SourceFeads();
		e.setCrawler_interval(3600);
		list.add(e);
		
		List<SourceFeads> l;
		l = new ArrayList<SourceFeads>();
		for (int i = 0; i < COUNT[0]; i++) {
			SourceFeads sf = new SourceFeads();
			sf.setCrawler_interval(600);
			l.add(sf);
		}
		m.put(600, l);

		l = new ArrayList<SourceFeads>();
		for (int i = 0; i < COUNT[1]; i++) {
			SourceFeads sf = new SourceFeads();
			sf.setCrawler_interval(900);
			l.add(sf);
		}
		m.put(900, l);

		l = new ArrayList<SourceFeads>();
		for (int i = 0; i < COUNT[2]; i++) {
			SourceFeads sf = new SourceFeads();
			sf.setCrawler_interval(1200);
			l.add(sf);
		}
		m.put(1200, l);

		l = new ArrayList<SourceFeads>();
		for (int i = 0; i < COUNT[3]; i++) {
			SourceFeads sf = new SourceFeads();
			sf.setCrawler_interval(1800);
			l.add(sf);
		}
		m.put(1800, l);

		l = new ArrayList<SourceFeads>();
		for (int i = 0; i < COUNT[4]; i++) {
			SourceFeads sf = new SourceFeads();
			sf.setCrawler_interval(3600);
			l.add(sf);
		}
		m.put(3600, l);
	}

	@Autowired
	private RabbitTemplate template;
	@Autowired
	private JedisPool pool;
	public static final String REDIS_XPATH_HASH_KEY = "inveno:redis:xpath:beijing:hash";
	
	public void StartRun() {
		logger.info("create  xpath init redis service started!");

		long StartTime = System.currentTimeMillis();
//		List<SourceFeads> list = MySqlHelper.getXpathCrawlerInterval();
		if (list.size() > 0) {
			initRedis();
			logger.info("get xpath seed group by crawler interval the group size is {}", list.size());
		} else {
			logger.info("search xpath  mysql error!");
			return;
		}
		Map<Integer, Map<Integer, List<SourceFeads>>> crawlerMap = new LinkedHashMap<>();
		for (SourceFeads listItem : list) {
			Map<Integer, List<SourceFeads>> map = new LinkedHashMap<>();
			crawlerMap.put(listItem.getCrawler_interval(), map);
			// List<SourceFeads> sources =
			// MySqlHelper.getXpathSourceFeeds(listItem.getCrawler_interval());
//			List<SourceFeads> sources = MySqlHelper.getXpathSourceFeedsExtend(listItem.getCrawler_interval());
			List<SourceFeads> sources = m.get(listItem.getCrawler_interval());
			if (sources.size() > 0) {
				logger.info("the xpath  interval is {} ,the size of this interval is{}", listItem.getCrawler_interval(),
						sources.size());
				/*long current = System.currentTimeMillis() - listItem.getCrawler_interval() * 1000 + 60 * 1000;
				long addTime = (listItem.getCrawler_interval() * 1000) / sources.size();
				for (int i = 0; i < sources.size(); i++) {
					SourceFeads feed = sources.get(i);
					if (feed.getSpider_status() == 0) {
						feed.setLast_push_time(current + (i * addTime) + "");
						RedisHelper.getInstance().hmset(Constants.REDIS_XPATH_HASH_KEY, String.valueOf(feed.getId()),
								feed);
						RedisHelper.getInstance().rPush(Constants.REDIS_XPATH_LIST_KEY, String.valueOf(feed.getId()));
					} else {
						continue;
					}

				}*/

				int count = sources.size();
				int interval = listItem.getCrawler_interval();

				int pos = -1;
//				5 6
				if(count < interval) {
					int half = interval / 2;
//					5 > 3
					if(count > half) {
						pos = setOdd(map, interval, half, sources, pos);
						pos = setEven(map, interval, count - half, sources, pos);
					}else {
						pos = setOdd(map, interval, count, sources, pos);
					}
					continue;
				}

				int ent = count / interval;
				pos = setModel(map, interval, ent, sources, pos);
				int mod = count % interval;
				if(mod <= interval) {
					int half = interval / 2;
					if(mod > half){
						pos = setOdd(map, interval, half, sources, pos);
						pos = setEven(map, interval, mod - half, sources, pos);
					}else {
						pos = setEven(map, interval, mod, sources, pos);
					}
				}
//					RedisHelper.getInstance().hmset(Constants.REDIS_XPATH_HASH_KEY, String.valueOf(feed.getId()), feed);
//					RedisHelper.getInstance().rPush(Constants.REDIS_XPATH_LIST_KEY, String.valueOf(feed.getId()));
			}
		}
		Map.Entry<Integer, List<SourceFeads>> entry;
		int maxInterval = list.get(list.size()-1).getCrawler_interval();
		Map<Integer, List<SourceFeads>> resMap = crawlerMap.get(maxInterval);
		List<SourceFeads> sfList;
		for (int i = 0; i < list.size(); i++) {
			SourceFeads listItem = list.get(i);
			if(listItem.getCrawler_interval()==maxInterval) {
				continue;
			}
			for (Iterator<Map.Entry<Integer, List<SourceFeads>>> it = resMap.entrySet().iterator(); it.hasNext();) {
				entry = it.next();
				sfList = entry.getValue();
				List<SourceFeads> cpList = crawlerMap.get(listItem.getCrawler_interval()).get(entry.getKey()%listItem.getCrawler_interval());
				if(null==cpList) {
					continue;
				}
				System.out.println(entry.getKey() + " " + listItem.getCrawler_interval());
				sfList.addAll(cpList);
			}
		}
		Jedis jedis = pool.getResource();
		for (Iterator<Map.Entry<Integer, List<SourceFeads>>> crawlerMapIt = resMap.entrySet().iterator(); crawlerMapIt.hasNext();) {
			entry = crawlerMapIt.next();
			jedis.hset(com.test.Utils.serialize(REDIS_XPATH_HASH_KEY), com.test.Utils.serialize(String.valueOf(entry.getKey())), com.test.Utils.serialize(entry.getValue()));
//			RedisHelper.getInstance().hmset(Constants.REDIS_XPATH_HASH_KEY, String.valueOf(entry.getKey()), entry.getValue());
			logger.info(" create xpath redis service end! total recevice the size is:{},use time{}", list.size(),
					(System.currentTimeMillis() - StartTime) / 1000);
	
		}
		jedis.close();
//		calc(resMap);
//		getall();	
	}

	public void initRedis() {
		Jedis jedis = pool.getResource();
		jedis.del(com.test.Utils.serialize(REDIS_XPATH_HASH_KEY));
		jedis.close();
	}

	public int setModel(Map<Integer, List<SourceFeads>> map, int interval, int count, List<SourceFeads> sources, int pos) {
		for (int i = 0; i < interval; i++) {
			for (int j = 0; j < count; j++) {
				pos++;
				sources.get(pos).setLast_push_time(pos+"");
				addSourceFead(map, i, sources.get(pos));
			}
		}
		return pos;
	}

	public int setOdd(Map<Integer, List<SourceFeads>> map, int interval, int count, List<SourceFeads> sources, int pos) {
		for (int i = 0; i < interval; i++) {
			if(count<=0) {
				break;
			}
			i++;
			try {
				pos++;
				sources.get(pos).setLast_push_time(pos+"");
				addSourceFead(map, i, sources.get(pos));
			} catch (Exception e) {
				e.printStackTrace();
			}
			count--;
		}
		return pos;
	}

	public int setEven(Map<Integer, List<SourceFeads>> map, int interval, int count, List<SourceFeads> sources, int pos) {
		for (int i = 0; i < interval; i++) {
			if(count<=0) {
				break;
			}
			pos++;
			sources.get(pos).setLast_push_time(pos+"");
			addSourceFead(map, i, sources.get(pos));
			count--;
			i++;
		}
		return pos;
	}

	public void addSourceFead(Map<Integer, List<SourceFeads>> map, int sec, SourceFeads sf) {
		List<SourceFeads> list;
		if(null==map.get(sec)) {
			list = new ArrayList<SourceFeads>();
			map.put(sec, list);
		}else {
			list = map.get(sec);
		}
//		long current = System.currentTimeMillis();
//		sf.setLast_push_time(current+"");
		list.add(sf);
		System.out.println(sf.getCrawler_interval() + "  " + sf.getLast_push_time());
	}

	Map<String, String> valid = new HashMap<String, String>();
	Map<String, Integer> validCount = new HashMap<String, Integer>();
	public void calc(Map<Integer, List<SourceFeads>> cmap) {
		Jedis jedis = pool.getResource();
		int total = 0;
		for(Iterator<Map.Entry<Integer,List<SourceFeads>>> it = cmap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer,List<SourceFeads>> entry = it.next();
			total += entry.getValue().size();
			jedis.hset(Utils.serialize(REDIS_XPATH_HASH_KEY), Utils.serialize(entry.getKey()+""), Utils.serialize(entry.getValue()));
			for (int i = 0; i < entry.getValue().size(); i++) {
				String key =entry.getValue().get(i).getCrawler_interval() + "-" + entry.getValue().get(i).getLast_push_time();
				if(valid.containsKey(key)) {
					System.err.println(key);
					validCount.put(key, validCount.get(key).intValue()+1);
					continue;
				}
				valid.put(key, key);
				validCount.put(key, 1);
				System.out.println(key);
			}
		}
		System.out.println(total);
	}

	@SuppressWarnings("unchecked")
	public void getall() {
		Jedis jedis = pool.getResource();
		int i = 0;
		try {
			int total = 0;
			while(i<3600) {
				byte[] bytes = jedis.hget(Utils.serialize(REDIS_XPATH_HASH_KEY), Utils.serialize(i+""));
				ByteArrayInputStream in = new ByteArrayInputStream(bytes);
				ObjectInputStream ois = new ObjectInputStream(in);
				List<SourceFeads> list = (List<SourceFeads>) ois.readObject();
				for (int j = 0; j < list.size(); j++) {
					String key =list.get(j).getCrawler_interval() + "-" + list.get(j).getLast_push_time();
					System.out.println(key);
				}
				total += list.size();
				if(list.size()<20) {
					System.out.println(list.size());
				}
				System.out.println(list.size());
				i++;
			}
			System.out.println(total);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			jedis.close();
		}
		System.out.println(399 * 3600/600 + 3227 * 3600/900 + 20791 * 3600/1200 + 1792 * 3600/1800 + 11785 * 3600/3600);
		
		for(Iterator<Entry<String, Integer>> it = validCount.entrySet().iterator(); it.hasNext();) {
			Entry<String, Integer> entry = it.next();
			if(entry.getKey().startsWith("600-")) {
				if(entry.getValue().intValue()!=6) {
					System.err.println(entry.getKey() + " " + entry.getValue());
				}else {
					continue;
				}
			}else if(entry.getKey().startsWith("900-")) {
				if(entry.getValue().intValue()!=4) {
					System.err.println(entry.getKey() + " " + entry.getValue());
				}else {
					continue;
				}
			}else if(entry.getKey().startsWith("1200-")) {
				if(entry.getValue().intValue()!=3) {
					System.err.println(entry.getKey() + " " + entry.getValue());
				}else {
					continue;
				}
			}else if(entry.getKey().startsWith("1800-")) {
				if(entry.getValue().intValue()!=2) {
					System.err.println(entry.getKey() + " " + entry.getValue());
				}else {
					continue;
				}
			}if(entry.getKey().startsWith("3600-")) {
				if(entry.getValue().intValue()!=1) {
					System.err.println(entry.getKey() + " " + entry.getValue());
				}else {
					continue;
				}
			}else {
				System.err.println("");
			}
		}
		System.out.println("");
	}
}
