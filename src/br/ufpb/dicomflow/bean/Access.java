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

import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.Transient;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;


@Entity
@Table(name="access")
public class Access extends AbstractPersistence {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8784986036008582117L;
	
	public static final String IN = "IN";
	public static final String OUT = "OUT";
	
	
	public static final String CERTIFICATE_OPEN = "OPEN";
	public static final String CERTIFICATE_PENDING = "PENDING";
	public static final String CREDENTIAL_PENDING = "CREDENTIAL_PENDING";
	public static final String CERTIFICATE_CLOSED = "CLOSED";

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	private String code;
	private String mail;
	private String host;
	private Integer port;
	private String certificateStatus;
	private String type;
	
	@Transient
	private byte[] certificate;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_owner")
	private Set<Credential> ownerCredentials;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_domain")
	private Set<Credential> domainCredentials;
	
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_access")
	private Set<StorageServiceAccess> accessStorageServices;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_access")
	private Set<RequestServiceAccess> accessRequestServices;
	
	
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void save() throws ServiceException{
		ServiceLocator.singleton().getPersistentService().saveOrUpdate(this);
	}
	
	@Override
	public void remove() throws ServiceException {
		ServiceLocator.singleton().getPersistentService().remove(this);
		
	}

	public Set<Credential> getOwnerCredentials() {
		return ownerCredentials;
	}

	public void setOwnerCredentials(Set<Credential> ownerCredentials) {
		this.ownerCredentials = ownerCredentials;
	}

	public Set<Credential> getDomainCredentials() {
		return domainCredentials;
	}

	public void setDomainCredentials(Set<Credential> domainCredentials) {
		this.domainCredentials = domainCredentials;
	}

	public byte[] getCertificate() {
		return certificate;
	}

	public void setCertificate(byte[] certificate) {
		this.certificate = certificate;
	}
	
	public Credential getDomainCredential(int index){
		Credential credential = null;
		
		if (index < domainCredentials.size()) {
			
			Iterator<Credential> it = domainCredentials.iterator();
			for (int i = 0; i <= index; i++) {
				credential = it.next();
			}
			
		}
		
		return credential;
	}
	
	public Credential getOwnerCredential(int index){
		Credential credential = null;
		
		if (index < ownerCredentials.size()) {
			
			Iterator<Credential> it = ownerCredentials.iterator();
			for (int i = 0; i <= index; i++) {
				credential = it.next();
			}
			
		}
		
		return credential;
	}
	
	public boolean addDomainCredential(Credential credential){
		if(domainCredentials == null){
			domainCredentials = new HashSet<>();
		}
		return credential != null ? domainCredentials.add(credential) : false;
	}
	
	public boolean addOwnerCredential(Credential credential){
		if(ownerCredentials == null){
			ownerCredentials = new HashSet<>();
		}
		return credential != null ? ownerCredentials.add(credential) : false;
	}

}
