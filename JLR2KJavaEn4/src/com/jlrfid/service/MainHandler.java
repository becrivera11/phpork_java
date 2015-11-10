package com.jlrfid.service;

import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import com.sun.jna.NativeLong;

/**
 * Dll 核心操作类
 * @author niuzehao
 *
 */
public class MainHandler {
	
	Thread loopThread = null;
	
	/**
	 * load Dll file
	 * @param dllName: dll file name, dllName can be the file name when below the project root directory, if not, dllName should add the file path
	 * @return mark of load succeed or not: succeed:true, failed:false
	 * @throws URISyntaxException 
	 */
	public boolean dllInit(String dllName){
		return UHFOperate.init(dllName);
	}

	/**
	 * connect reader, when parameter comm is not 0, can connect by serial port, when parameter comm is 0, can connect by TCP/IP
	 * @param ip: reader IP address
	 * @param comm: serial port
	 * @param port: reader network port or serial port baud rate
	 * @return mark of connection or not: succeed: true, failed:false
	 */
	public boolean deviceInit(String ip,int comm,int port){
		int a = UHFOperate.lib.deviceInit(ip,comm,port);
		int b = UHFOperate.lib.GetLastErrorMessage();
		int flag = UHFOperate.lib.deviceConnect();
		StopInv();
		return flag == 0?true:false;
		
		
	}
	
	
	/**
	 * reading circulation
	 * when start to circulation reading, do other operation should stop inventory first, such write tag or read antenna
	 * send circulation reading order by start sub thread, after sending order, the method will callback function getReadData 
	 * @param getReadData: callback function object of return reading data
	 * @return mark of order sending successful: succeed: true, failed:false
	 */
	public boolean BeginInv(GetReadData getReadData) throws RFIDException{
		if (getReadData == null) {
			throw new RFIDException("getReadData can not be null");
		}
		MainDllLib.CallbackFunctionImpl HANDLE_FUN = new MainDllLib.CallbackFunctionImpl();
		HANDLE_FUN.setGatReadData(getReadData);
		BeginLoopReadThread readThread = new BeginLoopReadThread(UHFOperate.lib, HANDLE_FUN);
		loopThread = new Thread(readThread);
		loopThread.start();
		return readThread.isSuccess;
	}
	
	/**
	 * stop circular inventory
	 * @return mark of order sending successful: succeed: true, failed:false
	 */
	public boolean StopInv(){
		int result = UHFOperate.lib.StopInv();
		return UHFOperate.FAILRESULT == result?false:true;
	}
	
	/**
	 * disconnection
	 */
	public void deviceDisconnect(){
		UHFOperate.lib.deviceDisconnect();
	}
	
	/**
	 * restore initialization
	 */
	public void deviceUnInit(){
		UHFOperate.lib.deviceUnInit();
	}
	
	/**
	 * getting device version No.
	 * @return version character string
	 */
	public String GetDevVersion(){
		ByteBuffer buffer = ByteBuffer.allocate(16);
		UHFOperate.lib.GetDevVersion(buffer);
		return new String(buffer.array());
	}
	
	/**
	 * get the related configuration of antenna
	 * @return antenna data structure
	 */
	public AntStruct GetAnt(){
		AntStruct struct = new AntStruct();
		UHFOperate.lib.GetAnt(struct);
		return struct;
	}
	
	/**
	 * 
	 * @param antEnable antenna enable array or not, length only can be 4,value only can be 0（antenna enabled）or 1（antenna not enabled）
	 * @param dwellTime antenna time array, 4 antenna has 4 related variable of array, time range 50-10000
	 * @param power antenna power array, 4 antenna has 4 related variable of array, power range 20-33, the unit is ms
	 * @return mark of setting succeed or not
	 * @throws RFIDException
	 */
	public boolean SetAnt(byte[] antEnable,long[] dwellTime,long[] power) throws RFIDException{
		AntStruct struct = new AntStruct();
		for (int i = 0; i < 4; i++) {
			if(antEnable[i] != 0 && antEnable[i] != 1){
				throw new RFIDException("the value of array antEnable only can be 0 or 1");
			}
			struct.antEnable[i] = antEnable[i];
			
			if(dwellTime[i] < 50 && dwellTime[i] > 10000){
				throw new RFIDException("the value of array dwellTime only can be 50-10000");
			}
			struct.dwellTime[i] = new NativeLong(dwellTime[i]);
			
			if(power[i] < 20 && power[i] > 33){
				throw new RFIDException("the value of array power only can be 20-33");
			}
			struct.power[i] = new NativeLong(power[i]);
		}
		int result = UHFOperate.lib.SetAnt(struct);
		return UHFOperate.FAILRESULT == result?false:true;
	}
	
