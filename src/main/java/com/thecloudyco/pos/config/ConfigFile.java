package com.thecloudyco.pos.config;

public class ConfigFile {
	
	//private String controllerServerAddress;
	private String terminal_number;
	private String store_number;
	
	private String mysql_hostname;
	private String mysql_db;
	private String mysql_username;
	private String mysql_password;
	
	public ConfigFile(/*String controllerServerAddress*/ String terminal_number, String store_number, String mysql_hostname, String mysql_db
			, String mysql_username, String mysql_password) {
		//this.controllerServerAddress = controllerServerAddress;
		this.terminal_number = terminal_number;
		this.store_number = store_number;
		
		this.mysql_hostname = mysql_hostname;
		this.mysql_db = mysql_db;
		this.mysql_username = mysql_username;
		this.mysql_password = mysql_password;
		
	}
	
//	public String getControllerAddress() {
//		return controllerServerAddress;
//	}
	
	public String getTerminalNumber() {
		return terminal_number;
	}
	
	public String getStoreNumber() {
		return store_number;
	}

	public String getMysql_hostname() {
		return mysql_hostname;
	}

	public String getMysql_db() {
		return mysql_db;
	}

	public String getMysql_username() {
		return mysql_username;
	}

	public String getMysql_password() {
		return mysql_password;
	}
	
}
