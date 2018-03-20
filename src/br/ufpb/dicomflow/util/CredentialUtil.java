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
package br.ufpb.dicomflow.util;

import java.util.UUID;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.service.ServiceLocator;

public class CredentialUtil {

	public static String generateCredentialKey(){
		return UUID.randomUUID().toString();
		
	}
	
	public static Access getDomain(){
		return (Access) ServiceLocator.singleton().getPersistentService().select("type", Access.IN, Access.class);
	}
	
	/**
	 * Create a credential for Access with type equals OUT. Other case returns NULL.
	 * @param access
	 * @return
	 */
	public static Credential createCredential(Access access) {
		
		Credential credential = new Credential();
		credential.setKeypass(generateCredentialKey());
		credential.setOwner(access);
		credential.setDomain(getDomain());
		return credential;
		
	}
	
	public static Access createAccess(String mail, String host, String port, String type) {

		Access access = new Access();
		access.setMail(mail);
		access.setHost(host);
		access.setPort(new Integer(port));
		access.setType(type);
		access.setCertificateStatus(Access.CERTIFICATE_CLOSED);

		return access;
	}
	
	public static Access getAcess(String host){		
		return (Access) ServiceLocator.singleton().getPersistentService().select("host", host, Access.class);		
	}
	
	public static Credential getCredential(Access owner, Access domain){
		return (Credential) ServiceLocator.singleton().getPersistentService().selectByParams(new Object[]{"owner", "domain" }, new Object[]{owner, domain}, Credential.class);
	}

	

}
