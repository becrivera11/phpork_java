package com.jlrfid.service;

import junit.framework.TestCase;

import com.jlrfid.service.AntStruct;
import com.jlrfid.service.MainHandler;
import com.jlrfid.service.RFIDException;

public class Demo extends TestCase{

	public void testGetAnt() {
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("192.168.2.202",0, 20058)){
				AntStruct struct = handler.GetAnt();
				for(int i=0; i<4; i++){
					System.out.println("天线" + (i+1) +(struct.antEnable[i]==1?"——已连接":"——未连接") + "——工作时间:" + struct.dwellTime[i] + "ms——功率:" + struct.power[i].longValue()/10 +"dBm");
				}
			}
		}
	}
	
	public void testSetAnt() throws RFIDException {
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				byte[] antEnable = new byte[]{1,0,0,0};
				long[] dwellTime = new long[]{2000,2000,4000,2000};
				long[] power = new long[]{300,300,250,200};
				if(handler.SetAnt(antEnable,dwellTime,power)){
					System.out.println("设置天线参数成功！");
					AntStruct struct = handler.GetAnt();
					for(int i=0; i<4; i++){
						System.out.println("天线" + (i+1) +(struct.antEnable[i]==1?"——已连接":"——未连接") + "——工作时间:" + struct.dwellTime[i] + "ms——功率:" + struct.power[i].longValue()/10 +"dBm");
					}
				}else{
					System.out.println("设置天线参数失败！");
				}
				
			}
		}
	}
	
	public void testGetDevVersion() {
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.GetDevVersion());
			}
		}
	}
	
	public void testGetDI() {
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.GetDI());
			}
		}
	}
	
	public void testSetDO() throws RFIDException {
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.SetDO((byte) 1, (byte) 0));
			}
		}
	}

	public void testLockTag() throws RFIDException {
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.lockTag(2, 2, "00000000"));
			}
		}
	}
	
	public void testReadCard() throws RFIDException{
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.ReadTagData(0, 0, 4, ""));
			}
		}
	}
	
	
	public void testWriteCard() throws RFIDException{
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.WriteTagData((char)1, (char)2, (char)2, "AAAABBBB", ""));
				System.out.println(handler.ReadTagData(1, 2, 2, ""));
			}
		}
	}
	
	public void testSetAlive() throws RFIDException{
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.SetAlive((byte)2));
			}
		}
	}
	
	public void testStopInv() throws RFIDException{
		MainHandler handler = new MainHandler();
		if(handler.dllInit("R2k.dll")){
			if(handler.deviceInit("",4, 115200)){
				System.out.println(handler.StopInv());
			}
		}
	}
	
	public void testResetTagBuffer() throws RFIDException{
		MainHandler handler = new MainHandler();
		if(handler.dllInit("dll/R2k.dll")){
			if(handler.deviceInit("192.168.1.200",0, 20058)){
				System.out.println(handler.ResetTagBuffer());
			}
		}
	}
}
