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

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Registry;
import br.ufpb.dicomflow.bean.RegistryAccess;

public interface MessageService {
	
	public static final String CERTIFICATE_RESULT_CREATED = "CREATED";
	public static final String CERTIFICATE_RESULT_UPDATED = "UPDATED";
	public static final String CERTIFICATE_RESULT_ERROR = "ERROR";
	
	public String sendURL(RegistryAccess registryAccess) throws ServiceException;
	
	public void sendURL(Registry registry, Access access) throws ServiceException;
	
	public void sendURL(Registry registry, List<Access> accesses) throws ServiceException ;
	
	public void sendURLs(List<Registry> registries, List<Access> accesses) throws ServiceException ;		

	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param messageID
	 * @return Map<String, String> contains <messageID, url>
	 * @throws ServiceException
	 */
	public Map<String, String> getURLs(Date initialDate, Date finalDate, String messageID) throws ServiceException ;

	public void sendResult(String messageID, Registry registry) throws ServiceException;
	
	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param idMessage
	 * @return Map<String, String> contains <domain, status>
	 * @throws ServiceException
	 */
	public Map<String, String> getResults(Date initialDate, Date finalDate, String originalMessageID) throws ServiceException;
	
	public String sendCertificate(File certificate, Access access) throws ServiceException;
	
	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param idMessage
	 * @return Map<Access, byte[]> contains <access,certificate>
	 * @throws ServiceException
	 */
	public Map<Access,byte[]> getCertificates(Date initialDate, Date finalDate, String messageID) throws ServiceException;
	
	public String sendCertificateResult(Access access, String status) throws ServiceException;
	
	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param idMessage
	 * @return Map<Acesso, String> contains <access, certificateStatus>
	 * @throws ServiceException
	 */
	public Map<Access, String>  getCertificateResults(Date initialDate, Date finalDate, String messageID) throws ServiceException;
	
	public int getMaxAttempts();

	

	

	

	

	
	
}
