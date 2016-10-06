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

import java.util.Date;


public interface FileIF {
	
	public Long getId();

	public void setId(Long id);
	
//	public InstanceIF getInstance();
//
//	public void setInstance(InstanceIF instance);
//
	public FileSystemIF getFileSystem();

//	public void setFileSystem(FileSystemIF fileSystem);

	public Date getCreatedTime();

	public void setCreatedTime(Date createdTime);

	public Date getTimeOfLastMd5Check();
	
	public void setTimeOfLastMd5Check(Date timeOfLastMd5Check);

	public String getFilePath();

	public void setFilePath(String filePath);

	public String getFileTsuid();

	public void setFileTsuid(String fileTsuid);

	public String getFileMd5Field();

	public void setFileMd5Field(String fileMd5Field);

	public Integer getFileStatus();

	public void setFileStatus(Integer fileStatus);

	public Long getFileSize();

	public void setFileSize(Long fileSize);
	
	/**
	 * MD5 checksum in binary format
	 * 
	 */
	public byte[] getFileMd5();
	
	 
	public void setFileMd5(byte[] md5);
	
	public boolean isRedundant();
	
}
