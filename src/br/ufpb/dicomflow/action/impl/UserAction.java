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

package br.ufpb.dicomflow.action.impl;

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
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.ForwardConstants;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;

@ParentPackage("myDefaultPackage")
@Namespace(value="/admin")
@Validations(
	requiredStrings={
		@RequiredStringValidator( type = ValidatorType.SIMPLE, fieldName = "user.login", message="Login ${getText('required')}" ),
		@RequiredStringValidator( type = ValidatorType.SIMPLE, fieldName = "user.passwrd", message="Senha ${getText('required')}" ),
		@RequiredStringValidator( type = ValidatorType.SIMPLE, fieldName = "user.type", message="Tipo ${getText('required')}" ),
		@RequiredStringValidator( type = ValidatorType.SIMPLE, fieldName = "rePassword", message="Confirmação de Senha ${getText('required')}" )
	}
)


@Results(
	{
		@Result(name = ForwardConstants.LOGIN_FAIL, location = "/falha.jsp"),
		@Result(name = ForwardConstants.LOGIN_SUCCESS, location = "/admin/index.jsp"),
		@Result(name = ForwardConstants.LOGIN_FORM, location = "/html/login.jsp"), 
		@Result(name = ForwardConstants.USER_LIST_PAGE, location = "/admin/user/list_user.jsp"),
		@Result(name = ForwardConstants.USER_CAD_PAGE, location = "/admin/user/cad_user.jsp"),
		@Result(name = ForwardConstants.USER_DETAIL_PAGE, location = "/admin/user/cad_user.jsp")
	}
)
@InterceptorRefs({
		@InterceptorRef("defaultStack"),
		@InterceptorRef("securityInterceptor")
})

public class UserAction extends GenericActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7911119497496121034L;

	private User user;
	private String rePassword;
	
	// control attributes
	
	private String[] types = {User.USER_ADMINISTRATOR};
	private String[] selectedObjects = {};

	
	public UserAction(){
		this.user = new User();
	}
	
	
	
	@SkipValidation
	@Action(value = "listUser")
	@Override
	public String listAction()  {
		return super.listAction();
	}

	@SkipValidation
	@Action(value = "eraseUser")
	@Override
	public String eraseFormAction() {
		return super.eraseFormAction();
	}
	
	
	@SkipValidation
	@Action(value = "removeUser")
	@Override
	public String removeAction() {
		return super.removeAction();
	}

	@SkipValidation
	@Action(value = "removeSelectedUser")
	@Override
	public String removeSelectedAction() {
		super.removeSelectedAction();
		return listAction();
	}

	
	
   
	@Action(value = "saveUser", 
			results = { 
				@Result(name = ForwardConstants.INPUT, location = "/admin/user/cad_user.jsp"),
				@Result( name =ForwardConstants.SUCCESS_PAGE, location="/admin/success_page.jsp")
			}
	)
	
	public String saveAction() {
		boolean error = false;
		if(!getUser().getPasswrd().equals(getRePassword())){
			error = true;
			super.addActionError("Senha e Confirmação de Senha devem ser iguais.");
		}
		
		User userExistente = (User) ServiceLocator.singleton().getPersistentService().select("login", getUser().getLogin(), getClassBean());
		getUser().setIdEncript(getIdEncript());
		if((getUser().getId() == null || getUser().getId() == 0) && userExistente != null){
			error = true;
			super.addActionError("Esse login já foi cadastrado.");
		}
		if(error){
			return ForwardConstants.INPUT;
		}
	   
		getUser().setFirstAccess(true);
		return super.saveAction();
	   
	}

	@SkipValidation
	@Action(value = "updateUser")
	@Override
	public String updateAction() {
		return super.updateAction();
	}


	
	/* Interface methods */
	
	public Class getClassBean() {
		return User.class;
	}

	@Override
	protected String getForwardDetail() {
		return ForwardConstants.USER_DETAIL_PAGE;
	}

	@Override
	protected String getForwardEraseForm() {
		return ForwardConstants.USER_CAD_PAGE;
	}

	@Override
	protected String getForwardListAll() {
		return ForwardConstants.USER_LIST_PAGE;
	}

	@Override
	protected String getForwardRemove() {
		return ForwardConstants.USER_LIST_PAGE;
	}

	@Override
	protected String getForwardSave() {
		return ForwardConstants.SUCCESS_PAGE;
	}

	@Override
	protected String getForwardUpdate() {
		return ForwardConstants.USER_CAD_PAGE;
	}
	
	public String[] getSelectedObjects() {
		return this.selectedObjects;
	}
	
	public void setSelectedObjects(String[] objetosSelecionados) {
		this.selectedObjects = objetosSelecionados;
	}
	
	
	
	public Persistent getPersistent() {
		return this.user;
	}

	
	public void setPersistent(Persistent p) {
		this.user = (User) p;
	}



	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}
	
	
	public String getRePassword() {
		return rePassword;
	}



	public void setRePassword(String rePassword) {
		this.rePassword = rePassword;
	}



	public String[] getTypes() {
		return types;
	}



	public void setTypes(String[] types) {
		this.types = types;
	}
	
}
