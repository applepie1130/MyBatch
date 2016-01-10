package com.batch.common;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class CommonTaskAction {
	
	public static <T> void run(List<Class> services, String... args) {
		
		ClassPathXmlApplicationContext context = null;
		ThreadPoolTaskExecutor taskExecutor = null;
		
		try {
			context = new ClassPathXmlApplicationContext("applicationContext.xml");
			context.registerShutdownHook();
			
			taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
			
			// ThreadPoolTaskExecutor Configuration
			taskExecutor.setCorePoolSize(services.size());
			taskExecutor.setMaxPoolSize(services.size() * 2);
			taskExecutor.setQueueCapacity(taskExecutor.getCorePoolSize() * taskExecutor.getMaxPoolSize() * 2);
			taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
			taskExecutor.initialize();
			
			// Thread Run!!
			for ( int i=0; i<services.size(); i++ ) {
				Class<?> service = services.get(i);
				Runnable runner = (Runnable) context.getBean(service);
				taskExecutor.execute(runner);
			}
			
		} finally {
			// All Close
			taskExecutor.shutdown();
			context.close();
		}
	}
}