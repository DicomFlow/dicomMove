package br.ufpb.dicomflow.action.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;

import br.ufpb.dicomflow.action.GenericActionAdapter;
import br.ufpb.dicomflow.bean.Persistent;
import br.ufpb.dicomflow.bean.User;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Constants;
import br.ufpb.dicomflow.util.ForwardConstants;


@ParentPackage("myDefaultPackage")
@Namespace(value="/admin")
@Results(
	{
		@Result(name = ForwardConstants.LOGIN_FAIL, location = "/login.jsp"),
		@Result(name = ForwardConstants.LOGIN_SUCCESS, location = "/admin/index.jsp"),
		@Result(name = ForwardConstants.LOGIN_FORM, location = "/login.jsp"),
		@Result(name = ForwardConstants.USER_ALTER_PASSWORD_PAGE, location = "/admin/alter_password.jsp"),
		@Result(name = ForwardConstants.PASSWORD_SUCCESS_PAGE, location = "/html/password_success_page.jsp"),
		@Result(name = ForwardConstants.PASSWORD_FAIL_PAGE, location = "/html/password_fail_page.jsp")
	}
)
@InterceptorRefs({
		@InterceptorRef("defaultStack"),
		@InterceptorRef("securityInterceptor")
})



public class LoginAction extends GenericActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7911119497496121034L;

	private User user;
	private String rePasswrd;
	
	// control attributes
	
	private String[] types = {User.USER_ADMINISTRATOR};
	private String[] selectedObjects = {};

	
	
	public LoginAction(){
		this.user = new User();
	}
	
	
	
	@Action(value = "/login", 
			results = { 
				@Result(name = ForwardConstants.INPUT, location = "/login.jsp") 
			}
	)
	public String loginAction() {
		
		User bd = null;
		
		if (user != null && user.getLogin() != null && !user.getLogin().equals("")) {
			bd = (User) ServiceLocator.singleton().getPersistentService()
			.select("login", this.user.getLogin(), getClassBean());
		}

		if (bd != null && bd.getLogin().equals(this.user.getLogin())
				&& bd.getPasswrd().equals(this.user.getPasswrd())) {
			
			
			if (bd.getBlock().booleanValue()) {
				super.addActionError(getText("login.fail.block"));
				return ForwardConstants.LOGIN_FORM;
			}
			
			getSession().put(User.USER_TYPE_ADMINISTRATOR, bd.getType());
			getSession().put(Constants.LOGGED_USER, bd);
			
			return ForwardConstants.LOGIN_SUCCESS;
		}
		super.addActionError(getText("login.fail"));
		return ForwardConstants.LOGIN_FORM;
	}

	
	@SkipValidation
	@Action(value = "updatePasswordUser")
	public String alterPasswordAction() {
		
		if (!getUser().getPasswrd().equals(getRePasswrd())) {
			super.addActionError("As senhas precisam ser iguais");
			return ForwardConstants.USER_ALTER_PASSWORD_PAGE;
		}
		if (getUser().getPasswrd() == null || getUser().getPasswrd().equals("")) {
			super.addActionError("O e-mail deve ser informado");
			return ForwardConstants.USER_ALTER_PASSWORD_PAGE;
		}
		// paga o usuário logado
		User sessionUser = (User) getSession().get(Constants.LOGGED_USER);
		
		//busca o usuário do banco
		User userDb = (User) ServiceLocator.singleton().getPersistentService().select("login", sessionUser.getLogin() , User.class);
		
		if (getUser().getPasswrd() != null && !getUser().getPasswrd().equals("")) {
			userDb.setPasswrd(getUser().getPasswrd());
			userDb.setMail(getUser().getMail());
			userDb.setFirstAccess(false);
			try {
				userDb.save();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
			
			// altera a nova senha do usuário da sessão
			getSession().put(Constants.LOGGED_USER, userDb);
			setUser(userDb);
		}
		
		return this.loginAction();
	}
	
	@SkipValidation
	@Action(value = "/logout")
	public String logoutAction() {
		getSession().clear();
		return ForwardConstants.LOGIN_FORM;
	}
	
	
	@SkipValidation
	@Action(value = "/sendPassword")
	public String sendPasswordAction() {
		if(this.user.getMail() != null && !this.user.getMail().equals("") &&
		   this.user.getLogin() != null && !this.user.getLogin().equals("")	){
			
			User userBd = (User) ServiceLocator.singleton().getPersistentService().select("login", this.user.getLogin(), User.class);
			if(userBd != null && userBd.getMail().equals(this.user.getMail())){
		
				try {
					ServiceLocator.singleton().getMailService().sendEmail(userBd.getMail(),Constants.CONTACT_MAIL,"<br><br>"+Constants.CONTACT_PASSWORD_TITTLE+"<br><br>"," Seu login e senha de acesso são: <br> LOGIN: " +userBd.getLogin() + " <br> SENHA: " + userBd.getPasswrd()+"<br><br>");
					return ForwardConstants.PASSWORD_SUCCESS_PAGE;
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		return ForwardConstants.PASSWORD_FAIL_PAGE;
		
	}
	
	@SkipValidation
	@Action(value = "/verifySession")
	public String verifySession() {
		System.err.println("verifiquei a sessão!");
		getSession().put("time", System.currentTimeMillis());
   		return ForwardConstants.SESSION_SUCCESS_PAGE;
		
		
	}



	@Override
	public Class getClassBean() {
		return User.class;
	}



	
	public Persistent getPersistent() {
		// TODO Auto-generated method stub
		return null;
	}


	public void setPersistent(Persistent p) {
		// TODO Auto-generated method stub
		
	}

	public String[] getSelectedObjects() {
		return this.selectedObjects;
	}
	
	
	public void setSelectedObjects(String[] objetosSelecionados) {
		this.selectedObjects = objetosSelecionados;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public String[] getTypes() {
		return types;
	}



	public void setTypes(String[] types) {
		this.types = types;
	}
	
	



	public String getRePasswrd() {
		return rePasswrd;
	}



	public void setRePasswrd(String rePasswrd) {
		this.rePasswrd = rePasswrd;
	}


	

	
	
	
	
}
