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

package br.ufpb.dicomflow.bean.dcm4che;

import java.util.Date;
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

import br.ufpb.dicomflow.bean.AbstractPersistence;
import br.ufpb.dicomflow.bean.InstanceIF;


@Entity
@Table(name="instance")
public class Instance extends AbstractPersistence implements InstanceIF{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5851474929556502906L;

	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="pk",unique=true)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="series_fk")
	private Series series;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="instance_fk")
	private Set<File> files;
	
	@Column(name="sop_iuid")
	private String sopIuid;
	
	@Column(name="sop_cuid")
	private String sopCuid;
	
	@Column(name="inst_no")
	private String instanceNumber;
	
	@Column(name="content_datetime")
	private Date contentDateTime;
	
	@Column(name="retrieve_aets")
	private String retrieveAETs;
	
	@Column(name="availability")
    private Integer availability;
	
	@Column(name="inst_status")
    private Integer instanceStatus;
	
	@Column(name="archived")
    private Boolean archived;
	
	@Column(name="all_attrs")
    private Boolean allAttributes;
	
	@Column(name="commitment")
    private Boolean commitment;
	
	@Column(name="updated_time")
    private Date updatedTime;
	
	@Column(name="created_time")
    private Date createdTime;
	
	@Column(name="inst_attrs")
    private byte[] encodedAttributes;
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public String getSopIuid() {
		return sopIuid;
	}

	public void setSopIuid(String sopIuid) {
		this.sopIuid = sopIuid;
	}

	public String getSopCuid() {
		return sopCuid;
	}

	public void setSopCuid(String sopCuid) {
		this.sopCuid = sopCuid;
	}

	public String getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public Date getContentDateTime() {
		return contentDateTime;
	}

	public void setContentDateTime(Date contentDateTime) {
		this.contentDateTime = contentDateTime;
	}

	public String getRetrieveAETs() {
		return retrieveAETs;
	}

	public void setRetrieveAETs(String retrieveAETs) {
		this.retrieveAETs = retrieveAETs;
	}

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public Integer getInstanceStatus() {
		return instanceStatus;
	}

	public void setInstanceStatus(Integer instanceStatus) {
		this.instanceStatus = instanceStatus;
	}

	public Boolean getArchived() {
		return archived;
	}

	public void setArchived(Boolean archived) {
		this.archived = archived;
	}

	public Boolean getAllAttributes() {
		return allAttributes;
	}

	public void setAllAttributes(Boolean allAttributes) {
		this.allAttributes = allAttributes;
	}

	public Boolean getCommitment() {
		return commitment;
	}

	public void setCommitment(Boolean commitment) {
		this.commitment = commitment;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public byte[] getEncodedAttributes() {
		return encodedAttributes;
	}

	public void setEncodedAttributes(byte[] encodedAttributes) {
		this.encodedAttributes = encodedAttributes;
	}

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	@Override
	public String getFilePath() {
		if(files != null && files.size() > 0 ){
			return files.iterator().next().getFilePath();
		}
		return null;
	}

	@Override
	public void setFilePath(String filePath) {
		if(files != null && files.size() > 0 ){
			files.iterator().next().setFilePath(filePath);
		}
	}
	
	

}
