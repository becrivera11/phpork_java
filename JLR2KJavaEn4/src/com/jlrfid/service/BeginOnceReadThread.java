package com.jlrfid.service;

import com.jlrfid.service.MainDllLib.CallbackFunctionImpl;

/**
 * multiple thread call inventory once
 * @author niuzehao
 *
 */
public class BeginOnceReadThread implements Runnable{

	MainDllLib lib = null;
	
	CallbackFunctionImpl FUN_CMC = null;
	
	/**
	 * mark of order send successful or not 
	 */
	public boolean isSuccess = true;
	
	/**
	 * initialize circulation reading thread object
	 * @param lib dll ib object
	 * @param FUN_CMC getting data succeed, callback function object
	 */
	public BeginOnceReadThread(MainDllLib lib, CallbackFunctionImpl FUN_CMC){
		this.lib = lib;
		this.FUN_CMC = FUN_CMC;
	}


	public void run() {
		int result = lib.BeginOnceInv(FUN_CMC);
		if (UHFOperate.FAILRESULT == result) {
			isSuccess = false;
		}else {
			isSuccess = true;
		}
	}
	

}
