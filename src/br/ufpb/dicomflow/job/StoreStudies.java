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

package br.ufpb.dicomflow.job;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.service.FileServiceIF;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;

public class StoreStudies {
	
	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SEARCHING OPEN RECEIVED REGISTRIES...");	
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		
		List<StorageService> storageServices = persistentService.selectAllByParams(new Object[]{"type", "status", "action"}, new Object[]{StorageService.RECEIVED, StorageService.OPEN, StorageService.SAVE}, StorageService.class);
		
		Iterator<StorageService> itRegistries = storageServices.iterator();
		while (itRegistries.hasNext()) {
			StorageService registry = (StorageService) itRegistries.next();
			registry.setStatus(StorageService.LOCK);
			try {
				registry.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Não foi possível bloquear o registro"+e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
		
		Iterator<StorageService> it = storageServices.iterator();
		while (it.hasNext()) {
			StorageService storageService = it.next();
			
			//String url = registry.getLink();
			//TODO - remover  - inserido para testes			
			String url = "http://150.165.250.242:8081/dicomMove2/rest/DownloadStudy/2.16.840.1.113669.632.20.1211.10000324479";
			
			Util.getLogger(this).debug("URL FOUND : " + url);
			
			FileServiceIF fileService = ServiceLocator.singleton().getFileService();
			try {
				Util.getLogger(this).debug("DOWNLOADING DICOM OBJECT");
				
				fileService.extractZipFile(new URL(url),storageService.getId()+".zip");
				Util.getLogger(this).debug("STORING DICOM OBJECT");
				fileService.storeFile(new File(fileService.getExtractDir()));
				
				storageService.setDownloadAttempt(storageService.getDownloadAttempt()+1);
				storageService.setStatus(StorageService.CLOSED);
				Util.getLogger(this).debug("STORED DICOM OBJECT");
				
			} catch (Exception e) {
				storageService.setDownloadAttempt(storageService.getDownloadAttempt()+1);
				storageService.setStatus(StorageService.PENDING);
				Util.getLogger(this).error(e.getMessage(), e);
				e.printStackTrace();
			} 
			try {
				storageService.save();
				messageService.sendStorageResult(storageService.getMessageID(), storageService);
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
