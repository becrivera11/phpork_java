package com.jlrfid.service;

import com.jlrfid.service.GetReadData;
import com.jlrfid.service.MainHandler;
import com.jlrfid.service.RFIDException;

public class ReadBufferDemo implements GetReadData{

	/**
	 * @param args
	 * @throws RFIDException 
	 */
	public static void main(String[] args) throws RFIDException {
		// TODO Auto-generated method stub

		MainHandler handler = new MainHandler();
		if(handler.dllInit("dll/R2k.dll")){
			if(handler.deviceInit("192.168.1.201",0, 20058)){
				handler.ReadTagBuffer(new ReadBufferDemo());
				//System.out.println(handler.ResetTagBuffer());
			}
		}
	}

	public void getReadData(String data, int antNo) {
		System.out.println(data);
	}

}
