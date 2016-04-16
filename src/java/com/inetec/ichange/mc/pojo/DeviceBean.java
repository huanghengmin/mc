package com.inetec.ichange.mc.pojo;

import com.avdheshyadav.p4j.jdbc.model.Column;
import com.avdheshyadav.p4j.jdbc.model.Entity;


@Entity(schema="app",table="device")
public class DeviceBean {
	@Column(isPrimaryKey = true)
	private String id;
	@Column
	private String name;
	@Column
	private String deviceip;
	@Column
	private String deviceport;
	@Column
	private String devicesnmppwd;
	@Column
	private String auth;
	@Column
	private String authpassword;
	@Column
	private String common;
	@Column
	private String commonpassword;
	
	
    
	
	
	public String getAuth() {
		return auth;
	}



	public void setAuth(String auth) {
		this.auth = auth;
	}



	public String getAuthpassword() {
		return authpassword;
	}



	public void setAuthpassword(String authpassword) {
		this.authpassword = authpassword;
	}







	public String getCommon() {
		return common;
	}



	public void setCommon(String common) {
		this.common = common;
	}



	public String getCommonpassword() {
		return commonpassword;
	}



	public void setCommonpassword(String commonpassword) {
		this.commonpassword = commonpassword;
	}



	public String getDevicesnmppwd() {
		return devicesnmppwd;
	}



	public void setDevicesnmppwd(String devicesnmppwd) {
		this.devicesnmppwd = devicesnmppwd;
	}



	public String getDeviceport() {
		return deviceport;
	}



	public void setDeviceport(String deviceport) {
		this.deviceport = deviceport;
	}

	@Column
	private String devicetype;
	@Column
	private String devicecompany;
	@Column
	private String available;
	@Column
	private String devicemode;

	@Column
	private String snmpver;

	public DeviceBean() {
	}



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDeviceip() {
		return deviceip;
	}

	public void setDeviceip(String deviceip) {
		this.deviceip = deviceip;
	}

	public String getDevicetype() {
		return devicetype;
	}

	public void setDevicetype(String devicetype) {
		this.devicetype = devicetype;
	}

	public String getDevicecompany() {
		return devicecompany;
	}

	public void setDevicecompany(String devicecompany) {
		this.devicecompany = devicecompany;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getDevicemode() {
		return devicemode;
	}

	public void setDevicemode(String devicemode) {
		this.devicemode = devicemode;
	}

	public String getSnmpver() {
		return snmpver;
	}

	public void setSnmpver(String snmpver) {
		this.snmpver = snmpver;
	}
}
