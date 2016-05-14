/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package br.ufpb.dicomflow.agent;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufpb.dicomflow.bean.Registry;
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
