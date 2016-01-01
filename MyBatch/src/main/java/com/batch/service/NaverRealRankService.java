package com.batch.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
public class NaverRealRankService implements Runnable {
	
	@Autowired
	private CommonConfiguration commonConfig;
	
	@Autowired
	private CommonFileUtil commonFileUtil;
	
	private final Logger logger = LoggerFactory.getLogger("THREAD");
	
	private final static String URL = "http://www.naver.com/";
	
	@Override
	public void run() {
		MDC.put("threadLogFileName", this.getClass().getSimpleName());
		
		logger.info("=========== NaverRealRankService() ===========");
		
		List lsRtnData	= new ArrayList();
		
		try {
			Document doc = Jsoup.connect(URL).get();
			
			// 검색구분자 CSS Selector
			String sRealRankSelector = "#realrank li a";
			Elements rcw = doc.select(sRealRankSelector);
			
			// 검색시간
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	        long nowmills = System.currentTimeMillis();
	        String now = sdf.format(new Date(nowmills));
	        
	        // 실시간검색어 Parsing
	        for (Element el : rcw) {
	        	Map mData = new HashMap();
	        	
	        	mData.put("rank", el.parent().attr("value"));
	        	mData.put("title", el.attr("title"));
	        	mData.put("link", el.attr("href"));
	        	mData.put("searchTime", now);
	        	
	        	String sId = el.parent().attr("id");
	        	if ( !"lastrank".equals(sId) ) {
	        		lsRtnData.add(mData);
	        	}
	        	
	        	mData = null;
	        }

	        String filePath = commonConfig.getBatchDataDir() + "NaverRealRankList.json";
	        commonFileUtil.writeFileToJSONList(filePath, lsRtnData);
	        
	        logger.info("=========== NaverRealRankService() ===========\n");
	        
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
