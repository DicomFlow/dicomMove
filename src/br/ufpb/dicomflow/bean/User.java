package br.ufpb.dicomflow.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;


@Entity
@Table(name="user")
public class User extends AbstractPersistence  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3100088645564844681L;
	
	public static final String USER_ADMINISTRATOR = "administrator";
	public static final Object USER_TYPE_ADMINISTRATOR = "userTypeAdministrator";
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique=true)
	private Long id;
	
	private String nome;
	private String cpf;
	private String mail;
	private String login;
	private String passwrd;
	private String type;
	private boolean firstAccess;
	private Boolean block;
	
	public User(){
		this.id = new Long(0);
		this.block = Boolean.FALSE;
	}
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getNome() {
		return nome;
	}


	public void setNome(String name) {
		this.nome = name;
	}


	public String getMail() {
		return mail;
	}


	public void setMail(String mail) {
		this.mail = mail;
	}


	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}


	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPasswrd() {
		return passwrd;
	}
	public void setPasswrd(String passwrd) {
		this.passwrd = passwrd;
	}
	
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public boolean isFirstAccess() {
		return firstAccess;
	}


	public void setFirstAccess(boolean firstAccess) {
		this.firstAccess = firstAccess;
	}


	public Boolean getBlock() {
		return block;
	}


	public void setBlock(Boolean block) {
		this.block = block;
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
