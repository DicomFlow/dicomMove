package br.ufpb.dicomflow.service;

import br.ufpb.dicomflow.bean.Study;

public interface UrlGeneratorIF {
	
	public String getURL(Study study);
	
	public String getHost();
	
	public int getPort();

}
