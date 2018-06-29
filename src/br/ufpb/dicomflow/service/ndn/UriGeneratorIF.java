package br.ufpb.dicomflow.service.ndn;

public interface UriGeneratorIF {
	
	public String getPrefix();
	
	public String getURI(String studyIuid);
	
	public String getStudyIuid(String uri);
	
	public String getPrefix(String uri);
	
	public String getHost(String uri);

}
