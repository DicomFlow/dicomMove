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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.bean.StorageServiceAccess;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;


public class SendNDNStorages {

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SENDING NEW STUDIES...");
		System.out.println("SENDING NEW STUDIES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		List<StorageServiceAccess> ras = persistentService.selectAll("status", StorageService.OPEN, StorageServiceAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		System.out.println("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		Iterator<StorageServiceAccess> it = ras.iterator();
		while (it.hasNext()) {
			StorageServiceAccess storageServiceAccess = (StorageServiceAccess) it.next();
			
			if(storageServiceAccess.getStorageService().getAction().equals(StorageService.SAVE)){
				try {
					//TODO requisitar o certificado do dominio remoto
					Credential credential = CredentialUtil.getCredential(storageServiceAccess.getAccess(), CredentialUtil.getDomain());
					
					String messageID = messageService.sendStorage(storageServiceAccess, credential);
					storageServiceAccess.setMessageID(messageID);
					storageServiceAccess.setStatus(StorageService.PENDING);
					storageServiceAccess.setUploadAttempt(storageServiceAccess.getUploadAttempt()+1);
					
					
				} catch (ServiceException e) {
					Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
					e.printStackTrace();
				}
				try {
					storageServiceAccess.save();
				} catch (ServiceException e) {
					Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
					e.printStackTrace();
					e.printStackTrace();
				}
			}
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:SEND_STUDIES_URL - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}


}