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

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.ServicePermission;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.AccessRegexUtil;
import br.ufpb.dicomflow.util.Util;

public class FindAccesses {

	private String accessConfigFile;

	public void execute(){

		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		try{

			File file = new File(accessConfigFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			//read each access into config file
			String accessLine;
			while ((accessLine = bufferedReader.readLine()) != null) {

				if(AccessRegexUtil.accessMatches(accessLine)){
					String mail = AccessRegexUtil.getMail(accessLine);
					String host = AccessRegexUtil.getHost(accessLine);
					String port = AccessRegexUtil.getPort(accessLine);

					//check access into DB
					Access access = (Access) persistentService.selectByParams(new Object[]{"mail", "host", "port"}, new Object[]{mail, host, new Integer(port)}, Access.class);

					//if access does not exists, create new access and permissions
					if(access == null){

						access = createAccess(mail, host, port);
						access.save();

						List<ServicePermission> newPermissions = readPermissions(access, accessLine);
						
						savePermissions(newPermissions);

					//if access exists, load existing permissions. 
					//Update the existing permissions that are in the new permissions list, remove the ones that are not in the list
					//Create the remaining permissions that are list of new permissions
					}else{

						List<ServicePermission> newPermissions = readPermissions(access, accessLine);

						   
						List<ServicePermission> servicePermissions = persistentService.selectAll("access", access, ServicePermission.class);
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

					}
				}
			}
			bufferedReader.close();

		}catch(Exception e){
			Util.getLogger(this).debug("accessConfigFile is invalid: " + accessConfigFile);
			Util.getLogger(this).error(e.getMessage());
			e.printStackTrace();
		}

	}

	private void savePermissions(List<ServicePermission> newPermissions) throws ServiceException {
		Iterator<ServicePermission> it = newPermissions.iterator();
		while (it.hasNext()) {
			ServicePermission newPermission = (ServicePermission) it.next();
			newPermission.save();
		}
	}

	private List<ServicePermission> readPermissions(Access access, String accessLine) {

		ArrayList<ServicePermission> servicePermissions = new ArrayList<ServicePermission>();

		String permissions = AccessRegexUtil.getPermissions(accessLine);

		int count = AccessRegexUtil.countPermissions(permissions);
		for (int i = 0; i < count; i++) {

			String permission = AccessRegexUtil.getPermission(permissions, i);
			ServicePermission servicePermission = createServicePermission(access, permission);

			if(servicePermission != null)
				servicePermissions.add(servicePermission);
		}

		return servicePermissions;
	}

	private Access createAccess(String mail, String host, String port) {

		Access access = new Access();
		access.setMail(mail);
		access.setHost(host);
		access.setPort(new Integer(port));
		access.setCertificateStatus(Access.CERIFICATE_OPEN);

		return access;
	}

	private ServicePermission createServicePermission(Access access, String permission) {

		StringTokenizer tokenizer = new StringTokenizer(permission, " ");
		ServicePermission servicePermission = null;

		if(tokenizer.countTokens() == 2){
			servicePermission = new ServicePermission();
			servicePermission.setAccess(access);
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
