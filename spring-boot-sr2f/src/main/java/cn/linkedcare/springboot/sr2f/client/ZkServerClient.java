package cn.linkedcare.springboot.sr2f.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.linkedcare.springboot.sr2f.config.Sr2fConfig;
import cn.linkedcare.springboot.sr2f.dto.ServerDto;
import lombok.extern.slf4j.Slf4j;

/**
 * zk客户端,监听数据的变化
 * @author wl
 *
 */
@Slf4j
public class ZkServerClient {


	private List<IServerClient> serversClients = new ArrayList<IServerClient>();
	
	
	public ZkServerClient(String path,List<IServerClient> serversClients){
		this.serversClients = serversClients;
		
		init(path);
	}

	

	
	private void notifyChange(CuratorFramework client,String path){
		try {
			List<String> datas = client.getChildren().forPath(path);
			
			List<ServerDto> servers = new ArrayList<ServerDto>();
			for(String data:datas){
				servers.add(JSON.parseObject(data,ServerDto.class));
			}
			
	    	for(IServerClient  l:serversClients){
	    		l.changeNotify(servers);
	    		
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("exception:",e);
			throw new RuntimeException(e);
		}
    	
	}
    /**
     * 增加子节点变化的监控
     */
    private void addDataChangeListener(CuratorFramework client,String path){
   	    PathChildrenCache cache = new PathChildrenCache(client,path, false);  
        try {
			cache.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}  
//         注册监听  
        cache.getListenable().addListener(new PathChildrenCacheListener() {  
            
            public void childEvent(CuratorFramework client,  
                    PathChildrenCacheEvent event) throws Exception {  
                switch (event.getType()) {  
                	case CHILD_ADDED: {  
                		notifyChange(client,path);
                		break;  
                	}
                	case CHILD_REMOVED: {  
                    	notifyChange(client,path);
                		break;  
                	}  
                }  
      
            }  
        });  
    }
	
	public void init(String path){

		CuratorFramework client = CuratorFrameworkFactory.builder().connectString(Sr2fConfig.getZkUrl())  
                .sessionTimeoutMs(60000)  
                .retryPolicy(new RetryNTimes(Integer.MAX_VALUE,10000)).build();  
        // 客户端注册监听，进行连接配置  
        client.start();  
        //阻塞到服务连接上
        try {
			client.blockUntilConnected(2,TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
    
        addDataChangeListener(client,path);
	}




}
