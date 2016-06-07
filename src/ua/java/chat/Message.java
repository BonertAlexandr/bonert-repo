package ua.java.chat;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ip = "";
	private String nickName = "";
	private String text = "";
	private String date = "";
	
	public Message(String text) {
		this.text = text;
		date = new Date().toString();
	}
	
	public void setIp(String ip) {
		this.ip = "(" + ip + ") ";
	}
	
	public void setNickname(String nickName) {
		this.nickName = nickName;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getNickName() {
		return this.nickName;
	}

	@Override
	public String toString() {
		return date + ip + nickName + ": " + text;
	}
}