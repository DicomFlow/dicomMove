package br.ufpb.dicomflow.service.impl;

import br.ufpb.dicomflow.bean.Study;
import br.ufpb.dicomflow.service.UrlGeneratorIF;

public class DefaultURLGenerator implements UrlGeneratorIF {

	private String host;
	private int port;
	
	@Override
	public String getURL(Study study) {
		return "http://"+host+":"+port+"/DicomMove/rest/DownloadStudy/" + study.getStudyIuid();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	

}
