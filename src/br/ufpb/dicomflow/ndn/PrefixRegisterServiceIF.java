package br.ufpb.dicomflow.ndn;

public interface PrefixRegisterServiceIF extends Runnable{

	public void processRegister() throws Exception;
}
