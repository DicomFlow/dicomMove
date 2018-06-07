package br.ufpb.dicomflow.service.ndn;

public interface RouteRegisterServiceIF{

	public void processRoute(String protocol, String host, String port, String prefix);
	
	public void processRoute(String uri, String prefix);
}
