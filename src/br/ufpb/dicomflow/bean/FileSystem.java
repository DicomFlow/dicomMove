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
@Table(name="filesystem")
public class FileSystem extends AbstractPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2379470717890787704L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="pk",unique=true)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="next_fk")
	private FileSystem nextFileSystem;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="next_fk")
	private Set<FileSystem> previousFileSystems;
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="dirpath")
    private String directoryPath;
	
	@Column(name="fs_group_id")
    private String groupID;
	
	@Column(name="retrieve_aet")
    private String retrieveAET;

	@Column(name="availability")
    private Integer availability;
	
	@Column(name="fs_status")
    private Integer status;
	
	@Column(name="user_info")
    private String userInfo;

	public FileSystem getNextFileSystem() {
		return nextFileSystem;
	}

	public void setNextFileSystem(FileSystem nextFileSystem) {
		this.nextFileSystem = nextFileSystem;
	}

	public Set<FileSystem> getPreviousFileSystems() {
		return previousFileSystems;
	}

	public void setPreviousFileSystems(Set<FileSystem> previousFileSystems) {
		this.previousFileSystems = previousFileSystems;
	}

	public String getDirectoryPath() {
		return directoryPath;
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath = directoryPath;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getRetrieveAET() {
		return retrieveAET;
	}

	public void setRetrieveAET(String retrieveAET) {
		this.retrieveAET = retrieveAET;
	}

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	
	
	
}
