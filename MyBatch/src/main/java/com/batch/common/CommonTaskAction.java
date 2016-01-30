package com.batch.common;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class CommonTaskAction extends QuartzJobBean {
	
	private static List<Class> services = null;
	
	public static <T> void run(List<Class> servicesPrm, String...args) {
		// 스케쥴링을 위한 QaurtzContext
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("scheduleContext.xml");
		context.registerShutdownHook();
		
		services = servicesPrm;
	}
	
	/**
	 * @see	Qaurtz 실행함수 정의
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		context.registerShutdownHook();
		
		ThreadPoolTaskExecutor taskExecutor = null;
		
		try {
			// // [S] ThreadPoolTaskExecutor Configuration
			taskExecutor = (ThreadPoolTaskExecutor) context.getBean("taskExecutor");
			taskExecutor.setCorePoolSize(services.size());
			taskExecutor.setMaxPoolSize(services.size() * 2);
			taskExecutor.setQueueCapacity(taskExecutor.getCorePoolSize() * taskExecutor.getMaxPoolSize() * 2);
			taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
			taskExecutor.initialize();
			// [E] TestExecutor 설정
			
			// Thread Run!!
			for ( int i=0; i<services.size(); i++ ) {
				Class<?> service = services.get(i);
				Runnable runner = (Runnable) context.getBean(service);
				taskExecutor.execute(runner);
			}
		} finally {
			taskExecutor.shutdown();
			context.close();
		}
	}
}