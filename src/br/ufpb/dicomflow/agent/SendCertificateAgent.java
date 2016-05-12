package br.ufpb.dicomflow.agent;

import java.io.File;
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
import br.ufpb.dicomflow.service.FileService;
import br.ufpb.dicomflow.service.MessageService;
import br.ufpb.dicomflow.service.PersistentService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.UrlGeneratorIF;
import br.ufpb.dicomflow.util.Util;


public class SendCertificateAgent implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("REQUEST CERTIFICATES...");
		
		PersistentService persistentServiceDICOMMOVE = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		FileService fileService =  ServiceLocator.singleton().getFileService();
		
		List<Access> accesses = persistentServiceDICOMMOVE.selectAll("certificateStatus", Access.CERIFICATE_OPEN, Access.class);
		
		
		Util.getLogger(this).debug("TOTAL ACCESS: " + accesses.size());
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = (Access) it.next();
			
			try {
				File certificate = fileService.getCertificate();
				messageService.sendCertificate(certificate, access);
				access.setCertificateStatus(Access.CERIFICATE_PENDING);
				access.save();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
	}

	

}