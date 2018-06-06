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

import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufpb.dicomflow.ndn.PrefixRegisterService;
import br.ufpb.dicomflow.ndn.PrefixRegisterServiceIF;
import br.ufpb.dicomflow.util.Util;

/**
 * The implementation of <code>ServiceLocator</code>.
 * <p>
 * This class is managed by the JSF managed bean facility,
 * and is set with application scope.
 * 
 * @see ServiceLocator
 */
public class ServiceLocator  {
	
		
	//the persistence service bean name
	private static final String PACS_PERSISTENT_SERVICE_BEAN_NAME = "pacsPersistentService";
	
	private static final String PERSISTENT_SERVICE_BEAN_NAME = "persistentService";

	private static final String CERTIFICATE_SERVICE_BEAN_NAME = "certificateService";
	
	private static final String MESSAGE_SERVICE_BEAN_NAME = "messageService";
	
	private static final String FILE_SERVICE_BEAN_NAME = "fileService";
	
	private static final String URL_GENERATOR_BEAN_NAME = "urlGenerator";
	
	private static final String URI_GENERATOR_BEAN_NAME = "uriGenerator";
	
	private static final String PREFIX_REGISTER_BEAN_NAME = "prefixRegisterService";
	

	//the logger for this class
	private Log logger = LogFactory.getLog(this.getClass());
	
	//the Spring application context
	private ApplicationContext appContext;
	
		
	//the cached user service
	private PacsPersistentServiceIF pacsPersistentService;
	
	private PersistentServiceIF persistentService;
	
	private CertificateServiceIF certificateService;
	
	private MessageServiceIF messageService;
	
	private UrlGeneratorIF urlGenerator;
	
	private UriGeneratorIF uriGenerator;
	
	private FileServiceIF fileService;
	
	private PrefixRegisterServiceIF prefixRegisterService;
	
	private static ServiceLocator singleton = null;
	
	/**
	 * Constructor.
	 * <p>
	 * The following steps being done:
	 * <ul>
	 * <li>retrieve Spring application context from servlet context.
	 * <li>look up <code>UserService</code> from Spring applicatio context.
	 * </ul>
	 */
	private ServiceLocator() {
		this.logger.info("Initializing ServiceLocator");
		ServletContext context = Util.singleton().getContext();
		this.logger.info("Cotext  "+ context);
		
		this.appContext = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		
		this.pacsPersistentService = (PacsPersistentServiceIF)this.lookupService(PACS_PERSISTENT_SERVICE_BEAN_NAME);
		this.persistentService = (PersistentServiceIF)this.lookupService(PERSISTENT_SERVICE_BEAN_NAME);
		this.certificateService = (CertificateServiceIF) this.lookupService(CERTIFICATE_SERVICE_BEAN_NAME);
		this.messageService = (MessageServiceIF)this.lookupService(MESSAGE_SERVICE_BEAN_NAME);
		this.fileService = (FileServiceIF)this.lookupService(FILE_SERVICE_BEAN_NAME);
		this.urlGenerator = (UrlGeneratorIF)this.lookupService(URL_GENERATOR_BEAN_NAME);
		this.uriGenerator = (UriGeneratorIF)this.lookupService(URI_GENERATOR_BEAN_NAME);
		
		this.prefixRegisterService = (PrefixRegisterServiceIF)this.lookupService(PREFIX_REGISTER_BEAN_NAME);
		Thread newThrd = new Thread(prefixRegisterService);
		newThrd.start();
		
		this.logger.info("ServiceLocator is initialized");
	}
	
	public static ServiceLocator singleton(){
		if (singleton == null) {
			singleton = new ServiceLocator();
		}
		return singleton;
	}
	
	/**
	 * Retorna o serviço de Persistência do PACS Server
	 * @return PersistentService o serviço de persistência
	 */
	public PacsPersistentServiceIF getPacsPersistentService() {
		return this.pacsPersistentService;
	}
	
	/**
	 * Retorna o serviço de Persistência do DICOMMOVE 
	 * @return PersistentService o serviço de persistência
	 */
	public PersistentServiceIF getPersistentService() {
		return this.persistentService;
	}
	
	/**
	 * Retorna o serviço de certificados 
	 * @return PersistentService o serviço de persistência
	 */
	public CertificateServiceIF getCertificateService() {
		return this.certificateService;
	}

	
	/**
	 * Retorna o serviço de mensagens
	 * @return MessageService o serviço de mensagens
	 */
	public MessageServiceIF getMessageService() {
		return messageService;
	}
	
	/**
	 * Retorna o serviço de arquivamento PACS
	 * @return FileService o serviço de arquivamento PACS
	 */
	public FileServiceIF getFileService() {
		return fileService;
	}
	
	/**
	 * Retorna o gerador de URL para acesso aos estudos
	 * @return UrlGeneratorIF o gerador de URL para acesso aos estudos
	 */
	public UrlGeneratorIF getUrlGenerator() {
		return urlGenerator;
	}
	
	/**
	 * Retorna o gerador de URI para acesso aos estudos
	 * @return UriGeneratorIF o gerador de URI para acesso aos estudos
	 */
	public UriGeneratorIF getUriGenerator() {
		return uriGenerator;
	}

	/**
	 * Lookup service based on service bean name.
	 * 
	 * @param serviceBeanName the service bean name
	 * @return the service bean
	 */
	public Object lookupService(String serviceBeanName) {
		return appContext.getBean(serviceBeanName);
	}
	
	
	public static void main(String[] args) {
		URL path = ServiceLocator.class.getResource("/");
		System.out.println(path.getPath());
	}
}
