package com.thecloudyco.pos.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.cj.exceptions.CJCommunicationsException;
import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.util.ConsoleUtil;

public class AtticusDB {
	
	private boolean isConnected;
	
	private String db_host = Main.getConfig().getMysql_hostname();
	private String db_username = Main.getConfig().getMysql_username();
	private String db_password = Main.getConfig().getMysql_password();
	private String db_name = Main.getConfig().getMysql_db();
	
	private Connection connection;
	
	private int reconnectAttempts = 0;
	
	public AtticusDB() {
		reconnect();
	}
	
	private void reconnect() {
		ConsoleUtil.Clear();
		try {
			isConnected = true;
			connection = DriverManager.getConnection("jdbc:mysql://" + db_host + ":3306/" + db_name + "?autoReconnect=true", db_username, db_password);
		} catch(SQLException ex) {
			isConnected = false;
			if(reconnectAttempts < 5) {
				reconnectAttempts++;
			}
			System.out.println("ERROR CONNECTING TO CONTROLLER: " + ex.getMessage());
			System.out.println("Reconnecting in " + reconnectAttempts * 5 + " seconds...");
			
			try {
				Thread.sleep((reconnectAttempts * 5) * 1000);
			} catch (InterruptedException e) {
			}
			reconnect();
		}
	}

	public int update(String sql) throws SQLException {
		//System.out.println(sql);
		if(connection == null) {
			reconnect();
		}
		int res;
		
		try {
			res = connection.createStatement().executeUpdate(sql);
		} catch(CJCommunicationsException e) {
			reconnect();
			return update(sql);
		}
		return res;
	}
	
	public ResultSet query(String sql) throws SQLException {
		//System.out.println(sql);
		if(connection == null) {
			reconnect();
		}
		ResultSet set = null;
		try {
			set = connection.createStatement().executeQuery(sql);
		} catch(CJCommunicationsException e) {
			reconnect();
			return query(sql);
		}
		return set;
	}
	
	public boolean isConnected() {
		return isConnected;
	}
}
