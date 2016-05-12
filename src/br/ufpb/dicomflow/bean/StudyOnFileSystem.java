package br.ufpb.dicomflow.bean;

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


@Entity
@Table(name="study_on_fs")
public class StudyOnFileSystem extends AbstractPersistence{
	
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
