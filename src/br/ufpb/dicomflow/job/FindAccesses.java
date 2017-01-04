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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.aop.ThrowsAdvice;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.ServicePermission;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.AccessRegexUtil;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;

public class FindAccesses {
	
	private static final int MAX_IN_ACCESS = 1;

	private String accessConfigFile;
	private int inTypeTotal = 0;

	public void execute(){
		inTypeTotal = 0;
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		try{

			File file = new File(accessConfigFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			//read each access into config file
			String accessLine;
			int lineNumber = 0;
			int totalAccess = 0;
			List<Long> idAccesses = new ArrayList<>();
			while ((accessLine = bufferedReader.readLine()) != null) {
				
				lineNumber++;
				//the first line must be a valid Access with type equals IN
				if((lineNumber == 1 && !AccessRegexUtil.accessMatches(accessLine)) ||
				   (lineNumber == 1 &&	AccessRegexUtil.accessMatches(accessLine) &&  !AccessRegexUtil.getType(accessLine).equals(AccessRegexUtil.IN))){
					throw new Exception("Access Config File invalid format: First line must contain a valid Access with type equals IN"); 
				}

				//include all Access with type equals OUT and only one Access with type equals IN
				if(AccessRegexUtil.accessMatches(accessLine) && isValidType(AccessRegexUtil.getType(accessLine))){
					String type = AccessRegexUtil.getType(accessLine);
					String mail = AccessRegexUtil.getMail(accessLine);
					String host = AccessRegexUtil.getHost(accessLine);
					String port = AccessRegexUtil.getPort(accessLine);
					
					

					//check access into DB
					Access access = (Access) persistentService.selectByParams(new Object[]{"mail", "host", "port", "type"}, new Object[]{mail, host, new Integer(port), type}, Access.class);

					//if access does not exists, create new access and permissions
					if(access == null){

						access = CredentialUtil.createAccess(mail, host, port, type);
						access.save();
						//create credential and permissions for Access with type equals OUT
						if(access.getType().equals(Access.OUT)){
							Credential credential = CredentialUtil.createCredential(access);
							credential.save();

							List<ServicePermission> newPermissions = readPermissions(credential, accessLine);
						
							savePermissions(newPermissions);
						}
						
						idAccesses.add(access.getId());
						totalAccess++;
					
						//if access exists and has type equals IN only counts
					}else if(access.getType().equals(Access.IN)){
						
						idAccesses.add(access.getId());
						totalAccess++;
				
					//if access exists and has type equals OUT, load credential and existing permissions. 
					//Update the existing permissions that are in the new permissions list, remove the ones that are not in the list
					//Create the remaining permissions that are list of new permissions
					}else if(access.getType().equals(Access.OUT)){
						Credential credential = CredentialUtil.getCredential(access, CredentialUtil.getDomain());

						if(credential == null){
							credential = CredentialUtil.createCredential(access);
							credential.save();
						}
						List<ServicePermission> newPermissions = readPermissions(credential, accessLine);

						   
						List<ServicePermission> servicePermissions = persistentService.selectAll("credential", credential, ServicePermission.class);
						Iterator<ServicePermission> it = servicePermissions.iterator();
						while (it.hasNext()) {

							boolean updated = false;
							List<ServicePermission> remainingPermissions = new ArrayList<ServicePermission>();

							ServicePermission servicePermission = (ServicePermission) it.next();

							Iterator<ServicePermission> it2 = newPermissions.iterator();
							while (it2.hasNext()) {
								ServicePermission newPermission = (ServicePermission) it2.next();
								
								if(newPermission.getDescription().equals(servicePermission.getDescription())){
									if(!newPermission.getModalities().equals(servicePermission.getModalities())){
										servicePermission.setModalities(newPermission.getModalities());
										servicePermission.save();
									}
									updated = true;
								}else{
									remainingPermissions.add(newPermission);
								}
							}

							
							if(!updated){
								servicePermission.remove();
							}
							newPermissions = remainingPermissions;

						}
						
						savePermissions(newPermissions);
						
						idAccesses.add(access.getId());
						totalAccess++;
					}
				}
			}
			bufferedReader.close();
			//remove access that is not in config access file 
			List<Access> invalidAccesses  = persistentService.selectAllNotIn("id", idAccesses, Access.class);
			removeAccesses(invalidAccesses);
			
			Util.getLogger(this).debug("Total acess read: " + lineNumber + ". Total access saved: " + totalAccess);

		}catch(Exception e){
			Util.getLogger(this).debug("accessConfigFile is invalid: " + accessConfigFile);
			Util.getLogger(this).error(e.getMessage());
			e.printStackTrace();
			
		}

	}

	private void removeAccesses(List<Access> invalidAccesses) throws ServiceException {
		Iterator<Access> it = invalidAccesses.iterator();
		while (it.hasNext()) {
			Access access = (Access) it.next();
			access.remove();
		}
		
	}

	private boolean isValidType(String type) {
		if(type.equals(AccessRegexUtil.IN)){
			inTypeTotal++;
			return inTypeTotal <= MAX_IN_ACCESS;
		}else{
			return true;
		}
		
	}

	private void savePermissions(List<ServicePermission> newPermissions) throws ServiceException {
		Iterator<ServicePermission> it = newPermissions.iterator();
		while (it.hasNext()) {
			ServicePermission newPermission = (ServicePermission) it.next();
			newPermission.save();
		}
	}

	private List<ServicePermission> readPermissions(Credential credential, String accessLine) {

		ArrayList<ServicePermission> servicePermissions = new ArrayList<ServicePermission>();

		String permissions = AccessRegexUtil.getPermissions(accessLine);

		int count = AccessRegexUtil.countPermissions(permissions);
		for (int i = 0; i < count; i++) {

			String permission = AccessRegexUtil.getPermission(permissions, i);
			ServicePermission servicePermission = createServicePermission(credential, permission);

			if(servicePermission != null)
				servicePermissions.add(servicePermission);
		}

		return servicePermissions;
	}

	
	

	private ServicePermission createServicePermission(Credential credential, String permission) {

		StringTokenizer tokenizer = new StringTokenizer(permission, " ");
		ServicePermission servicePermission = null;

		if(tokenizer.countTokens() == 2){
			servicePermission = new ServicePermission();
			servicePermission.setCredential(credential);
			servicePermission.setDescription(tokenizer.nextToken());
			servicePermission.setModalities(tokenizer.nextToken());
		}


		return servicePermission;
	}

	public String getAccessConfigFile() {
		return accessConfigFile;
	}

	public void setAccessConfigFile(String accessConfigFile) {
		this.accessConfigFile = accessConfigFile;
	}



}
