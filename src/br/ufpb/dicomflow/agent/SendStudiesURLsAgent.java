package br.ufpb.dicomflow.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Registry;
import br.ufpb.dicomflow.bean.RegistryAccess;
import br.ufpb.dicomflow.bean.Study;
import br.ufpb.dicomflow.service.MessageService;
import br.ufpb.dicomflow.service.PersistentService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.UrlGeneratorIF;
import br.ufpb.dicomflow.util.Util;


public class SendStudiesURLsAgent implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("SENDING NEW STUDIES...");
		
		PersistentService persistentServiceDICOMMOVE = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		
		List<RegistryAccess> ras = persistentServiceDICOMMOVE.selectAll("status", Registry.OPEN, RegistryAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		Iterator<RegistryAccess> it = ras.iterator();
		while (it.hasNext()) {
			RegistryAccess registryAccess = (RegistryAccess) it.next();
			try {
				//TODO requisitar o certificado do dominio remoto
				
				String messageID = messageService.sendURL(registryAccess);
				registryAccess.setMessageID(messageID);
				registryAccess.setStatus(Registry.PENDING);
				registryAccess.setUploadAttempt(registryAccess.getUploadAttempt()+1);
				
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
				e.printStackTrace();
			}
			try {
				registryAccess.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
				e.printStackTrace();
				e.printStackTrace();
			}
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
	}


}