	/**
	 * get the single card data, can set the reading area, start address and reading length
	 * @param bank
	 *            reading area. as following: 0—— Reserve 1——EPC 2——TID 3——User
	 * @param begin
	 *            get the address of area, value range is 0-7.
	 * @param size
	 *            reading length, value range is 1 to 8（1Word = 2Bytes =
	 *            16 bits）（explanation：bank=EPC area, the value of begin+size is not over 8
	 *            ．bank=Reserve area，the value of begin+size is not over 4．）.
	 * @param password
	 *            reader password  default can be null
	 * @return succeed: return the Hex character string   failed: return -1
	 * @throws RFIDException
	 */
	public String ReadTagData(int bank,int begin,int size,String password)throws RFIDException{
		if (bank < 0 || begin <0 || size <0) {
			throw new RFIDException("bank/begin/size必须是正整数！");
		}
		if (bank == 1 && (begin + size > 8)) {
			throw new RFIDException(
					"读取EPC区内容时，begin(读区域中的地址)+size(要读取的长度)的值不超过8．请检查输入参数值！");
		}
		if (bank == 0 && (begin + size > 4)) {
			throw new RFIDException(
					"读取保留区内容时，begin(读区域中的地址)+size(要读取的长度)的值不超过4．请检查输入参数值！");
		}
		ByteBuffer buffer = ByteBuffer.allocate(size * 2);
		if (password == null || "".equals(password)) {
			password = "0000";
		}
		int result = UHFOperate.lib.ReadTagData((char)bank, (char)begin, (char)size, buffer, password);
		return UHFOperate.FAILRESULT == result?"-1":UHFOperate.bytesToHexString(buffer.array());
	}
	
	/**
	 * write single card data, can set the writing area, start add and reading length
	 * @param bank
	 *            writing area. as following: 0——Reserve 1——EPC 2——TID 3——User
	 * @param begin
	 *            the address of writing area, value range is 0-7.
	 * @param size
	 *            writing length, value range is 1 to 8（1 Word = 2 Bytes =
	 *            16 bits）（explanation：bank=EPC area，the value of begin+size is not over 8
	 *            ．bank=Reserve area，the value of begin+size is not over 4．）。
	 * @param password
	 *            write the password, default can be null
	 * @return mark of writing succeed or not
	 * @throws RFIDException
	 */
	public boolean WriteTagData(char bank,char begin,char size,String data,String password) throws RFIDException{
		if (bank < 0 || begin <0 || size <0) {
			throw new RFIDException("bank/begin/size must be positive integar！");
		}
		if(bank >3){
			throw new RFIDException("bank only can be 0-3");
		}
		if (bank == 1 && (begin + size > 8 || begin < 2)) {
			throw new RFIDException(
					"when writing in EPC area，begin must start with 2，and the value of begin(add of area)+size(writing length) is not over 8．please check the parameter value！");
		}
		if (bank == 0 && (begin + size > 4)) {
			throw new RFIDException(
					"when writing in Reserve,the value of begin(add of area)+size(writing length) is not over 4, please check the parameter value！");
		}
		if (data.length() != 4 * size) {
			throw new RFIDException(
					"data length must be size*4！");
		}
		if (password == null || "".equals(password)) {
			password = "\0\0\0\0";
		}
		//System.out.print(UHFOperate.bytesToHexString(UHFOperate.hexStringToByte(data))+" ");
		int result = UHFOperate.lib.WriteTagData((char)bank, (char)begin, (char)size, new String(UHFOperate.hexStringToByte(data)), password);
		return UHFOperate.FAILRESULT == result?false:true;
	}
	
