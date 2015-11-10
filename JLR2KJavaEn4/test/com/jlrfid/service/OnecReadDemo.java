package com.jlrfid.service;

import com.jlrfid.service.GetReadData;
import com.jlrfid.service.MainHandler;
import com.jlrfid.service.RFIDException;

public class OnecReadDemo implements GetReadData{

	/**
	 * @param args
	 * @throws RFIDException 
	 */
	public static void main(String[] args) throws RFIDException {
		// TODO Auto-generated method stub

		MainHandler handler = new MainHandler();
		if(handler.dllInit("dll/R2k.dll")){
			if(handler.deviceInit("192.168.1.204",0, 20059)){
				//System.out.println(handler.StopInv());
				handler.InvOnce(new OnecReadDemo());
			}
		}
	}

	public void getReadData(String data, int antNo) {
		
		System.out.println(data);
	}

}
