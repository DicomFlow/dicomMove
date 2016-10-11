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
import java.util.Map;
import java.util.Set;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.service.CertificateServiceIF;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;


public class StoreCertificate {

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("STORE CERTIFICATES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		CertificateServiceIF certificateService =  ServiceLocator.singleton().getCertificateService();
		
		Map<Access, byte[]> map = new HashMap<Access, byte[]>();
		try {
			map = messageService.getCertificates( null, null, null);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		Set<Access> accesses = map.keySet();
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = (Access) it.next();
			Access bdAccess = (Access) persistentService.selectByParams(new Object[]{"host","port","mail"}, new Object[]{access.getHost(), access.getPort(), access.getMail()}, Access.class);
			
				byte[] certificate = map.get(access);
				try {
					if(certificateService.storeCertificate(certificate, access.getHost())){
						if(bdAccess == null){
							access.setCertificateStatus(Access.CERIFICATE_OPEN);
							access.setCredential(Util.getCredential());
							access.save();
							messageService.sendCertificateResult(access, MessageServiceIF.CERTIFICATE_RESULT_CREATED);
						}else{
							if(bdAccess.getCredential() == null || bdAccess.getCredential().isEmpty()){
								bdAccess.setCredential(Util.getCredential());
							}
							messageService.sendCertificateResult(bdAccess, MessageServiceIF.CERTIFICATE_RESULT_UPDATED);
						}
					}else{
						messageService.sendCertificateResult(access, MessageServiceIF.CERTIFICATE_RESULT_ERROR);
					}
					
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			
			
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:STORE_CERTIFICATE - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

	

}