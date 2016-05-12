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


public class FindCertificateResultAgent implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("STORE CERTIFICATES...");
		
		PersistentService persistentServiceDICOMMOVE = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		
		Map<Access, String> map = new HashMap<Access, String>();
		try {
			map = messageService.getCertificateResults(null, null, null);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		Set<Access> accesses = map.keySet();
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			
			Access access = (Access) it.next();
			String result = map.get(access);
			
			if(result.equals(MessageService.CERTIFICATE_RESULT_CREATED)|| result.equals(MessageService.CERTIFICATE_RESULT_UPDATED)){
				Access bdAccess = (Access) persistentServiceDICOMMOVE.selectByParams(new Object[]{"host","port","mail"}, new Object[]{access.getHost(), access.getPort(), access.getMail()}, Access.class);
				bdAccess.setCredential(access.getCredential());
				bdAccess.setCertificateStatus(Access.CERIFICATE_CLOSED);
				try {
					bdAccess.save();
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			
			
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
	}

	

}