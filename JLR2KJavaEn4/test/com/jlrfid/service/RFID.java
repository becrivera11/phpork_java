package com.jlrfid.service;

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

import com.jlrfid.service.MainHandler;
public class RFID implements Runnable,GetReadData{
	private  PreparedStatement preparedStmt;
	private Connection connect;
	public void run(){
		MainHandler handler = new MainHandler();
		if(handler.dllInit("dll/R2k.dll")){
			if(handler.deviceInit(Thread. currentThread(). getName(),0, 20058)){
				
				try {
					handler.BeginInv(new RFID());
				} catch (RFIDException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(handler.StopInv());
				System.out.println(Thread. currentThread(). getName());
				
				 
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
		ResultSet result = null;
		Statement statement = null;
		pig_id = getPigIdFromRFID(rfid);
		
		
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
			 
			try {
				connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phpork","root","");
				statement = connect.createStatement();
				System.out.println(pig_id);
				result = statement.executeQuery("Select DISTINCT location,house_no,pen_no from pig where pig_id = '"+pig_id +"'");
				//Pig p = new Pig(pig_id);
				
				String query = " INSERT INTO movement(pig_id, date,time,location,house_no,pen_no) VALUES(?,?,?,?,?,?)";
					while(result.next()){
				      // create the mysql insert preparedstatement
				       preparedStmt = connect.prepareStatement(query);
				      preparedStmt.setString (1, pig_id);
				      preparedStmt.setString (2, d1);
				      preparedStmt.setString (3, t2);
				      preparedStmt.setString (4,result.getString(1));
				      preparedStmt.setString (5,"H"+result.getString(2));
				      preparedStmt.setString (6,"P"+ result.getString(3));
				      // execute the preparedstatement
				    
				}
					  preparedStmt.execute();
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
				if(result!=null){
					try {
						result.close();
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
	
	public String getPigIdFromRFID(String rfid){
		ResultSet result = null;
		Statement statement = null;
		String pig_id = "";
		try{
			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phpork","root","");
			statement = connect.createStatement();
			result = statement.executeQuery("Select pig_id from pig where rfid = '"+rfid +"'");
			
			while(result.next()){
				pig_id = result.getString(1);
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
