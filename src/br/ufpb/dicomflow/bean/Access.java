package br.ufpb.dicomflow.bean;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;


@Entity
@Table(name="access")
public class Access extends AbstractPersistence {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8784986036008582117L;
	
	public static final String CERIFICATE_OPEN = "OPEN";
	public static final String CERIFICATE_PENDING = "PENDING";
	public static final String CERIFICATE_CLOSED = "CLOSED";

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	private String mail;
	private String host;
	private Integer port;
	private String credential;
	private String certificateStatus;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="id_access")
	private Set<RegistryAccess> accessRegistries;
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public Set<RegistryAccess> getAccessRegistries() {
		return accessRegistries;
	}

	public void setAccessRegistries(Set<RegistryAccess> accessRegistries) {
		this.accessRegistries = accessRegistries;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}
	
	public String getCertificateStatus() {
		return certificateStatus;
	}

	public void setCertificateStatus(String certificateStatus) {
		this.certificateStatus = certificateStatus;
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
