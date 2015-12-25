package com.batch.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.util.ObjectUtils;

import com.batch.common.CommonFileUtil;
import com.batch.configuration.CommonConfiguration;

@Service
public class NateRealRankNewsService implements Runnable {
	
	@Autowired
	private CommonConfiguration commonConfig;
	
	@Autowired
	private CommonFileUtil commonFileUtil;
	
	private final Logger logger = LoggerFactory.getLogger("THREAD");
	
	@Override
	public void run() {
		MDC.put("threadLogFileName", "NateRealRankNewsService");
		
		logger.info("=========== NateRealRankNewsService() ===========");
		
		// 네이트 랭킹뉴스(시사)
		// 네이트 랭킹뉴스(연예)
		// 네이트 랭킹뉴스(스포츠)
		List<String> lsUrl = Arrays.asList("http://m.news.nate.com/rank/list?mid=m2001&section=sisa&rmode=interest",
									"http://m.news.nate.com/rank/list?mid=e2001&section=ent&rmode=interest",
									"http://m.news.nate.com/rank/list?mid=s2001&section=spo&rmode=interest");
		
		List lsRtnData = new ArrayList();
		
		int nGb = 1;
		
		for (String sURL : lsUrl) {
			try {
				Document doc = Jsoup.connect(sURL).get();
				
				// 검색구분자 CSS Selector
				String sRealRankSelector = ".rk_list li";
				Elements rcw = doc.select(sRealRankSelector);
				
				// 검색시간
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		        long nowmills = System.currentTimeMillis();
		        String now = sdf.format(new Date(nowmills));
		        
		        // CSS Parsing
		        int nChk = 0, nRank = 1;
		        for (Element el : rcw) {
		        	
		        	if ( nChk >= 10 ) {
		        		break;
		        	}
		        	
	        		Map mData = new HashMap();
	        		
	        		mData.put("rank", nRank++);
		        	mData.put("title", el.after("span").text());
		        	mData.put("link", el.children().attr("href").replaceAll("m.news.nate.com", "news.nate.com").replaceAll("\\?.+", ""));
		        	mData.put("searchTime", now);
		        	
		        	lsRtnData.add(mData);
		        	
		        	mData = null;
		        	nChk++;
		        }
		        
		        String filePath = commonConfig.getBatchDataDir() + "NateRealRankNewsList_" + nGb++ + ".json";
		        commonFileUtil.writeFileToJSONList(filePath, lsRtnData);
		        
		        lsRtnData.clear();
		        
		        logger.info("=========== NateRealRankNewsService() ===========\n");
		        
			} catch (Exception e) {
				logger.error("", e);
			}
		
		}
	}
}
