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
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.ServicePermission;
import br.ufpb.dicomflow.service.PersistentServiceIF;
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
			
			String accessLine;
			while ((accessLine = bufferedReader.readLine()) != null) {
				
				if(AccessRegexUtil.accessMatches(accessLine)){
					String mail = AccessRegexUtil.getMail(accessLine);
					String host = AccessRegexUtil.getHost(accessLine);
					String port = AccessRegexUtil.getPort(accessLine);

					Access access = (Access) persistentService.selectByParams(new Object[]{"mail", "host", "port"}, new Object[]{mail, host, new Integer(port)}, Access.class);
					if(access == null){
						access = buildAccess(mail, host, port);
						access.save();
					}
					List<ServicePermission> servicePermissions = persistentService.selectAll("access", access, ServicePermission.class);
					
					String permissions = AccessRegexUtil.getPermissions(accessLine);
					int count = AccessRegexUtil.countPermissions(permissions);
					for (int i = 0; i < count; i++) {
						String permission = AccessRegexUtil.getPermission(permissions, i);
						ServicePermission servicePermission = buildServicePermission(access, permission);
						if(servicePermission != null){
							servicePermission.save();
						}
					}
					
					Iterator<ServicePermission> it = servicePermissions.iterator();
					while (it.hasNext()) {
						ServicePermission servicePermission = (ServicePermission) it.next();
						servicePermission.remove();
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

	private Access buildAccess(String mail, String host, String port) {
		
		Access access = new Access();
		access.setMail(mail);
		access.setHost(host);
		access.setPort(new Integer(port));
		access.setCertificateStatus(Access.CERIFICATE_OPEN);
		
		return access;
	}

	private ServicePermission buildServicePermission(Access access, String permission) {
		
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
