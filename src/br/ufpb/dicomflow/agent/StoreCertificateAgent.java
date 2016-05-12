package br.ufpb.dicomflow.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


public class StoreCertificateAgent implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("STORE CERTIFICATES...");
		
		PersistentService persistentServiceDICOMMOVE = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		FileService fileService =  ServiceLocator.singleton().getFileService();
		
		Map<Access, byte[]> map = new HashMap<Access, byte[]>();
		try {
			map = messageService.getCertificates( null, null, null);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		Set<Access> accesses = map.keySet();
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = (Access) it.next();
			Access bdAccess = (Access) persistentServiceDICOMMOVE.selectByParams(new Object[]{"host","port","mail"}, new Object[]{access.getHost(), access.getPort(), access.getMail()}, Access.class);
			
				byte[] certificate = map.get(access);
				try {
					if(fileService.storeCertificate(certificate, access.getHost())){
						if(bdAccess == null){
							access.setCertificateStatus(Access.CERIFICATE_OPEN);
							access.setCredential(Util.getCredential());
							access.save();
							messageService.sendCertificateResult(access, MessageService.CERTIFICATE_RESULT_CREATED);
						}else{
							if(bdAccess.getCredential() == null || bdAccess.getCredential().isEmpty()){
								bdAccess.setCredential(Util.getCredential());
							}
							messageService.sendCertificateResult(bdAccess, MessageService.CERTIFICATE_RESULT_UPDATED);
						}
					}else{
						messageService.sendCertificateResult(access, MessageService.CERTIFICATE_RESULT_ERROR);
					}
					
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			
			
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
	}

	

}