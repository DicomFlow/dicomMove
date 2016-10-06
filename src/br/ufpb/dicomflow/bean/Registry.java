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
@Table(name="registry")
public class Registry extends AbstractPersistence {

	
	//type values
	public static final String SENT = "sent";
	public static final String RECEIVED = "received";
	
	//status values
	public static final String OPEN = "open";
	public static final String PENDING = "pending";
	public static final String CLOSED = "closed";
	public static final String ERROR = "error";
	public static final String LOCK = "lock";
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4338082442529426759L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	private String link;
	
	@Column(name="study_iuid")
	private String studyIuid;
	
	private String messageID;
	
	private String host;
	
	private int port;
	
	private String type;
	
	private String status;
	
	private int downloadAttempt;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_registry")
	private Set<RegistryAccess> registryAccesses;
	
	public Registry(){
		
	}
	
	public Registry(String link){
		this.link = link;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Set<RegistryAccess> getRegistryAccesses() {
		return registryAccesses;
	}

	public void setRegistryAccesses(Set<RegistryAccess> registryAccesses) {
		this.registryAccesses = registryAccesses;
	}
	
	public String getStudyIuid() {
		return studyIuid;
	}

	public void setStudyIuid(String studyIuid) {
		this.studyIuid = studyIuid;
	}
	
	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getDownloadAttempt() {
		return downloadAttempt;
	}

	public void setDownloadAttempt(int downloadAttempt) {
		this.downloadAttempt = downloadAttempt;
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
