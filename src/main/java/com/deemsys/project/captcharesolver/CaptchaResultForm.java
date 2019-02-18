package com.deemsys.project.captcharesolver;

public class CaptchaResultForm {

	private Integer status;
	private String captchaId;
	private String text;
	private String errorCode;
	private String errorMessage;
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getCaptchaId() {
		return captchaId;
	}
	public void setCaptchaId(String captchaId) {
		this.captchaId = captchaId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public CaptchaResultForm(Integer status, String captchaId, String text, String errorCode, String errorMessage) {
		super();
		this.status = status;
		this.captchaId = captchaId;
		this.text = text;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	public CaptchaResultForm() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
