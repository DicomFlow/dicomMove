package br.ufpb.dicomflow.service;

import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
	private static final String PERSISTENT_SERVICE_BEAN_NAME = "persistentService";
	
	private static final String PERSISTENT_SERVICE_BEAN_NAME_2 = "persistentService2";

	private static final String MAIL_SERVICE_BEAN_NAME = "mailService";
	
	private static final String MESSAGE_SERVICE_BEAN_NAME = "messageService";
	
	private static final String FILE_SERVICE_BEAN_NAME = "fileService";
	
	private static final String URL_GENERATOR_BEAN_NAME = "urlGenerator";
	

	//the logger for this class
	private Log logger = LogFactory.getLog(this.getClass());
	
	//the Spring application context
	private ApplicationContext appContext;
	
		
	//the cached user service
	private PersistentService persistentService;
	
	private PersistentService persistentService2;
	
	private EmailService mailService;
	
	private MessageService messageService;
	
	private UrlGeneratorIF urlGenerator;
	
	private FileService fileService;
	
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
		
		this.persistentService = (PersistentService)this.lookupService(PERSISTENT_SERVICE_BEAN_NAME);
		this.persistentService2 = (PersistentService)this.lookupService(PERSISTENT_SERVICE_BEAN_NAME_2);
		this.mailService = (EmailService)this.lookupService(MAIL_SERVICE_BEAN_NAME);
		this.messageService = (MessageService)this.lookupService(MESSAGE_SERVICE_BEAN_NAME);
		this.fileService = (FileService)this.lookupService(FILE_SERVICE_BEAN_NAME);
		this.urlGenerator = (UrlGeneratorIF)this.lookupService(URL_GENERATOR_BEAN_NAME);
		
		this.logger.info("ServiceLocator is initialized");
	}
	
	public static ServiceLocator singleton(){
		if (singleton == null) {
			singleton = new ServiceLocator();
		}
		return singleton;
	}
	
	/**
	 * Retorna o serviço de Persistência do DCM4CHE
	 * @return PersistentService o serviço de persistência
	 */
	public PersistentService getPersistentService() {
		return this.persistentService;
	}
	
	/**
	 * Retorna o serviço de Persistência do DICOMMOVE 
	 * @return PersistentService o serviço de persistência
	 */
	public PersistentService getPersistentService2() {
		return this.persistentService2;
	}

	
	/**
	 * Retorna o serviço de mail
	 * @return EmailService o serviço de mail
	 */
	public EmailService getMailService() {
		return mailService;
	}

	
	/**
	 * Retorna o serviço de mensagens
	 * @return MessageService o serviço de mensagens
	 */
	public MessageService getMessageService() {
		return messageService;
	}
	
	/**
	 * Retorna o serviço de arquivamento PACS
	 * @return FileService o serviço de arquivamento PACS
	 */
	public FileService getFileService() {
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
