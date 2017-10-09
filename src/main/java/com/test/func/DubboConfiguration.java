package com.test.func;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;

@Configuration
public class DubboConfiguration {
	@Bean
	public ApplicationConfig applicationConfig(DubboProperties properties) {
		ApplicationConfig application = properties.getApplication();
		return application;
	}

	@Bean
	public RegistryConfig registryConfig(DubboProperties properties) {
		//注册中心
		RegistryConfig registryConfig = properties.getRegistry();
//		registryConfig.setAddress("127.0.0.1:2181");
//		registryConfig.setProtocol("zookeeper");
//		registryConfig.setPort(2183);
//		registryConfig.setCheck(false);
//		registryConfig.setSubscribe(true);
//		registryConfig.setRegister(true);
//		registryConfig.setTimeout(5000);
		return registryConfig;
	}

	@Bean
	public ProtocolConfig protocolConfig(DubboProperties properties) {
		ProtocolConfig protocolConfig = properties.getProtocol();
		return protocolConfig;
	}

	@Bean
	public MonitorConfig monitorConfig(DubboProperties properties) {
		MonitorConfig monitorConfig = properties.getMonitor();
		return monitorConfig;
	}

	@Bean
	public ProviderConfig providerConfig(DubboProperties properties) {
		ProviderConfig providerConfig = properties.getProvider();
		if(null==providerConfig) {
			providerConfig = new ProviderConfig();
			providerConfig.setHost("127.0.0.1");
		}
		return providerConfig;
	}

	@Bean
	public ReferenceConfig<?> referenceConfig(DubboProperties properties) {
		ReferenceConfig<?> referenceConfig = new ReferenceConfig<>();
		return referenceConfig;
	}

	@Bean
	public ServiceConfig<?> serviceConfig(DubboProperties properties) {
		ServiceConfig<?> serviceConfig = new ServiceConfig<>();
		return serviceConfig;
	}

	public void test() {
//		MonitorConfig monitorConfig = new MonitorConfig();
		
//		ProtocolConfig protocolConfig = new ProtocolConfig();
//		ProviderConfig providerConfig = new ProviderConfig();
//		ServiceConfig<StartService> serviceConfig = new ServiceConfig<>();
	}
}
