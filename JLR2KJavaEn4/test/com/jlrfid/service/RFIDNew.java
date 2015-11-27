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
public class RFIDNew implements GetReadData{

	
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
	private static String existing_pig;
	private static String location;
	private static String stat_pig;
	private static String rfid;
	private static int counter = 0;
	
	private static MainHandler handler;	
 
	
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
						handler.BeginInv(new RFIDNew());
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
							System.out.println(rfid);
							counter = 1;
							existing_pig = resultset.getString(2);
							stat_pig = resultset.getString(3);
						}
					}	
					if(counter == 0){
						showPanel(rfid);
				        
				       // System.out.println(comboBox.getSelectedItem());
				        query = " INSERT INTO rfid_tags(tag_rfid,pig_id,label,status) VALUES(?,?,?,?)";
							
						  // create the mysql insert preparedstatement
						 preparedStmt = connect.prepareStatement(query);
						  preparedStmt.setString (1,rfid);
						  preparedStmt.setString (2, pigid.getText());
						  preparedStmt.setInt (3,0);
						  preparedStmt.setString (4, status);
						  // execute the preparedstatement
						  preparedStmt.execute();
						 
					}else{
						JPanel pan = new JPanel();
						if(stat_pig.equals("Active")){
							Statement statement2 = connect.createStatement();
							query = "SELECT l.address FROM rfid_tags rt INNER JOIN pig pi ON"
									+ " pi.pig_id = rt.pig_id"
									+ " INNER JOIN pen p ON"
									+ " p.pen_id = pi.pen_id"
									+ " INNER JOIN house h ON"
									+ " h.house_id = p.house_id"
									+ " INNER JOIN location l ON"
									+ " l.loc_id = h.loc_id"
									+ " WHERE rt.pig_id ='"+existing_pig+"'";
							ResultSet resultset2 = statement2.executeQuery(query);
							resultset2.next();
							if (JOptionPane.showConfirmDialog(null, "This RFID tag is already active with Pig ID "+existing_pig+".\n"
									+ " The current location of the pig is "+resultset2.getString(1)+"\n Do you want to deactivate this tag so that another pig can use it?", "WARNING",
							        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
							    // yes option	
								if (JOptionPane.showConfirmDialog(null, "Are you really sure?", "WARNING",
								        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
								    // yes option
									query = "UPDATE rfid_tags SET status = 'Inactive',pig_id='0',label='0' WHERE tag_rfid = '"+rfid+"'";
									preparedStmt = connect.prepareStatement(query);
									preparedStmt.execute();
								}
							}
						}else if(stat_pig.equals("Inactive")){
							JOptionPane.showMessageDialog(panel,"This RFID tag is already inactive!");
							
							
						}else if(stat_pig.equals("Marker")){
							JOptionPane.showMessageDialog(panel,"This RFID tag is a marker!");
						}
						
						
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
