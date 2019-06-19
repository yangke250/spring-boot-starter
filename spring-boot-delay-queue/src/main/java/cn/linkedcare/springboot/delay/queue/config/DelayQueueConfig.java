package cn.linkedcare.springboot.delay.queue.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;



@Configuration
@PropertySource(
value = {
		"classpath:application-delay-queue.properties",
		"classpath:application-delay-queue-${spring.profiles.active}.properties"},
ignoreResourceNotFound = true, encoding = "UTF-8")
public class DelayQueueConfig {
	private static int partition;
	
	private static String zkUrl;
	
	@Value("${zookeeper.url}")
	public void setZkUrl(String zkUrl){
		DelayQueueConfig.zkUrl = zkUrl;
	}
	
	@Value("${delay.queue.partition}")
	public void setPartition(int partition){
		DelayQueueConfig.partition = partition;
	}

	public static int getPartition() {
		return partition;
	}

	public static String getZkUrl() {
		return zkUrl;
	}
	
	
	
	
}
