package com.jlrfid.service;

public interface GetReadData {

	/**
	 * callback function of circulation reading or invertory once 
	 * @param data tags data
	 * @param antNo antenna serial No. from No. 0
	 */
	
	public void getReadData(String data ,int antNo);
	
	
}
