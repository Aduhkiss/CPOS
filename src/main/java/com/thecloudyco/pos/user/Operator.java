package com.thecloudyco.pos.user;

import java.sql.SQLException;

import com.thecloudyco.override.api.ManagerAPI;
import com.thecloudyco.override.common.OverrideType;
import com.thecloudyco.override.ent.ManagerProfile;

public class Operator {
	
	private String first_name;
	private String last_name;
	private String operator_id;
	private ManagerProfile mProfile;
	
	public Operator(String first_name, String last_name, String operator_id) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.operator_id = operator_id;
		ManagerAPI mAPI = new ManagerAPI();
		try {
			this.mProfile = mAPI.getProfile(operator_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getFirstName() {
		return first_name;
	}
	
//	public String getLastName() {
//		return last_name;
//	}
	
	public String getOperatorId() {
		return operator_id;
	}
	
	public OverrideType getOverrideType() {
		return mProfile.getOverrideType();
	}
	
	public String getFullName() {
		return first_name;
	}
	
	public String getActualFullName() {
		return first_name + " " + last_name;
	}
}
