package com.test.schd.consume;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.schd.init.ConsumeMBean;
import com.test.schd.init.StartService;
import com.test.schd.prod.GetXpathFromRedisTask;

@com.alibaba.dubbo.config.annotation.Service(version="1.0.1")
@Service
public class Consume implements StartService, ConsumeMBean {
	protected final static Logger logger = LoggerFactory.getLogger(GetXpathFromRedisTask.class);

	@Autowired
	private RabbitTemplate template;

	public void StartRun() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					sleep();
					long start = System.currentTimeMillis();
					template.receiveAndConvert(GetXpathFromRedisTask.CRAWLER_SEED_LIST_PAGE);
					logger.info("get data from mq cost: " + (System.currentTimeMillis() - start) + "ms");
				}
			}
		}).start();
	}

	public void sleep() {
		try {
			long timeInMillions;
			double r = Math.random();
			if(r<=0.001) {
				timeInMillions = 10;
			}else if(r>0.001 && r<=0.01) {
				timeInMillions = 200;
			}else if(r>0.01&&r<=0.1) {
				timeInMillions = 150;
			}else {
				timeInMillions = this.timeInMillions;
			}
			Thread.sleep(timeInMillions);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private int timeInMillions = 10;
	@Override
	public void setSleep(int timeInMillions) {
		this.timeInMillions = timeInMillions;
	}
}
