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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;


@Entity
@Table(name="request_service_access")
public class RequestServiceAccess extends AbstractPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -764669393826586469L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_access")
	private Access access;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_request_service")
	private RequestService requestService;
	
	private String messageID;
	
	private Credential credential;
	
	private String validity;
	
	private String status;
	
	private int uploadAttempt;
	
	public int getUploadAttempt() {
		return uploadAttempt;
	}

	public void setUploadAttempt(int uploadAttempt) {
		this.uploadAttempt = uploadAttempt;
	}

	public RequestServiceAccess(){
		
	}
	
	public RequestServiceAccess(RequestService requestService, Access access){
		this.access = access;
		this.requestService = requestService;
		
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}
	
	public RequestService getRequestService() {
		return requestService;
	}

	public void setRequestService(RequestService requestService) {
		this.requestService = requestService;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
