package com.batch.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.batch.common.CommonTaskAction;
import com.batch.service.DailyQTService;
import com.batch.service.DaumRealRankService;
import com.batch.service.NaverRealRankService;

/**
 * CrawlingTaskAction
 * 
 * @author applepie1130
 */
public class CrawlingTaskAction {
	
	private static final Logger logger = LoggerFactory.getLogger(CrawlingTaskAction.class);

	/**
	 * 메인 메소드
	 * 
	 * @author	applepie1130
	 * @param	args
	 */
	public static void main(String[] args) {
		List<Class> services = new ArrayList<Class>();
		
		services.add(NaverRealRankService.class);
		services.add(DaumRealRankService.class);
		services.add(DailyQTService.class);
		
		CommonTaskAction.run(services, args);
	}
}
