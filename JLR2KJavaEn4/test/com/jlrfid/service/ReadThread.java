package com.jlrfid.service;

import com.jlrfid.service.MainHandler;

public class ReadThread implements Runnable{
	
	@Override
	public void run() {		
			
		 MainHandler handler = new MainHandler();		 
			if(handler.dllInit("dll/R2k.dll")){
				if(handler.deviceInit("192.168.1.204",0, 20059)){
					//System.out.println(handler.StopInv());
					try {
						System.out.println("Thread1");
						handler.BeginInv(new LoopReadDemo());
						System.out.println(handler.StopInv());
						//handler.BeginInv(new LoopReadDemo2());
					} catch (RFIDException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.out.println("error");
					}
				}
			}	
			
			MainHandler handler2 = new MainHandler();		 
			if(handler2.dllInit("dll/R2k2.dll")){
				if(handler2.deviceInit("192.168.1.203",0, 20058)){
					//System.out.println(handler.StopInv());
					try {
						System.out.println("Thread2");
						handler2.BeginInv(new ReadThreadMain());
						System.out.println(handler2.StopInv());
						//handler.BeginInv(new LoopReadDemo2());
					} catch (RFIDException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.out.println("error");
					}
				}
			}
			
	}
	
	
	
}

