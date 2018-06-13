package br.ufpb.dicomflow.job.ndn;

import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.ndn.PrefixRegisterServiceIF;
import br.ufpb.dicomflow.util.Util;

public class VerifyPrefixRegisterService {
	
	public void execute(){
		
		Util.getLogger(this).debug("STARTING PREFIX REGISTRY...");	
		
		PrefixRegisterServiceIF prefixRegisterService = ServiceLocator.singleton().getPrefixRegisterService();
		
		if(!prefixRegisterService.isRunning()){
			Thread newThrd = new Thread(prefixRegisterService);
			newThrd.start();
			
			Util.getLogger(this).debug("PREFIX REGISTRY STARTED...");	
		}
		Util.getLogger(this).debug("PREFIX REGISTRY DONE...");
		
	}

}
