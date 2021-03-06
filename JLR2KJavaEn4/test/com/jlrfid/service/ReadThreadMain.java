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

import com.jlrfid.service.GetReadData;

public class ReadThreadMain implements GetReadData{
	 
	private  PreparedStatement preparedStmt;
	private Connection connect;
	
	public static void main(String[] args){
		ReadThread mt = new ReadThread();
		//ReadThread my = new ReadThread();
		Thread b = new Thread(mt);
		//Thread c = new Thread(my);
		b. start();
		//c. start();
		
	}

	@Override
	public void getReadData(String data, int antNo) {
		//System.out.println(ip + " " + port);
		System.out.println("Dev");
		System.out.println(data + " " + antNo);
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
				result = statement.executeQuery("Select DISTINCT location,house_no,pen_no,rfid from pig where pig_id = '"+pig_id +"'");
				//Pig p = new Pig(pig_id);
				
				String query = " INSERT INTO movement(pig_id, date,time,location,house_no,pen_no,count) VALUES(?,?,?,?,?,?,?)";
					while(result.next()){
				      // create the mysql insert preparedstatement
				       preparedStmt = connect.prepareStatement(query);
				      preparedStmt.setString (1, pig_id);
				      preparedStmt.setString (2, d1);
				      preparedStmt.setString (3, t2);
				      preparedStmt.setString (4,"Development Laboratory");
				      preparedStmt.setString (5,"H"+result.getString(2));
				      preparedStmt.setString (6,"P"+ result.getString(3));
				      preparedStmt.setInt (7,1);
				     
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
		 try {
			updateCount(getCount(pig_id,"Development Laboratory"),rfid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public String getCount(String pig,String loc){
			String count = "";
			Statement st = null;
			ResultSet rs = null;
			try {
				connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phpork","root","");
				st = connect.createStatement();
				rs = st.executeQuery("Select SUM(m.count) from movement m inner join reader r on r.location = m.location inner join pig p on p.pig_id = m.pig_id where m.pig_id = '"+pig+"'and m.location = '"+loc+"'group by r.reader,m.pig_id" );
				
				while(rs.next()){
					
				count = rs.getString(1);	
				}
				
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
				if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(st!=null){
					try {
						st.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return count;
		
	}
	 public void updateCount(String count,String rfid) throws SQLException{
		 Statement st = null;
		 System.out.println(count);
		 connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phpork","root","");
			st = connect.createStatement();
			st.executeUpdate("UPDATE usertag SET COUNT ='"+count+"' WHERE RFID = '"+rfid+"'");

		    
	}
}
