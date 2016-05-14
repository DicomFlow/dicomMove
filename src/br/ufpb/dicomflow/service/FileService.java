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

package br.ufpb.dicomflow.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import br.ufpb.dicomflow.bean.File;

public interface FileService {

	public void createZipFile(List<File> files, OutputStream os) throws IOException, FileNotFoundException, ServiceException;
	
	public void extractZipFile(URL url) throws IOException, ServiceException;
	
	public void storeFile(java.io.File file) throws ServiceException;

	public java.io.File getCertificate() throws ServiceException;
	
	public boolean storeCertificate(byte[] certificate, String alias) throws ServiceException;
	
	public String getArchivePath();
	
	public String getEat();

	public String getHost();

	public String getPort();
	
	public String getExtractDir();


	
}
