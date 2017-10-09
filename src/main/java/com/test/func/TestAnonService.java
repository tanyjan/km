package com.test.func;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.test.schd.consume.Consume;
import com.test.schd.init.InitializeXpathRedisTask;
import com.test.schd.init.StartService;
import com.test.schd.prod.GetXpathFromRedisTask;

@Service
@PropertySource(value = { "classpath:application.properties" })
public class TestAnonService {
	@Autowired
	private Environment env;
//	@Autowired
//	private com.test.schd.Test test;
//	private static int counts = 1;


//	@Autowired
//	private AmqpAdmin admin;
//	@Autowired
//	private RabbitTemplate template;
	@Autowired
	private InitializeXpathRedisTask initTask;
	@Autowired
	private GetXpathFromRedisTask getTask;
	@Autowired
	private Consume consumefromMQ;

	public void testAnonService() {
		System.out.println("testAnonService..."+ env.getProperty("port"));
		//初始化
		initTask.init();
		initTask.StartRun();

		this.run(getTask);
		this.run(consumefromMQ);

		/*ExecutorService service = Executors.newFixedThreadPool(2);
		int pos = 0;
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			service.execute(new Test(pos));
			pos++;
		}*/
		
//		MBeanServer server = MBeanServerFactory.createMBeanServer();
//		try {
//			server.registerMBean(consumefromMQ, new ObjectName("Server:hostName=machineA,portNumber=8080"));
//		} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException
//				| MalformedObjectNameException e) {
//			e.printStackTrace();
//		}
		/*try {
			test.main(null);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
//		while(true) {
//			this.consume();
//		}
		
//		admin.declareQueue(new Queue(RabbitMQTest.routingKey, false, false, false));
//		ExecutorService service = Executors.newFixedThreadPool(5);
//		for (int i = 0; i < 1000; i++) {
//			testRabbitMQ(service);
//		}
	}

	/*public void testRabbitMQ(ExecutorService service) {
		SourceFeads model = new SourceFeads();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String key = format.format(new Date());
		model.setInterval(900);
		model.setMillions(counts++);
		model.setPushTime(key);
		service.execute(new RabbitMQTest(template, model));
	}

	public void consume() {
		SourceFeads model = (SourceFeads) template.receiveAndConvert(RabbitMQTest.routingKey);
		System.out.println(model.getMillions());
	}*/

	/*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/

	public void test(int pos) {
		if(Math.random() < 0.1)
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(pos);
	}
	class Test implements Runnable {
		private int pos;
		public Test(int pos) {
			this.pos = pos;
		}
		@Override
		public void run() {
			test(pos);
		}
	}

	public void run(StartService service) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				service.StartRun();
			}
		}).start();;
	}

	/*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/
	@Autowired
	private ServiceConfig<StartService> service;
	@Autowired
	private ApplicationConfig applicationConfig;
	@Autowired
	private ProviderConfig providerConfig;
	@Autowired
	private RegistryConfig registryConfig;
	@Autowired
	private ReferenceConfig<StartService> referenceConfig;
	public void dubboTest() {
		service.setProvider(providerConfig);
		service.setApplication(applicationConfig);
		service.setInterface(StartService.class);
		service.setRegistry(registryConfig);
		service.setRef(consumefromMQ);
		service.export();

		referenceConfig.setApplication(applicationConfig);
		referenceConfig.setInterface(StartService.class);
		StartService s = referenceConfig.get();
		System.out.println(s);
	}
}
