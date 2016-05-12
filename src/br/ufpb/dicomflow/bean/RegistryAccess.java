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

import com.sun.org.apache.xerces.internal.impl.xpath.regex.RegularExpression;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;


@Entity
@Table(name="registry_access")
public class RegistryAccess extends AbstractPersistence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -764669393826586469L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_access")
	private Access access;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_registry")
	private Registry registry;
	
	private String messageID;
	
	private String credential;
	
	private String validity;
	
	private String status;
	
	private int uploadAttempt;
	
	public int getUploadAttempt() {
		return uploadAttempt;
	}

	public void setUploadAttempt(int uploadAttempt) {
		this.uploadAttempt = uploadAttempt;
	}

	public RegistryAccess(){
		
	}
	
	public RegistryAccess(Registry registry, Access access){
		this.access = access;
		this.registry = registry;
		
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public void save() throws ServiceException{
		ServiceLocator.singleton().getPersistentService2().saveOrUpdate(this);
	}
	
	@Override
	public void remove() throws ServiceException {
		ServiceLocator.singleton().getPersistentService2().remove(this);
		
	}

}
