package com.test.func;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ModuleConfig;
import com.alibaba.dubbo.config.MonitorConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("spring.dubbo")
@Component
public class DubboProperties {
	static final String targetName = "spring.dubbo";
	private ApplicationConfig application;
	private RegistryConfig registry = new RegistryConfig();
	private ModuleConfig module;
	private MonitorConfig monitor;
	private String basePackage;
	private ProviderConfig provider;
	private ConsumerConfig consumer;
	private ProtocolConfig protocol;
	private List<ProtocolConfig> protocols;
	private List<ServiceConfig<?>> services;
	private List<ReferenceConfig<?>> references;

	public DubboProperties() {
		this.registry.setClient("zkclient");
		this.registry.setAddress("127.0.0.1");
		this.registry.setPort(Integer.valueOf(2181));
		this.registry.setProtocol("zookeeper");
	}

	public ApplicationConfig getApplication() {
		return this.application;
	}

	public void setApplication(ApplicationConfig application) {
		this.application = application;
	}

	public RegistryConfig getRegistry() {
		return this.registry;
	}

	public void setRegistry(RegistryConfig registry) {
		this.registry = registry;
	}

	public ModuleConfig getModule() {
		return this.module;
	}

	public void setModule(ModuleConfig module) {
		this.module = module;
	}

	public MonitorConfig getMonitor() {
		return this.monitor;
	}

	public void setMonitor(MonitorConfig monitor) {
		this.monitor = monitor;
	}

	public String getBasePackage() {
		return this.basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public ProviderConfig getProvider() {
		return this.provider;
	}

	public void setProvider(ProviderConfig provider) {
		this.provider = provider;
	}

	public ConsumerConfig getConsumer() {
		return this.consumer;
	}

	public void setConsumer(ConsumerConfig consumer) {
		this.consumer = consumer;
	}

	public ProtocolConfig getProtocol() {
		return this.protocol;
	}

	public void setProtocol(ProtocolConfig protocol) {
		this.protocol = protocol;
	}

	public List<ProtocolConfig> getProtocols() {
		return this.protocols;
	}

	public void setProtocols(List<ProtocolConfig> protocols) {
		this.protocols = protocols;
	}

	public List<ServiceConfig<?>> getServices() {
		return this.services;
	}

	public void setServices(List<ServiceConfig<?>> services) {
		this.services = services;
	}

	public List<ReferenceConfig<?>> getReferences() {
		return this.references;
	}

	public void setReferences(List<ReferenceConfig<?>> references) {
		this.references = references;
	}
}
