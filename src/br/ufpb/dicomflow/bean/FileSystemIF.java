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


public interface FileSystemIF {

	public Long getId();
	
	public void setId(Long id);

//	public FileSystemIF getNextFileSystem();
//
//	public void setNextFileSystem(FileSystemIF nextFileSystem);

//	public Set<FileSystemIF> getPreviousFileSystems();
//
//	public void setPreviousFileSystems(Set<FileSystemIF> previousFileSystems);

	public String getDirectoryPath();

	public void setDirectoryPath(String directoryPath);

	public String getGroupID();

	public void setGroupID(String groupID);

	public String getRetrieveAET();

	public void setRetrieveAET(String retrieveAET);

	public Integer getAvailability();

	public void setAvailability(Integer availability);

	public Integer getStatus();

	public void setStatus(Integer status);

	public String getUserInfo();

	public void setUserInfo(String userInfo);
	
	
	
}
