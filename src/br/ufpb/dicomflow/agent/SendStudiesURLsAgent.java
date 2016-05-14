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

import java.util.Iterator;
import java.util.List;

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


public class SendStudiesURLsAgent implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("SENDING NEW STUDIES...");
		
		PersistentService persistentServiceDICOMMOVE = ServiceLocator.singleton().getPersistentService2();
		MessageService messageService = ServiceLocator.singleton().getMessageService();
		
		List<RegistryAccess> ras = persistentServiceDICOMMOVE.selectAll("status", Registry.OPEN, RegistryAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		Iterator<RegistryAccess> it = ras.iterator();
		while (it.hasNext()) {
			RegistryAccess registryAccess = (RegistryAccess) it.next();
			try {
				//TODO requisitar o certificado do dominio remoto
				
				String messageID = messageService.sendURL(registryAccess);
				registryAccess.setMessageID(messageID);
				registryAccess.setStatus(Registry.PENDING);
				registryAccess.setUploadAttempt(registryAccess.getUploadAttempt()+1);
				
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
				e.printStackTrace();
			}
			try {
				registryAccess.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
				e.printStackTrace();
				e.printStackTrace();
			}
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
	}


}