	/**
	 * inventory once
	 * send circulation reading order by start sub thread, after sending success, the method will callback getReadData.
	 * @param getReadData: callback function object of reading return data
	 * @return mark of order sending success: succeed true, failed false
	 * @throws RFIDException
	 */
	public boolean InvOnce(GetReadData getReadData) throws RFIDException {
		if (getReadData == null) {
			throw new RFIDException("getReadData不能为null");
		}
		MainDllLib.CallbackFunctionImpl HANDLE_FUN = new MainDllLib.CallbackFunctionImpl();
		HANDLE_FUN.setGatReadData(getReadData);
		BeginOnceReadThread readThread = new BeginOnceReadThread(UHFOperate.lib, HANDLE_FUN);
		loopThread = new Thread(readThread);
		loopThread.start();
		return readThread.isSuccess;
	}
	
	/**
	 * set alive time interval
	 * @param intervalSecond time interval will be setted, unit is second
	 * @return  mark of order sending succeed or not: succeed true, failed false
	 * @throws RFIDException
	 */
	public boolean SetAlive(byte intervalSecond) throws RFIDException{
		int result = UHFOperate.lib.SetAlive(intervalSecond);
		return UHFOperate.FAILRESULT == result?false:true;
	}
	
	/**
	 * set Digital Output status
	 * @param port the setting port 0 or 1
	 * @param state status value 0 or 1
	 * @return mark of order sending succeed or not: succeed true, failed false
	 * @throws RFIDException
	 */
	public boolean SetDO(byte port,byte state) throws RFIDException{
		if (port != 0 && port != 1) {
			throw new RFIDException("parameter value incorrect , only can be 0 or 1");
		}
		
		if (state != 0 && state != 1) {
			throw new RFIDException("paramter value incorrect, only can be 0 or 1");
		}
		int result = UHFOperate.lib.SetDO(port, state);
		return UHFOperate.FAILRESULT == result?false:true;
	}
	
	/**
	 * get Digital Input status
	 * @return DI data character string
	 */
	public String GetDI(){
		//TODO 待确定长度
		ByteBuffer buffer = ByteBuffer.allocate(2);
		UHFOperate.lib.GetDI(buffer);
		return new String(buffer.array());
	}
	
	/**
	 * kill tag
	 * @param pKill_pwd: kill password
	 * @param access_pwd: access password
	 * @return mark of order sending succeed or not: succeed: true, failed:false
	 * @throws RFIDException
	 */
	public boolean KillTag(String pKill_pwd,String access_pwd) throws RFIDException{
		int result = UHFOperate.lib.KillTag(pKill_pwd, access_pwd);
		return UHFOperate.FAILRESULT == result?false:true;
	}
	
	/**
	 * lock tag
	 * @param opcode: operation code, 0: unlock; 1: permanence_writalbe, 2:security_lock; 3: permanence_unwriable
	 * @param block operation area
	 * @param password operation password
	 * @return mark of order sending succeed or not: succeed true, failed false
	 * @throws RFIDException
	 */
	public boolean lockTag(int opcode,int block,String password) throws RFIDException{
		int result = UHFOperate.lib.LockTag(opcode, block, new String(UHFOperate.hexStringToByte(password)));
		return UHFOperate.FAILRESULT == result?false:true;
	}
	
	/**
	 * read buffer
	 * when start to circulation reading, do other operation should stop inventory first, such write tag or read antenna
	 * send circulation reading order by start sub thread, after sending order, the method will callback function getReadData 
	 * @param getReadData: callback function object of return reading data
	 * @return mark of order sending successful: succeed: true, failed:false
	 */
	public boolean ReadTagBuffer(GetReadData getReadData) throws RFIDException{
		if (getReadData == null) {
			throw new RFIDException("getReadData can not be null");
		}
		MainDllLib.CallbackFunctionImpl HANDLE_FUN = new MainDllLib.CallbackFunctionImpl();
		HANDLE_FUN.setGatReadData(getReadData);
		LoopReadBufferThread readThread = new LoopReadBufferThread(UHFOperate.lib, HANDLE_FUN);
		loopThread = new Thread(readThread);
		loopThread.start();
		return readThread.isSuccess;
	}
	
	/**
	 * clear buffer
	 */
	public boolean ResetTagBuffer(){
		int result = UHFOperate.lib.ResetTagBuffer();
		return UHFOperate.FAILRESULT == result?false:true;
	}
}
