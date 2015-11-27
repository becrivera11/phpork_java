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
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class NewRFIDTag implements GetReadData{

	String ip;
	String penid;
	
	NewRFIDTag(){}
	
	NewRFIDTag(String ip,String pen_id)
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
	private static JPanel panel;
	private static JTextField pigid;
	private static String status;
	
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
						handler.BeginInv(new NewRFIDTag(ips.get(i),locs.get(i)));
						System.out.println(handler.StopInv());	
						 pigid = new JTextField(5);
						panel = new JPanel();
						DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
				        model.addElement("Inactive");
				        model.addElement("Marker");
				        JComboBox<String> comboBox = new JComboBox<String>(model);
				        panel.add(new JLabel("Pig id:"));
				        panel.add(pigid);
				        panel.add(new JLabel("Status:"));
				        panel.add(comboBox);
				        JOptionPane.showConfirmDialog(null, panel, "NEW RFID TAGS", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				        System.out.println(pigid.getText());
				         status = (String) comboBox.getSelectedItem();
				        System.out.println(comboBox.getSelectedItem());
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
		
		String rfid = getRFID(data);
				
		
			try {
				//if(isTagActive == true){
				System.out.println("akkaka");
				
					connect = DriverManager.getConnection(TAG_SERVER,TAG_USER,TAG_PASS);
					Statement statement = connect.createStatement();
					query = "SELECT tag_rfid FROM rfid_tags";
					ResultSet resultset = statement.executeQuery(query);
					while(resultset.next())	{
						String infoMessage = "RFID already exists!";
						if(resultset.getString(1)==rfid){
							JOptionPane.showMessageDialog(null, infoMessage, "RFID: " + rfid, JOptionPane.INFORMATION_MESSAGE);
						}else{
							query = " INSERT INTO rfid_tags(tag_rfid,pig_id,label,status) VALUES(?,?,?,?)";
							
							  // create the mysql insert preparedstatement
							 preparedStmt = connect.prepareStatement(query);
							  preparedStmt.setString (1,rfid);
							  preparedStmt.setString (2, pigid.getText());
							  preparedStmt.setInt (3,0);
							  preparedStmt.setString (4, status);
							  // execute the preparedstatement
							  preparedStmt.execute();
						}
					}	
										
					 
					/*  
					  query = "INSERT INTO transaction(trsc_desc, reader_date, reader_time, server_date, server_time, reader_id, tag_id)"
					  		+ "VALUES(?,?,?,NOW(),NOW(),?,?)";
					  preparedStmt = connect.prepareStatement(query);
					  preparedStmt.setString (1, "Movement");
					  preparedStmt.setString (2, d1);
					  preparedStmt.setString (3, t2);
					  //preparedStmt.setString (4, "NOW()");
					  //preparedStmt.setString (5, "NOW()");
					  preparedStmt.setString (4, TAG_READER_ID);
					  preparedStmt.setString (5, TAG_RFID);
					  // execute the preparedstatement
					  preparedStmt.execute();*/
					  
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
			
		//}
		
		
	      		
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
	
	public String getPigIdFromRFID(String rfid){
		Statement statement = null;
		String pig_id = "";
		ResultSet result = null;
		try{
			connect = DriverManager.getConnection(TAG_SERVER,TAG_USER,TAG_PASS);
			statement = connect.createStatement();
			query = "SELECT pig_id, tag_id FROM rfid_tags  WHERE tag_rfid = '"+rfid +"'";
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
