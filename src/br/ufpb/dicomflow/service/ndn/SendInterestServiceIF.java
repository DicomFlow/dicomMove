package br.ufpb.dicomflow.service.ndn;

public interface SendInterestServiceIF {
	
	public String processInterest(String uri);
	
	public int getMaxAttempts();
	
	public void setMaxAttempts(int maxAttempts);

}
