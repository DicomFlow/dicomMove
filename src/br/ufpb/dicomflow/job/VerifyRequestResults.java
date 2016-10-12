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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.RequestServiceAccess;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;


public class VerifyRequestResults {

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SENDING PENDING NEW STUDIES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		List<RequestServiceAccess> ras = persistentService.selectAll("status", RequestService.PENDING, RequestServiceAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		Iterator<RequestServiceAccess> it = ras.iterator();
		while (it.hasNext()) {
			RequestServiceAccess requestServiceAccess = (RequestServiceAccess) it.next();
			if(requestServiceAccess.getRequestService().getAction().equals(RequestService.PUT)){
				requestServiceAccess.setUploadAttempt(requestServiceAccess.getUploadAttempt()+1);
				if(requestServiceAccess.getUploadAttempt() <= messageService.getMaxAttempts()){
					
					Map<String, String> results = new HashMap<String, String>();
					
					try {
						results = messageService.getRequestResults(null, null, requestServiceAccess.getMessageID());
						//TODO implementar o que fazer com os resultados do request.
					} catch (ServiceException e1) {
						Util.getLogger(this).error("Could not get results: " + e1.getMessage(),e1);
						e1.printStackTrace();
						//TODO verificar se avançar para o próximo é uma estratégia melhor
						//continue;
					}
					
					
						
					Iterator<String> itDomain = results.keySet().iterator();
					String domainStatus = null;
					while (itDomain.hasNext()) {
						String domain = (String) itDomain.next();
						if(requestServiceAccess.getAccess().getHost().equals(domain)){
							domainStatus = results.get(domain);
							break;
						}
					}
					//se domainStatus diferente de null, o domain recebeu a URL 
					if(domainStatus != null){
						requestServiceAccess.setStatus(RequestService.CLOSED);
						treatDomainStatus(domainStatus);
					} else {
						try {
							messageService.sendRequest(requestServiceAccess.getRequestService(), requestServiceAccess.getAccess());
							requestServiceAccess.setStatus(RequestService.PENDING);
						} catch (ServiceException e) {
							String status = requestServiceAccess.getUploadAttempt() == messageService.getMaxAttempts() ? RequestService.ERROR : RequestService.PENDING;
							requestServiceAccess.setStatus(status);
							Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
							e.printStackTrace();
						}
					}
					
					try {
						requestServiceAccess.save();
					} catch (ServiceException e) {
						Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
						e.printStackTrace();
					}							
					
				} else {
					requestServiceAccess.setStatus(RequestService.CLOSED);
					try {
						requestServiceAccess.save();
					} catch (ServiceException e) {
						Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
						e.printStackTrace();
					}
				}
			}
		}
		
		Util.getLogger(this).debug("DONE!!");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:SEND_PENDING_STUDIES_URL - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

	private void treatDomainStatus(String domainStatus) {
		// TODO Auto-generated method stub
		
	}


}