package com.test.schd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitTest {
	protected final static Logger logger = LoggerFactory.getLogger(InitTest.class);
	public static Map<Integer, List<SourceFeads>> m = new HashMap<Integer, List<SourceFeads>>();
//	public static int[] COUNT = {399, 3227, 20791, 1792, 11785};
	public static int[] COUNT = {0, 0, 0, 0, 0};
	public static List<SourceFeads> list = new ArrayList<>();
	static {
		
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

	public void StartRun() {
		logger.info("create  xpath init redis service started!");

		long StartTime = System.currentTimeMillis();
//		List<SourceFeads> list = MySqlHelper.getXpathCrawlerInterval();
		if (list.size() > 0) {
			logger.info("get xpath seed group by crawler interval the group size is {}", list.size());
		} else {
			logger.info("search xpath  mysql error!");
			return;
		}
		Map<Integer, List<SourceFeads>> map = new LinkedHashMap<>();
		for (SourceFeads listItem : list) {
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
		for(Iterator<Map.Entry<Integer, List<SourceFeads>>> it = map.entrySet().iterator(); it.hasNext();) {
			entry = it.next();
//			RedisHelper.getInstance().hmset(Constants.REDIS_XPATH_HASH_KEY, String.valueOf(entry.getKey()), entry.getValue());
		}
		logger.info(" create xpath redis service end! total recevice the size is:{},use time{}", list.size(),
				(System.currentTimeMillis() - StartTime) / 1000);

		calc(map);
	}

	public static void main(String[] args) {
		new InitTest().StartRun();
	}

	public static int setModel(Map<Integer, List<SourceFeads>> map, int interval, int count, List<SourceFeads> sources, int pos) {
		for (int i = 0; i < interval; i++) {
			for (int j = 0; j < count; j++) {
				pos++;
				sources.get(pos).setLast_push_time(pos+"");
				addSourceFead(map, i, sources.get(pos));
			}
		}
		return pos;
	}

	public static int setOdd(Map<Integer, List<SourceFeads>> map, int interval, int count, List<SourceFeads> sources, int pos) {
		if(count <= 0 || count > interval/2) {
			return pos;
		}
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

	public static int setEven(Map<Integer, List<SourceFeads>> map, int interval, int count, List<SourceFeads> sources, int pos) {
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

	public static void addSourceFead(Map<Integer, List<SourceFeads>> map, int sec, SourceFeads sf) {
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

	static Map<String, String> valid = new HashMap<String, String>();
	public static void calc(Map<Integer, List<SourceFeads>> cmap) {
		int total = 0;
		for(Iterator<Map.Entry<Integer,List<SourceFeads>>> it = cmap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer,List<SourceFeads>> entry = it.next();
			total += entry.getValue().size();
			for (int i = 0; i < entry.getValue().size(); i++) {
				String key =entry.getValue().get(i).getCrawler_interval() + "-" + entry.getValue().get(i).getLast_push_time();
				if(valid.containsKey(key)) {
					System.err.println(key);
				}
				valid.put(key, key);
				System.out.println(key);
			}
		}
		System.out.println(total);
	}

}
