package com.batch.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonConfiguration {
	@Value("#{commonConf['batchDataDir']}")
	private String batchDataDir;

	/**
	 * @return the batchDataDir
	 */
	public String getBatchDataDir() {
		return batchDataDir;
	}
}
