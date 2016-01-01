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
public class DaumRealRankService implements Runnable {
	
	@Autowired
	private CommonConfiguration commonConfig;
	
	@Autowired
	private CommonFileUtil commonFileUtil;
	
	private final Logger logger = LoggerFactory.getLogger("THREAD");
	
	private final static String URL = "http://www.daum.net/";
	
	@Override
	public void run() {
		MDC.put("threadLogFileName", this.getClass().getSimpleName());
		
		logger.info("=========== DaumRealRankService() ===========");
		
		List lsRtnData	= new ArrayList();
		
		try {
			Document doc = Jsoup.connect(URL).get();
			
			// 검색구분자 CSS Selector
			String sRealRankSelector = "#realTimeSearchWord li div div .txt_issue a";
			Elements rcw = doc.select(sRealRankSelector);
			
			// 검색시간
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	        long nowmills = System.currentTimeMillis();
	        String now = sdf.format(new Date(nowmills));
	        
	        // 실시간검색어 Parsing
	        int nChk = 1, nRank = 1;
	        for (Element el : rcw) {
	        	nChk++;
	        	
	        	if ( nChk % 2 == 0 ) {
	        		Map mData = new HashMap();
	        		mData.put("rank", nRank++);
		        	mData.put("title", el.after("strong").text());
		        	mData.put("link", el.attr("href"));
		        	mData.put("searchTime", now);
		        	
		        	lsRtnData.add(mData);
		        	
		        	mData = null;
	        	}
	        }
	        
	        String filePath = commonConfig.getBatchDataDir() + "DaumRealRankList.json";
	        commonFileUtil.writeFileToJSONList(filePath, lsRtnData);
	        
	        logger.info("=========== DaumRealRankService() ===========\n");
	        
		} catch (Exception e) {
			logger.error("{}", e);
		}
	}
}
