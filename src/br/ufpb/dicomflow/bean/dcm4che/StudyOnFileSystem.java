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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufpb.dicomflow.bean.AbstractPersistence;
import br.ufpb.dicomflow.bean.StudyOnFileSystemIF;


@Entity
@Table(name="study_on_fs")
public class StudyOnFileSystem extends AbstractPersistence implements StudyOnFileSystemIF{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8293631282617834565L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="pk",unique=true)
	private Long id;
	
	@Column(name="access_time")
    private Date accessTime;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="study_fk")
	private Study study;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="filesystem_fk")
	private FileSystem fileSystem;
	
	@Column(name="mark_to_delete")
    private Boolean markedForDeletion;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id= id;
	}

	public Date getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(Date accessTime) {
		this.accessTime = accessTime;
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public FileSystem getFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(FileSystem fileSystem) {
		this.fileSystem = fileSystem;
	}

	public Boolean getMarkedForDeletion() {
		return markedForDeletion;
	}

	public void setMarkedForDeletion(Boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}
	
	

}
