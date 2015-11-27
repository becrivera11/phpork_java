package com.jlrfid.service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class ReusedRFID implements GetReadData{

	
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
	private static String rfid;
	private static int counter = 0;
	
	public static void main(String[] args) throws RFIDException {		
		
	
		//For every RFID that is active, spawn a getReadData method
		MainHandler handler = new MainHandler();
			if(handler.dllInit("dll/R2k.dll")){				
				if(handler.deviceInit("10.0.5.101",0, 20058)){
					System.out.println(handler.StopInv());
					try {
						try{
							Thread.sleep(1000);
						}catch(InterruptedException e){
							System.out.println(e);
						}
						handler.BeginInv(new ReusedRFID());
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

	public void getReadData(String data, int antNo) {
		
		rfid = getRFID(data);
		
		if(rfid!=""){
		
			try {
					connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phpork","root","");
					Statement statement = connect.createStatement();
					query = "SELECT rt.tag_rfid,rt.pig_id,rt.status FROM rfid_tags rt";
					ResultSet resultset = statement.executeQuery(query);
					
					while(resultset.next())	{
						String r = resultset.getString(1);
						if(r.equals(rfid)){
							System.out.println(r);
							counter = 1;
							resultset.getString(2);
							resultset.getString(3);
						}
						//System.out.println(counter);
					}	
					System.out.println(counter);
					if(counter == 1){
				       // System.out.println(comboBox.getSelectedItem());
						query = "UPDATE rfid_tags SET status = 'Inactive' WHERE tag_rfid = '"+rfid+"'";
						preparedStmt = connect.prepareStatement(query);
						preparedStmt.execute();
						 
					}else{
						showPanel(rfid);
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
	      		
	}
	
	
	public String getRFID(String data){

		try{
			return data.substring(0,data.length()-14);
			}catch(Exception e){
				return "";
			}
	}
	public JPanel showPanel(String rfid){
		pigid = new JTextField(5);
		panel = new JPanel();
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        model.addElement("Inactive");
        model.addElement("Marker");
        JComboBox<String> comboBox = new JComboBox<String>(model);
        panel.add(new JLabel("RFID:"));
        panel.add(new JTextField(rfid));
        panel.add(new JLabel("Pig label:"));
        panel.add(pigid);
        panel.add(new JLabel("Status:"));
        panel.add(comboBox);
        JOptionPane.showConfirmDialog(null, panel, "NEW RFID TAGS", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        //System.out.println(pigid.getText());
         status = (String) comboBox.getSelectedItem();
         System.out.println(comboBox.getSelectedItem());
        return panel;
	}
	
	
	 
}
