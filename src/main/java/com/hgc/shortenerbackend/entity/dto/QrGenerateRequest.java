package com.hgc.shortenerbackend.entity.dto;

public class QrGenerateRequest {
    private String linkToQr;
    private String baseUrl;

    public QrGenerateRequest() {
    }


	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}


	public String getLinkToQr() {
		return linkToQr;
	}


	public void setLinkToQr(String linkToQr) {
		this.linkToQr = linkToQr;
	}
}
