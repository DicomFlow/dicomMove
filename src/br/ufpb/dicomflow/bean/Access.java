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

package br.ufpb.dicomflow.bean;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;


@Entity
@Table(name="access")
public class Access extends AbstractPersistence {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8784986036008582117L;
	
	public static final String CERIFICATE_OPEN = "OPEN";
	public static final String CERIFICATE_PENDING = "PENDING";
	public static final String CERIFICATE_CLOSED = "CLOSED";

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	private String mail;
	private String host;
	private Integer port;
	private String credential;
	private String certificateStatus;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_access")
	private Set<StorageServiceAccess> accessStorageServices;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_access")
	private Set<RequestServiceAccess> accessRequestServices;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_access")
	private Set<ServicePermission> servicePermissions;
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}
	
	public Set<StorageServiceAccess> getAccessStorageServices() {
		return accessStorageServices;
	}

	public void setAccessStorageServices(Set<StorageServiceAccess> accessStorageServices) {
		this.accessStorageServices = accessStorageServices;
	}
	
	public Set<RequestServiceAccess> getAccessRequestServices() {
		return accessRequestServices;
	}

	public void setAccessRequestServices(Set<RequestServiceAccess> accessRequestServices) {
		this.accessRequestServices = accessRequestServices;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getCertificateStatus() {
		return certificateStatus;
	}

	public void setCertificateStatus(String certificateStatus) {
		this.certificateStatus = certificateStatus;
	}
	

	public Set<ServicePermission> getServicePermissions() {
		return servicePermissions;
	}

	public void setServicePermissions(Set<ServicePermission> servicePermissions) {
		this.servicePermissions = servicePermissions;
	}

	@Override
	public void save() throws ServiceException{
		ServiceLocator.singleton().getPersistentService().saveOrUpdate(this);
	}
	
	@Override
	public void remove() throws ServiceException {
		ServiceLocator.singleton().getPersistentService().remove(this);
		
	}

}
