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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.service.CertificateServiceIF;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;


public class VerifyCertificateResult {

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("STORE CERTIFICATES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		CertificateServiceIF certificateService =  ServiceLocator.singleton().getCertificateService();
		
		Access domain = CredentialUtil.getDomain();
		
		List<Access> accesses = new ArrayList<Access>();
		try {
			accesses = messageService.getCertificateResults(null, null, null);
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			
			Access access = (Access) it.next();
			String result = access.getCertificateStatus();
			
			if(result.equals(MessageServiceIF.CERTIFICATE_RESULT_CREATED)|| result.equals(MessageServiceIF.CERTIFICATE_RESULT_UPDATED)){
				
				byte[] accessCertificate = access.getCertificate();
				
				try {
					if(certificateService.storeCertificate(accessCertificate, access.getHost())){
						Access bdAccess = (Access) persistentService.selectByParams(new Object[]{"host","port","mail"}, new Object[]{access.getHost(), access.getPort(), access.getMail()}, Access.class);
						bdAccess.setCertificateStatus(Access.CERTIFICATE_CLOSED);
						bdAccess.save();
						
						Credential credential = access.getDomainCredential(0);
						if(credential != null){
							credential.setOwner(domain);
							credential.setDomain(bdAccess);
							credential.save();
						}
						
						Credential accessCredential = CredentialUtil.getCredential(bdAccess, domain);
						messageService.sendCertificateConfirm(access.getMail(), domain, MessageServiceIF.CERTIFICATE_RESULT_UPDATED, accessCredential);
						
					}
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			}
			
			
			
		}
		
		Util.getLogger(this).debug("DONE!!");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:FIND_SERTIFICATE_RESULT - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

	

}