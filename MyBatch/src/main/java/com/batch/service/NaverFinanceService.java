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
public class NaverFinanceService implements Runnable {
	
	@Autowired
	private CommonConfiguration commonConfig;
	
	@Autowired
	private CommonFileUtil commonFileUtil;
	
	private final Logger logger = LoggerFactory.getLogger("THREAD");
	
	private final static String URL = "http://m.stock.naver.com/";
	
	@Override
	public void run() {
		MDC.put("threadLogFileName", "NaverFinanceService");
		
		logger.info("=========== NaverFinanceService() ===========");
		
		List lsRtnData	= new ArrayList();
		
		try {
			// 검색시간
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	        long nowmills = System.currentTimeMillis();
	        String now = sdf.format(new Date(nowmills));
			
			Document doc = Jsoup.connect(URL).get();
			
			// 검색구분자 CSS Selector
			String sRealRankSelector = ".index_lst a";
			Elements rcw = doc.select(sRealRankSelector);
			
			int num = 0;
			
	        // CSS Parsing
	        for (Element el : rcw) {
	        	num++;
	        	
        		Map mData = new HashMap();
        		
	        	mData.put("section", el.children().select("strong").get(0).text());
	        	mData.put("link", el.attr("href"));
	        	mData.put("amount", el.children().select("span").get(0).text());
	        	mData.put("point", el.children().select(".gap_price").text().replaceAll("[가-힣]+",""));
	        	mData.put("rate", el.children().select(".gap_rate").text());
	        	
	        	if ( num < 2 ) {
	        		mData.put("gaein", el.children().select("ul li").get(0).select("span").text());
	        		mData.put("forein", el.children().select("ul li").get(0).select("span").text());
	        		mData.put("gigan", el.children().select("ul li").get(0).select("span").text());
	        	}
	        	
	        	mData.put("searchTime", now);
	        	
	        	lsRtnData.add(mData);
	        	
	        	mData = null;
	        }

	        String filePath = commonConfig.getBatchDataDir() + "NaverFinanceList.json";
	        commonFileUtil.writeFileToJSONList(filePath, lsRtnData);
	        
	        logger.info("=========== NaverFinanceService() ===========");
	        
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
