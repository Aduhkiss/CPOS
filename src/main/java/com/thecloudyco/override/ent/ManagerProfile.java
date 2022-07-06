package com.thecloudyco.override.ent;

import com.thecloudyco.override.common.OverrideType;

public class ManagerProfile {
	
	private String first_name;
	private String last_name;
	private OverrideType override_type;
	private String password;
	
	public ManagerProfile(String first_name, String last_name, int override_type) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.override_type = OverrideType.getType(override_type);
	}
	
	public String getFirstName() {
		return first_name;
	}
	
	public String getLastName() {
		return last_name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String f) {
		password = f;
	}
	
	public String getOverrideTypeAsString() {
		return override_type.toString();
	}
	
	public OverrideType getOverrideType() {
		return override_type;
	}
}
