package com.hgc.shortenerbackend.entity.dto;

public class ErrorResponse {
    private String message;
    private String status;
	public ErrorResponse() {
		super();
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}
