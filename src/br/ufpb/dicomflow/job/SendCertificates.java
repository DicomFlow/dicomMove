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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.service.CertificateServiceIF;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;


public class SendCertificates {

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("REQUEST CERTIFICATES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		CertificateServiceIF certificateService =  ServiceLocator.singleton().getCertificateService();
		
		List<Access> accesses = persistentService.selectAll("certificateStatus", Access.CERIFICATE_OPEN, Access.class);
		
		
		Util.getLogger(this).debug("TOTAL ACCESS: " + accesses.size());
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = (Access) it.next();
			
			try {
				File certificate = certificateService.getCertificate();
				messageService.sendCertificate(certificate, access);
				access.setCertificateStatus(Access.CERIFICATE_PENDING);
				access.save();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
			
		}
		
		Util.getLogger(this).debug("DONE!!");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:SEND_CERTIFICATE - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

	

}