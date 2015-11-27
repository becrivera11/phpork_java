package com.jlrfid.service;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
public class LoopReadDemo implements GetReadData{

	String ip;
	String penid;
	
	LoopReadDemo(){}
	
	LoopReadDemo(String ip,String pen_id)
	{
		this.ip = ip;
		this.penid = pen_id;
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
		Vector<Integer> ports =new Vector<Integer>(5,2);
		Vector<String> dlls = new Vector<String>(5,2);
		Vector<String> locs = new Vector<String>(5,2);
		
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
				int value = Integer.parseInt(prop.getProperty(key));
				dlls.add(key);
				ports.add(value);
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
		try {

			prop = new Properties();
			String filename = "reader.properties";
			input3 = new FileInputStream(filename);			

			prop.load(input3);	
			
			Enumeration<?> e = prop.propertyNames();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				String value = prop.getProperty(key);
				ips.add(key);
				locs.add(value);
			}
						
			
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
		//For every RFID that is active, spawn a getReadData method
		for(int i = 0;i < ips.size();i++)
		{
			handler = new MainHandler();
			if(handler.dllInit("dll/" + dlls.get(i))){				
				if(handler.deviceInit(ips.get(i),0, ports.get(i))){
					System.out.println(handler.StopInv());
					try {
						System.out.println("Thread" + " " + (i+1));
						try{Thread.sleep(1000);}catch(InterruptedException e){System.out.println(e);}
						handler.BeginInv(new LoopReadDemo(ips.get(i),locs.get(i)));
						System.out.println(handler.StopInv());											
						//handler.BeginInv(new LoopReadDemo2());
					} catch (RFIDException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.out.println(e);
					}
				}
			}
		}
	}

	public void getReadData(String data, int antNo) {
		
		String dateTime = getDateTimeFromRFID(data);
		String rfid = getRFID(data);
		String pig_id;
		String d1 = "";
		String t2="";
		String t = getTimeFromRFID(data);
		pig_id = getPigIdFromRFID(rfid);
		//String reader = getReaderName();
		String label = displayLabel(pig_id);		
		
		//Boolean isTagActive = isRFIDActive(rfid);
		//System.out.println("Current: " + rfid);		
		if(dateTime!="" && rfid!="" && pig_id!= ""){
			
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
			 	//System.out.println(isTagActive);

			
			try {
				//if(isTagActive == true){
					System.out.println( ip + " Pig: " + label + " Pen: " + penid);
					connect = DriverManager.getConnection(TAG_SERVER,TAG_USER,TAG_PASS);														
					String query = " INSERT INTO movement(date_moved,time_moved,pen_id,server_date,server_time,pig_id)"
							+ " VALUES(?,?,?,NOW(),NOW(),?)";
					
					  // create the mysql insert preparedstatement
					  preparedStmt = connect.prepareStatement(query);
					  preparedStmt.setString (1,d1);
					  preparedStmt.setString (2, t2);
					  preparedStmt.setString (3, penid);
					  preparedStmt.setString (4,pig_id);				      				 			
					  // execute the preparedstatement
					  preparedStmt.execute();
					  
					 
					  
				//}
				//  else
				//	  System.out.println("RFID tag is not active");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				 /*This block should be added to your code
				  * You need to release the resources like connections
				  */
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
	
	public String displayLabel(String _pig_id)
	{
		int size = _pig_id.length();
		String s = "0";
		size = 10 - size;		
		for(int i = 0;i < size;i++)
		{
			s = s + "0";
		}
		s = s + _pig_id;
		String temp1 = s.substring(0,4);
		String temp2 = s.substring(5,9);
		String temp3 = s.substring(9);
		String label = temp1 + "-" + temp2 + "-" +temp3;		
		return label;
	}
	
	/*public String getPenID()
	{
		String pen_id = "";		
		Statement statement = null;
		ResultSet result = null;
		try
		{
			connect = DriverManager.getConnection(TAG_SERVER, TAG_USER, TAG_PASS);
			statement = connect.createStatement();
			query = "SELECT pen_id FROM reader WHERE ip_address = '" + ip +"'";
			result = statement.executeQuery(query);
			while(result.next())			
				pen_id = result.getString(1);				
		
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			 /*This block should be added to your code
			  * You need to release the resources like connections
			  
			if(connect!=null){
				try {
					connect.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return pen_id;
	}
	
	public String getReaderName()
	{
		String reader = "";
		Statement statement = null;
		ResultSet result = null;
		try
		{
			
			connect = DriverManager.getConnection(TAG_SERVER, TAG_USER, TAG_PASS);
			statement = connect.createStatement();
			query = "SELECT reader_name, reader_id FROM reader WHERE ip_address = '" + ip +"'";
			result = statement.executeQuery(query);
			while(result.next())
			{
				reader = result.getString(1);
				TAG_READER_ID = result.getString(2);
			}
		
		}
		catch(Exception e){
			e.printStackTrace();
		}finally{
			 /*This block should be added to your code
			  * You need to release the resources like connections
			  
			if(connect!=null){
				try {
					connect.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return reader;
	}*/
	
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
	
	public String getPigIdFromRFID(String rfid){
		Statement statement = null;
		String pig_id = "";
		ResultSet result = null;
		try{
			connect = DriverManager.getConnection(TAG_SERVER,TAG_USER,TAG_PASS);
			statement = connect.createStatement();
			query = "SELECT pig_id, tag_id FROM rfid_tags WHERE tag_rfid = '"+rfid +"'";
			result = statement.executeQuery(query);	
			while(result.next())
			{
				pig_id = result.getString(1);
				TAG_RFID =result.getString(2);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			 /*This block should be added to your code
			  * You need to release the resources like connections
			  */
			if(connect!=null){
				try {
					connect.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return pig_id;
	}	 
	 
}
