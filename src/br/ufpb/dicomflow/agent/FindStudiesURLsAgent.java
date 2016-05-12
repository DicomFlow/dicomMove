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

public class FindStudiesURLsAgent implements Job {
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("SEARCHING URLs...");	
		
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		
		Map<String,String> urls = new HashMap<String, String>(); 
		try {
			urls = messageService.getURLs(null, null, null);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		Iterator<String> it = urls.keySet().iterator();
		while (it.hasNext()) {
			String messageID = it.next();
			String url = urls.get(messageID);
			Util.getLogger(this).debug("URL FOUND : " + url);
			
			PersistentService persistentService = ServiceLocator.singleton().getPersistentService2();
			Registry registry = (Registry) persistentService.selectByParams(new Object[]{"link", "type"},new Object[]{url,Registry.RECEIVED}, Registry.class);
			if(registry == null){
				
				registry = new Registry();
				registry.setLink(url);
				try {
					URL aURL = new URL(url);
					registry.setHost(aURL.getHost());
					registry.setPort(aURL.getPort());
				} catch (MalformedURLException e) {
					Util.getLogger(this).error(e.getMessage(), e);
					e.printStackTrace();
				}
				registry.setStatus(Registry.OPEN);
				registry.setType(Registry.RECEIVED);
				registry.setDownloadAttempt(0);
				
			}

			try {
				registry.save();
				messageService.sendResult(messageID, registry);
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
			
			
//			FileService fileService = ServiceLocator.singleton().getFileService();
//			try {
//				fileService.extractZipFile(new URL(url));
//				fileService.storeFile(new File(fileService.getExtractDir()));
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (ServiceException e) {
//				e.printStackTrace();
//			}
		}
		
		Util.getLogger(this).debug("DONE");
	}

}
