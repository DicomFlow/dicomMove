package br.ufpb.dicomflow.ws;

import java.util.Date;

public class CommonRequest {
		
	private String host;
	private String contentLegth;
	private String contentType;
	private String contentMD5;	
	private Date date;	
	private String authorization;
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getContentLegth() {
		return contentLegth;
	}
	public void setContentLegth(String contentLegth) {
		this.contentLegth = contentLegth;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date responseDate) {
		this.date = responseDate;
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getContentMD5() {
		return contentMD5;
	}
	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}	
}
