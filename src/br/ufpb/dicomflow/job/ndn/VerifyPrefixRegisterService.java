package br.ufpb.dicomflow.job.ndn;

import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.ndn.PrefixRegisterServiceIF;

public class VerifyPrefixRegisterService {
	
	public void execute(){
		
		PrefixRegisterServiceIF prefixRegisterService = ServiceLocator.singleton().getPrefixRegisterService();
		
		if(!prefixRegisterService.isRunning()){
			Thread newThrd = new Thread(prefixRegisterService);
			newThrd.start();
		}
		
		
	}

}
