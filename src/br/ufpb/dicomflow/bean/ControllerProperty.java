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
@Table(name="controller_property")
public class ControllerProperty extends AbstractPersistence {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6381031067864417006L;
	
	/**
	 * Current date used to list studies during preparation of storage services
	 */
	public static final String CURRENT_DATE_PROPERTY = "currentDate";
	
	/**
	 * Current date used to access storage services messages
	 */
	public static final String MAIL_CURRENT_DATE_PROPERTY = "mailCurrentDate";
	
	/**
	 * Current date used to access storage services result messages
	 */
	public static final String VERIFY_CURRENT_DATE_PROPERTY = "verifyCurrentDate";
	
	/**
	 * Current date used to list studies during preparation of storage services
	 */
	public static final String REQUEST_CURRENT_DATE_PROPERTY = "requestCurrentDate";
	
	/**
	 * Current date used to access request services messages
	 */
	public static final String REQUEST_MAIL_CURRENT_DATE_PROPERTY = "requestMailCurrentDate";
	
	/**
	 * Current date used to access request services result messages
	 */
	public static final String REQUEST_VERIFY_CURRENT_DATE_PROPERTY = "requestVerifyCurrentDate";
	
	/**
	 * Current date used to access certificate services messages
	 */
	public static final String CERTIFICATE_MAIL_CURRENT_DATE_PROPERTY = "certificateMailCurrentDate";
	
	/**
	 * Current date used to access certificate services result messages
	 */
	public static final String CERTIFICATE_VERIFY_CURRENT_DATE_PROPERTY = "certificateVerifyCurrentDate";
	
	/**
	 * Current date used to access request services confirm messages
	 */
	public static final String CERTIFICATE_CONFIRM_CURRENT_DATE_PROPERTY = "certificateConfirmCurrentDate";
	
	/**
	 * Current id used to list studies during preparation of storage services
	 */
	public static final String CURRENT_ID_PROPERTY = "currentID";

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	private String property;
	private String value;

	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
