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

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SEARCHING OPEN RECEIVED REGISTRIES...");	
		
		PersistentService persistentService = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		
		
		List<Registry> registries = persistentService.selectAllByParams(new Object[]{"type", "status"}, new Object[]{Registry.RECEIVED, Registry.OPEN}, Registry.class);
		
		Iterator<Registry> itRegistries = registries.iterator();
		while (itRegistries.hasNext()) {
			Registry registry = (Registry) itRegistries.next();
			registry.setStatus(Registry.LOCK);
			try {
				registry.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("N�o foi poss�vel bloquear o registro"+e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
		
		Iterator<Registry> it = registries.iterator();
		while (it.hasNext()) {
			Registry registry = it.next();
			
			//String url = registry.getLink();
			//TODO - remover  - inserido para testes			
			String url = "http://150.165.202.39:8081/dicomMove2/rest/DownloadStudy/2.16.840.1.113669.632.20.1211.10000324479";
			
			Util.getLogger(this).debug("URL FOUND : " + url);
			
			FileService fileService = ServiceLocator.singleton().getFileService();
			try {
				Util.getLogger(this).debug("DOWNLOADING DICOM OBJECT");
				
				fileService.extractZipFile(new URL(url),registry.getId()+".zip");
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
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:STORE_STUDIES - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

}
