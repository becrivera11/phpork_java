package com.jlrfid.service;

import java.nio.ByteBuffer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public class UHFOperate {
	
	/**
	 * return data of failed operation 
	 */
	public static int FAILRESULT = 0x80;
	
	static MainDllLib lib = null;
	
	/**
	 * load dll  
	 * @param dll:file name
	 * @return mark of load successful or not
	 */
	public static boolean init(String dll) {
		try {
			lib = (MainDllLib) Native.loadLibrary(dll,MainDllLib.class);
			if (lib != null)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * byte array convert into Hex character string
	 * @param bArray: byte array should be converted 
	 * @return: Hex character string
	 */
	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	
	/**
	 * Hex character string convert to byte array (convert the string to capital letter automatically) 
	 * @param hex: Hex character string
	 * @return: byte array after character string converted 
	 */
	public static byte[] hexStringToByte(String hex) {
		hex = hex.toUpperCase();
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}
	
}


interface MainDllLib extends StdCallLibrary {
	
	public int deviceInit(String ip,int comm,int port);
	
	public int deviceConnect();
	
	public int BeginMultiInv(CallbackFunctionImpl HANDLE_FUN);
	
	public int StopInv();
	
	public void deviceDisconnect();
	
	public void deviceUnInit();
	
	public int GetDevVersion(ByteBuffer buffer);
	
	public int GetAnt(AntStruct struct);
	
	public int SetAnt(AntStruct struct);
	
	public int ReadTagData(char bank,char begin,char size,ByteBuffer buffer,String password);
	
	public int WriteTagData(char bank,char begin,char size,String data,String password);
	
	public int BeginOnceInv(CallbackFunctionImpl HANDLE_FUN);
	
	public int ReadTagBuffer(CallbackFunctionImpl HANDLE_FUN);
	
	public int SetAlive(byte intervalSecond);
	
	public int ResetTagBuffer();
	
	public int SetDO(byte port,byte state);
	
	public int GetDI(ByteBuffer buffer);
	
	public int KillTag(String pKill_pwd,String access_pwd);
	
	public int LockTag(int opcode,int block,String password);
	
	public void ForceStop();
	
	public int GetLastErrorMessage();
	
	public interface CallbackFunction extends StdCallCallback {
		public void HANDLE_FUN(char cmdId, Pointer data, int length);
	}
	
	public class CallbackFunctionImpl implements CallbackFunction {
		
		public GetReadData gatReadData;
		
		public void setGatReadData(GetReadData gatReadData) {
			this.gatReadData = gatReadData;
		}

		public void HANDLE_FUN(char cmdId, Pointer data, int length) {
			String datasString = UHFOperate.bytesToHexString(data.getByteArray(0, length)).trim();
			if(!"".equals(datasString) && !"F0".equals(datasString) && !"F1".equals(datasString) && !"F2".equals(datasString) && !"F3".equals(datasString)){
				gatReadData.getReadData(datasString.substring(0,datasString.length()-2),
						Integer.parseInt(datasString.substring(datasString.length()-2))+1);
				//System.out.println(datasString);
			}else {
				gatReadData.getReadData(datasString,0);
			}
			
		}
		

	}
}

