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
import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.RequestServiceAccess;
import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.bean.StorageServiceAccess;

public interface MessageServiceIF {
	
	public static final String CERTIFICATE_RESULT_CREATED = "CREATED";
	public static final String CERTIFICATE_RESULT_UPDATED = "UPDATED";
	public static final String CERTIFICATE_RESULT_ERROR = "ERROR";
	
	public String sendStorage(StorageServiceAccess storageServiceAccess) throws ServiceException;
	
	public void sendStorage(StorageService storageService, Access access) throws ServiceException;
	
	public void sendStorage(StorageService storageService, List<Access> accesses) throws ServiceException ;
	
	public void sendStorages(List<StorageService> storageServices, List<Access> accesses) throws ServiceException ;		

	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param messageID
	 * @return Map<String, String> contains <messageID, url>
	 * @throws ServiceException
	 */
	public Map<String, String> getStorages(Date initialDate, Date finalDate, String messageID) throws ServiceException ;

	public void sendStorageResult(String messageID, StorageService storageService) throws ServiceException;
	
	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param idMessage
	 * @return Map<String, String> contains <domain, status>
	 * @throws ServiceException
	 */
	public Map<String, String> getStorageResults(Date initialDate, Date finalDate, String originalMessageID) throws ServiceException;
	
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
	
	
	
	public String sendRequest(RequestServiceAccess requestServiceAccess) throws ServiceException;
	
	public void sendRequest(RequestService requestService, Access access) throws ServiceException;
	
	public void sendRequest(RequestService requestService, List<Access> accesses) throws ServiceException ;
	
	public void sendRequests(List<RequestService> requestService, List<Access> accesses) throws ServiceException ;		

	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param messageID
	 * @return Map<String, String> contains <messageID, url>
	 * @throws ServiceException
	 */
	public Map<String, String> getRequests(Date initialDate, Date finalDate, String messageID) throws ServiceException ;
	
	public void sendRequestResult(String messageID, RequestService requestService) throws ServiceException;
	
	/**
	 * 
	 * @param initialDate
	 * @param finalDate
	 * @param idMessage
	 * @return Map<String, String> contains <domain, status>
	 * @throws ServiceException
	 */
	public Map<String, String> getRequestResults(Date initialDate, Date finalDate, String originalMessageID) throws ServiceException;
	
	
	public int getMaxAttempts();
	
}
