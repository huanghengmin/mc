package com.inetec.ichange.mc.pojo;

import com.avdheshyadav.p4j.jdbc.model.Column;
import com.avdheshyadav.p4j.jdbc.model.Entity;


@Entity(schema="app",table="snmpoid")
public class SnmpOIDBean {
	@Column(isPrimaryKey = true)
	private String name;
	@Column
	private String type;
	@Column
	private String company;
	@Column
	private String snmpver;
	@Column 
	private String cpuuse;
	@Column 
	private String disktotal;
	@Column 
	private String diskuse;
	@Column 
	private String memtotal;
	@Column 
	private String memuse;
	@Column 
	private String curconn;
	
	
	
	

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCompany() {
		return company;
	}


	public void setCompany(String company) {
		this.company = company;
	}


	public String getCpuuse() {
		return cpuuse;
	}


	public void setCpuuse(String cpuuse) {
		this.cpuuse = cpuuse;
	}


	public String getDisktotal() {
		return disktotal;
	}


	public void setDisktotal(String disktotal) {
		this.disktotal = disktotal;
	}


	public String getDiskuse() {
		return diskuse;
	}


	public void setDiskuse(String diskuse) {
		this.diskuse = diskuse;
	}


	public String getMemtotal() {
		return memtotal;
	}


	public void setMemtotal(String memtotal) {
		this.memtotal = memtotal;
	}


	public String getMemuse() {
		return memuse;
	}


	public void setMemuse(String memuse) {
		this.memuse = memuse;
	}


	public String getCurconn() {
		return curconn;
	}


	public void setCurconn(String curconn) {
		this.curconn = curconn;
	}


	public SnmpOIDBean() {
	}




	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSnmpver() {
		return snmpver;
	}

	public void setSnmpver(String snmpver) {
		this.snmpver = snmpver;
	}
}
