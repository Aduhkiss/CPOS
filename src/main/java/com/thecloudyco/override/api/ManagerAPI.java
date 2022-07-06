package com.thecloudyco.override.api;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.override.ent.ManagerProfile;
import com.thecloudyco.pos.Main;
import com.thecloudyco.pos.database.AtticusDB;
import com.thecloudyco.pos.user.Operator;
import com.thecloudyco.pos.util.StringUtil;

public class ManagerAPI {
	
	AtticusDB db = Main.getDB();
	
	public ManagerAPI() {
	}
	
	public ManagerProfile getProfile(String key) throws SQLException {
		ResultSet result = db.query("SELECT * FROM `mgr_overrides` WHERE `override_key` = '" + key + "';");
		ManagerProfile pro = null;
		while(result.next()) {
			pro = new ManagerProfile(result.getString("first_name"), result.getString("last_name"), result.getInt("override_type"));
		}
		return pro;
	}
	
	public boolean authenticate(Operator operator, String password) throws SQLException {
		ResultSet result = db.query("SELECT * FROM `mgr_overrides` WHERE `override_key` = '" + operator.getOperatorId() + "';");
		while(result.next()) {
			if(password.equals(result.getString("password"))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean authenticate(String operator, String password) throws SQLException {
		ResultSet result = db.query("SELECT * FROM `mgr_overrides` WHERE `override_key` = '" + operator + "';");
		while(result.next()) {
			if(password.equals(result.getString("password"))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isLoginAuthorized(OverrideType needed, String key) throws IOException, InterruptedException, SQLException {
		return true;
	}
	
	// Switching to the new safer paper manager override system
	
	public boolean isAuthorized(OverrideType needed, String key) throws SQLException {
		
		ResultSet result = Main.getDB().query("SELECT * FROM `paper_overrides` WHERE `override_number` = " + key + ";");
		
		while(result.next()) {
			long expires = result.getLong("expiration_time");
			String operator_id = result.getString("operator_id");
			//    1008       1006
			if(System.currentTimeMillis() >= expires) {
				System.out.println("EXPIRED");
				
				// If the override is expired, then delete it from the system
				 Main.getDB().update("DELETE FROM `paper_overrides` WHERE `override_number` = '" + key + "';");
				
				return false;
			} else {
				ManagerProfile prof = getProfile(operator_id);
				if(prof.getOverrideType().getPower() >= needed.getPower()) {
					return true;
				}
				System.out.println("NOT A PIC");
				return false;
			}
		}
		System.out.println("NOT FOUND");
		return false;
	}
}
