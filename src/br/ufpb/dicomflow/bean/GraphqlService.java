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
@Table(name="graphql_service")
public class GraphqlService extends AbstractPersistence {
	
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
	
	@Column(name="patient_id")
	private String patientId;
	
	private String status;
	
	
	
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_graphql_service")
	private Set<GraphqlServiceAccess> graphqlServiceAccesses;
	
	public GraphqlService(){
		
	}
	
	public GraphqlService(String link){
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

	public Set<GraphqlServiceAccess> getGraphqlServiceAccesses() {
		return graphqlServiceAccesses;
	}

	public void setGraphqlServiceAccesses(Set<GraphqlServiceAccess> graphqlServiceAccesses) {
		this.graphqlServiceAccesses = graphqlServiceAccesses;
	}

	public String getStudyIuid() {
		return studyIuid;
	}

	public void setStudyIuid(String studyIuid) {
		this.studyIuid = studyIuid;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
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
