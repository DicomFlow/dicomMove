package br.ufpb.dicomflow.agent;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufpb.dicomflow.bean.Registry;
import br.ufpb.dicomflow.service.FileService;
import br.ufpb.dicomflow.service.MessageService;
import br.ufpb.dicomflow.service.PersistentService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;

public class StoreStudiesAgent implements Job {
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("SEARCHING OPEN RECEIVED REGISTRIES...");	
		
		PersistentService persistentService = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		
		
		List<Registry> registries = persistentService.selectAllByParams(new Object[]{"type", "status"}, new Object[]{Registry.RECEIVED, Registry.OPEN}, Registry.class);
		
		Iterator<Registry> it = registries.iterator();
		while (it.hasNext()) {
			Registry registry = it.next();
			String url = registry.getLink();
			Util.getLogger(this).debug("URL FOUND : " + url);
			
			FileService fileService = ServiceLocator.singleton().getFileService();
			try {
				Util.getLogger(this).debug("DOWNLOADING DICOM OBJECT");
				
				fileService.extractZipFile(new URL(url));
				Util.getLogger(this).debug("STORING DICOM OBJECT");
				fileService.storeFile(new File(fileService.getExtractDir()));
				
				registry.setDownloadAttempt(registry.getDownloadAttempt()+1);
				registry.setStatus(Registry.CLOSED);
				Util.getLogger(this).debug("STORED DICOM OBJECT");
				
			} catch (Exception e) {
				registry.setDownloadAttempt(registry.getDownloadAttempt()+1);
				registry.setStatus(Registry.PENDING);
				Util.getLogger(this).error(e.getMessage(), e);
				e.printStackTrace();
			} 
			try {
				registry.save();
				messageService.sendResult(registry.getMessageID(), registry);
			} catch (ServiceException e) {
				Util.getLogger(this).error(e.getMessage(), e);
				e.printStackTrace();
			}
		}
		
		Util.getLogger(this).debug("DONE");
	}

}
