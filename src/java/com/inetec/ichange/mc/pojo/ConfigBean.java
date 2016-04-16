package com.inetec.ichange.mc.pojo;


import com.avdheshyadav.p4j.jdbc.model.Column;
import com.avdheshyadav.p4j.jdbc.model.Entity;

@Entity(schema="app",table="config")
public class ConfigBean {
	@Column(isPrimaryKey = true)
	public String proteId="mc";
	@Column
	public String descr;
	
	@Column
	public String ip;
	@Column
	public String port;
	@Column
	public String sysport;
	@Column
	public String cmsip;
	@Column
	public String cmsport;
	@Column
	public String cmssysport;

	public String getSysport() {
		return sysport;
	}

	public void setSysport(String sysport) {
		this.sysport = sysport;
	}

	public String getCmssysport() {
		return cmssysport;
	}

	public void setCmssysport(String cmssysport) {
		this.cmssysport = cmssysport;
	}

	public String getProteId() {
		return proteId;
	}

	public void setProteId(String proteId) {
		this.proteId = proteId;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String desc) {
		this.descr = desc;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getCmsip() {
		return cmsip;
	}

	public void setCmsip(String cmsip) {
		this.cmsip = cmsip;
	}

	public String getCmsport() {
		return cmsport;
	}

	public void setCmsport(String cmsport) {
		this.cmsport = cmsport;
	}
	

}
