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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.RequestServiceAccess;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;


public class SendRequests {

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SENDING NEW STUDIES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		List<RequestServiceAccess> ras = persistentService.selectAll("status", RequestService.OPEN, RequestServiceAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		Iterator<RequestServiceAccess> it = ras.iterator();
		while (it.hasNext()) {
			RequestServiceAccess requestServiceAccess = (RequestServiceAccess) it.next();
			
			if(requestServiceAccess.getRequestService().getAction().equals(RequestService.PUT)){
				try {
					//TODO requisitar o certificado do dominio remoto
					Credential credential = CredentialUtil.getCredential(requestServiceAccess.getAccess(), CredentialUtil.getDomain());
					
					String messageID = messageService.sendRequest(requestServiceAccess, credential);
					requestServiceAccess.setMessageID(messageID);
					requestServiceAccess.setStatus(RequestService.PENDING);
					requestServiceAccess.setUploadAttempt(requestServiceAccess.getUploadAttempt()+1);
					
					
				} catch (ServiceException e) {
					Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
					e.printStackTrace();
				}
				try {
					requestServiceAccess.save();
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