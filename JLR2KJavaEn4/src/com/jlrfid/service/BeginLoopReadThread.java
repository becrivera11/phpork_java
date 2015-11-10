package com.jlrfid.service;

import com.jlrfid.service.MainDllLib.CallbackFunctionImpl;

/**
 * multiple thread call circulation reading  
 * @author niuzehao
 *
 */
public class BeginLoopReadThread implements Runnable{

	/**
	 * dll object
	 */
	private MainDllLib lib = null;
	
	/**
	 * succeed reading data, callbacks function object
	 */
	private CallbackFunctionImpl FUN_CMC = null;
	
	public boolean isSuccess = true;
	
	/**
	 * initialize thread object of circulation reading 
	 * @param lib dll object
	 * @param FUN_CMC succeed getting data , callbacks funciton object
	 */
	public BeginLoopReadThread(MainDllLib lib, CallbackFunctionImpl FUN_CMC){
		this.lib = lib;
		this.FUN_CMC = FUN_CMC;
	}	

	public void run() {
		int result = lib.BeginMultiInv(FUN_CMC);
		if (UHFOperate.FAILRESULT == result) {
			isSuccess = false;
		}else {
			isSuccess = true;
		}
	}
	

}
