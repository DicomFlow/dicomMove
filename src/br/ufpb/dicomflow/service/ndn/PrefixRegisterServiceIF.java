package br.ufpb.dicomflow.service.ndn;

public interface PrefixRegisterServiceIF extends Runnable{

	public void processRegister() throws Exception;
	
	public boolean isRunning();
}
