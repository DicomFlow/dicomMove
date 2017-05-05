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

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;

public class FindStorages {
	
	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SEARCHING URLs...");
		System.out.println("SEARCHING URLs...");
		
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		List<StorageService> storages = new ArrayList<>(); 
		try {
			storages = messageService.getStorages(null, null, null);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		Iterator<StorageService> it = storages.iterator();
		while (it.hasNext()) {
			StorageService storage = it.next();
			String originalMessageID = storage.getMessageID();
			String url = storage.getLink();
			Util.getLogger(this).debug("URL FOUND : " + url);
			System.out.println("URL FOUND : " + url);
			
			PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
			StorageService storageService = (StorageService) persistentService.selectByParams(new Object[]{"link", "type", "action"},new Object[]{url,StorageService.RECEIVED, StorageService.SAVE}, StorageService.class);
			if(storageService == null){
				
				storageService = new StorageService();
				storageService.setLink(url);
				try {
					URL aURL = new URL(url);
					storageService.setMessageID(originalMessageID);
					storageService.setHost(aURL.getHost());
					storageService.setPort(aURL.getPort());
				} catch (MalformedURLException e) {
					Util.getLogger(this).error(e.getMessage(), e);
					e.printStackTrace();
				}
				storageService.setStatus(StorageService.OPEN);
				storageService.setAction(StorageService.SAVE);
				storageService.setType(StorageService.RECEIVED);
				storageService.setDownloadAttempt(0);
				
				try {
					storageService.save();
					messageService.sendStorageResult(originalMessageID, storageService);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				
			}

		}
		
		Util.getLogger(this).debug("DONE");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:FIND_STUDIES_URL - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

}
