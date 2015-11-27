package com.jlrfid.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class NewRFIDTag implements GetReadData{

	String ip;
	
	NewRFIDTag(){}
	
	NewRFIDTag(String ip)
	{
		this.ip = ip;
	}
	/**
	 * @param args
	 * @throws RFIDException 
	 */
	private PreparedStatement preparedStmt;	
	private Connection connect;
	private String query = "";
	
	private static MainHandler handler;	
 
	public static String TAG_SERVER = "server";
	public static String TAG_USER = "user";
	public static String TAG_PASS = "pass";
	public static String TAG_READER = "ip";
	public static String TAG_PORT = "port";
	public static String TAG_READER_ID = "";
	public static String TAG_RFID = "";
	public static void main(String[] args) throws RFIDException {		
		
		Vector<String> ips =new Vector<String>(5,2);
		//Vector<Integer> ports =new Vector<Integer>(5,2);
		Vector<String> dlls = new Vector<String>(5,2);
		//Vector<String> locs = new Vector<String>(5,2);
		
		//Map <String, Integer> readers = new LinkedHashMap<String, Integer>();		
				
		Properties prop;
		InputStream input = null,input3 = null;

		//Open and Read Database Configuration File
		try {

			input = new FileInputStream("config.properties");

			prop = new Properties();
			// load a properties file
			prop.load(input);

			// get the property value			
			
			TAG_SERVER = prop.getProperty(TAG_SERVER);
			TAG_USER = prop.getProperty(TAG_USER);
			TAG_PASS = prop.getProperty(TAG_PASS);			
						
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}				
		
		
		//Open and Read DLLs and respective IPs in File
		try {

			prop = new Properties();
			String filename = "dll_loc.properties";
			input3 = new FileInputStream(filename);			

			prop.load(input3);	
			
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				dlls.add(key);
				ips.add(value);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input3 != null) {
				try {
					input3.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//For every RFID that is active, spawn a getReadData method
		for(int i = 0;i < ips.size();i++)
		{
			handler = new MainHandler();
			if(handler.dllInit("dll/" + dlls.get(i))){				
				if(handler.deviceInit(ips.get(i),0, 20058)){
					try {
						System.out.println("Thread" + " " + (i+1));
						handler.BeginInv(new LoopReadDemo(ips.get(i)));
						System.out.println(handler.StopInv());	
					} catch (RFIDException e) {
						// TODO Auto-generated catch block
						System.out.println(e);
					}
				}
			}
		}
	}

	public void getReadData(String data, int antNo) {
		String dateTime = getDateTimeFromRFID(data);
		String rfid = getRFID(data);
		String d1 = "";
		String t2="";
		String t = getTimeFromRFID(data);
		
		if(dateTime!="" && rfid!=""){
			DateFormat formatter = new SimpleDateFormat("yyMMdd");
			DateFormat formatter2 = new SimpleDateFormat("YY-MM-dd");
			DateFormat time2 = new SimpleDateFormat("HHmmss");
			 try {
				Date date = formatter.parse(dateTime);
				Date getTime = time2.parse(t);
				t2 =time2.format(getTime);
				d1 = formatter2.format(date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 	
			try {
					System.out.println(rfid);
					connect = DriverManager.getConnection(TAG_SERVER,TAG_USER,TAG_PASS);
					File file =new File("output.txt");
		    		
		    		if(!file.exists()){
		    			try {
							file.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    		}
					 BufferedWriter writer = null;
					    try {
					        writer = new BufferedWriter(new FileWriter("Desktop/output.txt",true));
					        writer.write(rfid+" "+ d1+" "+t2);
					        writer.newLine();
					        writer.flush();
					    } catch (IOException e) {
					        System.err.println(e);
					    } finally {
					        if (writer != null) {
					            try {
					                writer.close();
					            } catch (IOException e) {
					                System.err.println(e);
					            }
					        }
					    }
					
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(connect!=null){
					try {
						connect.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(preparedStmt!=null){
					try {
						preparedStmt.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}
		  
			
		}
	      		
	}
	
	public String getDateTimeFromRFID(String data){
		try{
		return data.substring(data.length() - 14,data.length()-8);
		}catch(Exception e){
			return "";
		}
	}
	
	public String getTimeFromRFID(String data){
		try{
			return data.substring(data.length() - 8,data.length()-3);
			}catch(Exception e){
				return "";
			}
	}
	
	public String getRFID(String data){

		try{
			return data.substring(0,data.length()-14);
			}catch(Exception e){
				return "";
			}
	}
	

}
