package com.deemsys.project.captcharesolver;

public class CaptchaResolverForm {
	
	private String username;
	private String password;
	private String captchafile;
	private String captchaId;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCaptchafile() {
		return captchafile;
	}
	public void setCaptchafile(String captchafile) {
		this.captchafile = captchafile;
	}
	public CaptchaResolverForm(String username, String password, String captchafile,String captchaId) {
		super();
		this.username = username;
		this.password = password;
		this.captchafile = captchafile;
		this.captchaId=captchaId;
	}
	public CaptchaResolverForm() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getCaptchaId() {
		return captchaId;
	}
	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}
	
	
	

}
