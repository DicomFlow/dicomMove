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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufpb.dicomflow.bean.Registry;
import br.ufpb.dicomflow.bean.RegistryAccess;
import br.ufpb.dicomflow.service.MessageService;
import br.ufpb.dicomflow.service.PersistentService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;


public class SendPendingStudiesURLsAgent implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SENDING PENDING NEW STUDIES...");
		
		PersistentService persistentServiceDICOMMOVE = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		
		List<RegistryAccess> ras = persistentServiceDICOMMOVE.selectAll("status", Registry.PENDING, RegistryAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		Iterator<RegistryAccess> it = ras.iterator();
		while (it.hasNext()) {
			RegistryAccess registryAccess = (RegistryAccess) it.next();
			registryAccess.setUploadAttempt(registryAccess.getUploadAttempt()+1);
			if(registryAccess.getUploadAttempt() <= messageService.getMaxAttempts()){
				
				Map<String, String> results = new HashMap<String, String>();
				
				try {
					results = messageService.getResults(null, null, registryAccess.getMessageID());
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
					if(registryAccess.getAccess().getHost().equals(domain)){
						domainStatus = results.get(domain);
						break;
					}
				}
				//se domainStatus diferente de null, o domain recebeu a URL 
				if(domainStatus != null){
					registryAccess.setStatus(Registry.CLOSED);
					treatDomainStatus(domainStatus);
				} else {
					try {
						messageService.sendURL(registryAccess.getRegistry(), registryAccess.getAccess());
						registryAccess.setStatus(Registry.PENDING);
					} catch (ServiceException e) {
						String status = registryAccess.getUploadAttempt() == messageService.getMaxAttempts() ? Registry.ERROR : Registry.PENDING;
						registryAccess.setStatus(status);
						Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
						e.printStackTrace();
					}
				}
				
				try {
					registryAccess.save();
				} catch (ServiceException e) {
					Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
					e.printStackTrace();
				}							
				
			} else {
				registryAccess.setStatus(Registry.CLOSED);
				try {
					registryAccess.save();
				} catch (ServiceException e) {
					Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
					e.printStackTrace();
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