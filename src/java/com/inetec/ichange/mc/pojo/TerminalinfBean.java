package com.inetec.ichange.mc.pojo;

import java.util.Date;

import com.avdheshyadav.p4j.jdbc.model.Column;
import com.avdheshyadav.p4j.jdbc.model.Entity;

@Entity(schema="app",table="terminalinf")
public class TerminalinfBean {
	@Column(isPrimaryKey = true)
	private long id;
	@Column
	private String idSystem;
	@Column
	private String idTerminal;
	@Column
	private String terminalName;
	@Column
	private String terminalType;
	@Column
	private String terminalOutLink;
	@Column
	private String terminalOS;
	@Column
	private String terminalBand;
	@Column
	private String cardType;
	@Column
	private String cardName;
	@Column
	private String card_version;
	@Column
	private String userId;
	@Column
	private String userName;
	@Column
	private String userDepart;
	@Column
	private String userZone;
	@Column
	private String policeNumber;
	@Column
	private Date regTime;
	@Column
	private String ifcancel;
	@Column
	private String flag;
	@Column
	private long lastDate;
	@Column
	private String ip;

	/**
	 * 是否阻断
	 */
	@Column
	private boolean isBlock=false;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIdSystem() {
		return idSystem;
	}

	public void setIdSystem(String idSystem) {
		this.idSystem = idSystem;
	}

	public String getIdTerminal() {
		return idTerminal;
	}

	public void setIdTerminal(String idTerminal) {
		this.idTerminal = idTerminal;
	}

	public String getTerminalName() {
		return terminalName;
	}

	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public String getTerminalOutLink() {
		return terminalOutLink;
	}

	public void setTerminalOutLink(String terminalOutLink) {
		this.terminalOutLink = terminalOutLink;
	}

	public String getTerminalOS() {
		return terminalOS;
	}

	public void setTerminalOS(String terminalOS) {
		this.terminalOS = terminalOS;
	}

	public String getTerminalBand() {
		return terminalBand;
	}

	public void setTerminalBand(String terminalBand) {
		this.terminalBand = terminalBand;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCard_version() {
		return card_version;
	}

	public void setCard_version(String card_version) {
		this.card_version = card_version;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserDepart() {
		return userDepart;
	}

	public void setUserDepart(String userDepart) {
		this.userDepart = userDepart;
	}

	public String getUserZone() {
		return userZone;
	}

	public void setUserZone(String userZone) {
		this.userZone = userZone;
	}

	public String getPoliceNumber() {
		return policeNumber;
	}

	public void setPoliceNumber(String policeNumber) {
		this.policeNumber = policeNumber;
	}

	public Date getRegTime() {
		return regTime;
	}

	public void setRegTime(Date regTime) {
		this.regTime = regTime;
	}

	public String getIfcancel() {
		return ifcancel;
	}

	public void setIfcancel(String ifcancel) {
		this.ifcancel = ifcancel;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setLastDate(long lastDate) {
		this.lastDate = lastDate;
	}

	public long getLastDate() {
		return lastDate;
	}
    
	
	public boolean isBlock() {
		return isBlock;
	}

	public void setBlock(boolean isBlock) {
		this.isBlock = isBlock;
	}
}
