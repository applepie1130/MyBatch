package com.batch.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.batch.common.CommonFileUtil;
import com.batch.configuration.CommonConfiguration;

@Service
public class DailyQTService implements Runnable {
	
	@Autowired
	private CommonConfiguration commonConfig;
	
	@Autowired
	private CommonFileUtil commonFileUtil;
	
	private final Logger logger = LoggerFactory.getLogger("THREAD");
	
	private final static String URL = "http://www.godpeople.com/ajax/_home_top100.php";
	
	@Override
	public void run() {
		MDC.put("threadLogFileName", "DailyQTService");
		
		logger.info("=========== DailyQTService() ===========");
		
		Map mRtnData = new HashMap();
		
		try {
			Document doc = Jsoup.connect(URL).get();
			
			// 검색구분자 CSS Selector
			String sRealRankSelector = "div";
			Elements rcw = doc.select(sRealRankSelector);
			
			// 검색시간
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	        long nowmills = System.currentTimeMillis();
	        String now = sdf.format(new Date(nowmills));
	        mRtnData.put("searchTime", now);
	        
	        // CSS Parsing
	        int nChk = 1;
	        for (Element el : rcw) {
        		if ( nChk == 3 ) {
        			mRtnData.put("contents", el.after("div").text());
        		}
	        	nChk++;
	        }
	        
	        String filePath = commonConfig.getBatchDataDir() + "DailyQTMap.json";
	        commonFileUtil.writeFileToJSONMap(filePath, mRtnData);
	        
	        logger.info("=========== DailyQTService() ===========\n");
	        
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}
}
