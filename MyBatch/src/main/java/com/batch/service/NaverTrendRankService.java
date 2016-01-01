package com.batch.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.batch.common.CommonFileUtil;
import com.batch.configuration.CommonConfiguration;

@Service
public class NaverTrendRankService implements Runnable {
	
	@Autowired
	private CommonConfiguration commonConfig;
	
	@Autowired
	private CommonFileUtil commonFileUtil;
	
	private final Logger logger = LoggerFactory.getLogger("THREAD");
	
	@Override
	public void run() {
		MDC.put("threadLogFileName", this.getClass().getSimpleName());
		
		logger.info("=========== NaverTrendRankService() ===========");
		
		List lsRtnData = new ArrayList<String>();
		List lsTrendURL = new ArrayList<String>();
		
		lsTrendURL.add("http://m.stock.naver.com/api/json/trend/getTrendList.nhn?type=search");		// 검색
		lsTrendURL.add("http://m.stock.naver.com/api/json/trend/getTrendList.nhn?type=news");		// 뉴스
		lsTrendURL.add("http://m.stock.naver.com/api/json/trend/getTrendList.nhn?type=company");	// 증권사
		lsTrendURL.add("http://m.stock.naver.com/api/json/trend/getTrendList.nhn?type=talk");		// 토론
		lsTrendURL.add("http://m.stock.naver.com/api/json/trend/getTrendList.nhn?type=blog");		// 블로그
		lsTrendURL.add("http://m.stock.naver.com/api/json/trend/getTrendList.nhn?type=cafe");		// 카페
		
		RestTemplate restTemplate = new RestTemplate();
		Iterator itr = lsTrendURL.iterator();
		
		while ( itr.hasNext() ) {
			lsRtnData.add(restTemplate.getForObject(itr.next().toString(), String.class));
		}

        String filePath = commonConfig.getBatchDataDir() + "NaverTrendRankList.json";
        commonFileUtil.writeFileToJSONList(filePath, lsRtnData);
        
        logger.info("=========== NaverTrendRankService() ===========\n");
	}
}
