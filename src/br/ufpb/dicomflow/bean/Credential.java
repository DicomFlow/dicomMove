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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="credential")
public class Credential extends AbstractPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1727371166080980807L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	private String key;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_owner")
	private Access owner;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_domain")
	private Access domain;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_credential")
	private Set<ServicePermission> servicePermissions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	 public Access getOwner() {
		return owner;
	}

	public void setOwner(Access owner) {
		this.owner = owner;
	}

	public Access getDomain() {
		return domain;
	}

	public void setDomain(Access domain) {
		this.domain = domain;
	}
	
	public Set<ServicePermission> getServicePermissions() {
		return servicePermissions;
	}

	public void setServicePermissions(Set<ServicePermission> servicePermissions) {
		this.servicePermissions = servicePermissions;
	}
	
	

}
