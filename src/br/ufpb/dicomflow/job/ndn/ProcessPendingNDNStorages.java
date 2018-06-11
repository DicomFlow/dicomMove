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

package br.ufpb.dicomflow.job.ndn;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.ndn.SendInterestServiceIF;
import br.ufpb.dicomflow.util.Util;

public class ProcessPendingNDNStorages {
	
	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SEARCHING PENDING RECEIVED REGISTRIES...");	
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		SendInterestServiceIF sendInterestService = ServiceLocator.singleton().getSendInterestService();
		
		
		List<StorageService> storageServices = persistentService.selectAllByParams(new Object[]{"type", "status", "action"}, new Object[]{StorageService.RECEIVED, StorageService.PENDING_PROCESS, StorageService.SAVE}, StorageService.class);
		
		Iterator<StorageService> itRegistries = storageServices.iterator();
		while (itRegistries.hasNext()) {
			StorageService registry = (StorageService) itRegistries.next();
			registry.setStatus(StorageService.LOCK_PROCESS);
			try {
				registry.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Não foi possível bloquear o registro"+e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
		
		Iterator<StorageService> it = storageServices.iterator();
		while (it.hasNext()) {
			StorageService storagService = it.next();
			storagService.setProcessAttempt(storagService.getProcessAttempt()+1);
			if(storagService.getProcessAttempt() <= sendInterestService.getMaxAttempts()){
				
				String uri = storagService.getLink();
				Util.getLogger(this).debug("URI FOUND : " + uri);
				
				Util.getLogger(this).debug("OPEN INTEREST.....");
				
				String url = sendInterestService.processInterest(uri);
				
				Util.getLogger(this).debug("RECEIVED DATA WITH REAL URL : " + url);
				
				try {
					
					URL aURL = new URL(url);
					storagService.setHost(aURL.getHost());
					storagService.setPort(aURL.getPort());
					
					storagService.setLink(url);
					storagService.setStatus(StorageService.PROCESSED);
					
				} catch (Exception e) {
					String status = storagService.getProcessAttempt() == sendInterestService.getMaxAttempts() ? StorageService.ERROR : StorageService.PENDING_PROCESS;
					storagService.setStatus(status);
					Util.getLogger(this).error(e.getMessage(), e);
					e.printStackTrace();
				} 
				
			}else{
				storagService.setStatus(StorageService.ERROR);
			}
			try {
				storagService.save();
				messageService.sendStorageResult(storagService.getMessageID(), storagService);
			} catch (ServiceException e) {
				Util.getLogger(this).error(e.getMessage(), e);
				e.printStackTrace();
			}
			
				
			}
			
		
		Util.getLogger(this).debug("DONE");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:STORE_PENDING_STUDIES - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

}
