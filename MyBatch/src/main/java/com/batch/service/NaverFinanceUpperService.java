package com.batch.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class NaverFinanceUpperService implements Runnable {
	
	@Autowired
	private CommonConfiguration commonConfig;
	
	@Autowired
	private CommonFileUtil commonFileUtil;
	
	private final Logger logger = LoggerFactory.getLogger("THREAD");
	
	private final static String URL = "http://finance.naver.com/sise/sise_upper.nhn";
	
	@Override
	public void run() {
		MDC.put("threadLogFileName", this.getClass().getSimpleName());
		
		logger.info("=========== NaverFinanceUpperService() ===========");
		
		List lsRtnData = new ArrayList();
		
		// KOSPI, KOSDAQ 타입설정
		Map<String, Integer> mTypeData = new HashMap<String, Integer>();
		mTypeData.put("KOSPI", 0);
		mTypeData.put("KOSDAQ", 1);

		for (Map.Entry<String, Integer> entry : mTypeData.entrySet()) {
			try {
				// 검색시간
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				long nowmills = System.currentTimeMillis();
				String now = sdf.format(new Date(nowmills));
				
				Document doc = Jsoup.connect(URL).get();
				
				// 검색구분자 CSS Selector
				String sRealRankSelector = ".box_type_l tbody";
				
				// CSS Parsing
				int num = 0;
				int nEq = entry.getValue();
				Elements rcw = doc.select(sRealRankSelector).eq(nEq).select("tr");
				
				for (Element el : rcw) {
					Map mData = new HashMap();
					
					if ( !(num == 0 || num == 1 || (num == rcw.size() - 1) || (num == rcw.size() - 2)) ) {
						mData.put("section", el.select("td").eq(3).select("a").text());			// 종목명
						mData.put("amount", el.select("td").eq(4).text());						// 현재가
						mData.put("diffpercent", el.select("td").eq(5).select("span").text());	// 전일비
						mData.put("percent", el.select("td").eq(6).select("span").text());		// 등락률
						mData.put("volume", el.select("td").eq(7).text());						// 거래량
						mData.put("searchTime", now);
						
						lsRtnData.add(mData);
					}
					
					mData = null;
					num++;
				}
				
				String filePath = commonConfig.getBatchDataDir() + "NaverFinanceUpperList_" + entry.getKey() + ".json";
				commonFileUtil.writeFileToJSONList(filePath, lsRtnData);
				
			} catch (Exception e) {
				logger.error("", e);
			}
			
			logger.info("=========== NaverFinanceUpperService() ===========\n");
		}
	}
}
