package br.ufpb.dicomflow.service;

public interface UriGeneratorIF {
	
	public String getPrefix();
	
	public String getURI(String studyIuid);
	
	public String getStudyIuid(String uri);

}
