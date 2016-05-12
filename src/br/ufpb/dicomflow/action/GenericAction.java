/*
 * 	This file is part of DicomFlow.
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
  	
package br.ufpb.dicomflow.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsStatics;

import br.ufpb.dicomflow.bean.Persistent;
import br.ufpb.dicomflow.service.PersistentService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Constants;
import br.ufpb.dicomflow.util.Pager;
import br.ufpb.dicomflow.util.PagerIF;
import br.ufpb.dicomflow.util.Util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * Basic abstract class for creating Managed Beans
 */
public abstract class GenericAction extends  ActionSupport implements
		GenericActionIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8904625383033058025L;
	
	protected String idEncript;
	protected PagerIF pager;
	protected String option;
	protected List genericList = new ArrayList();
	protected Map session;
//	protected Map request;
	protected HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
	protected HttpServletResponse response = (HttpServletResponse) ActionContext.getContext().get(StrutsStatics.HTTP_RESPONSE);

	/**
	 * Método abstrato que retorna o bean de persistência que será manipulado
	 * nas ações
	 * 
	 * @return Class a classe que representa um bean de persistência.
	 */
	public abstract Class getClassBean();

	/**
	 * retorna o redirecionamento para a ação de salvamento.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardSave() {
		return sucessfulPage();
	}

	/**
	 * retorna o redirecionamento para a ação de remoção.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardRemove() {
		return sucessfulPage();
	}

	/**
	 * retorna o redirecionamento para a ação de detalhe.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardDetail() {
		return sucessfulPage();
	}

	/**
	 * retorna o redirecionamento para a ação de detalhe.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardDetailSite() {
		return sucessfulPage();
	}

	/**
	 * retorna o redirecionamento para a ação de listagem.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardListAll() {
		return sucessfulPage();
	}

	/**
	 * retorna o redirecionamento para a ação de atualização.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardUpdate() {
		return sucessfulPage();
	}

	/**
	 * retorna o redirecionamento para a ação de erro.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardErro() {
		return "error";
	}

	/**
	 * retorna o redirecionamento para a ação de inicialização de formulário.
	 * 
	 * @return String O redirecionamento da ação.
	 */
	protected String getForwardEraseForm() {
		return sucessfulPage();
	}

	/**
	 * parametro passado para a ação de remoção
	 * 
	 * @return String, o nome do valor que será usado como paramentro na ação de
	 *         remoção Ex: request.getParameter(getLabelRemove())
	 */
	protected String getLabelRemove() {
		return "id";
	}

	/**
	 * parametro passado para a ação de detalhe
	 * 
	 * @return String, o nome do valor que será usado como paramentro na ação de
	 *         remoção Ex: request.getParameter(getLabelDetail())
	 */
	protected String getLabelDetail() {
		return "id";
	}

	/**
	 * parametro passado para a ação de atualização
	 * 
	 * @return String, , o nome do valor que será usado como paramentro na ação
	 *         de atualização Ex: request.getParameter(getLabelUpdate())
	 */
	protected String getLabelUpdate() {
		return "id";
	}

	/**
	 * gera uma chave default para o bean de persistência na ação de remoção
	 * 
	 * @return Object que representa a chave do bean.
	 */
	protected Object createKeyRemove(String id) {
		return new Long(id);
	}

	protected Object createKeyDetail(String id) {
		return new Long(id);
	}
	
	/**
	 * gera uma chave default para o bean de persistência na ação de atualização
	 * 
	 * @return Object que representa a chave do bean.
	 */
	protected Object createKeyUpdate(String id) {
		return new Long(id);
	}

	protected abstract String getFormNameUpdate();

	protected String sucessfulPage() {
		return "sucessful";
	}

	public String eraseFormAction() {
		try {
			setPersistent((Persistent) getClassBean().newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return getForwardEraseForm();
	}
	
	

	public String saveAction() {

		Persistent persistent = getPersistent();
		if (getIdEncript() != null && !getIdEncript().equals("")) {
			persistent.setIdEncript(getIdEncript());
		}
		
		
		try {
			
			Persistent newPersistent = (Persistent) getClassBean().newInstance();
			if(persistent.saveOnDb()){
				newPersistent = (Persistent) ServiceLocator.singleton().getPersistentService().select("id", persistent.getId(), getClassBean());
			}
			Util.copyPropertiesExcludingCollection(newPersistent, persistent);
			newPersistent.save();	
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		setPersistent(persistent);
		setIdEncript(persistent.getIdEncript());

		return getForwardSave();
	
	
	}

	public String removeAction() {
		if (getIdEncript() != null && !getIdEncript().equals("")) {
			Persistent p;
			try {
				p = (Persistent) getClassBean().newInstance();
				p.setIdEncript(getIdEncript());
				p = (Persistent) ServiceLocator.singleton()
						.getPersistentService().select(getLabelRemove(),
								createKeyRemove(p.getId().toString()), getClassBean());
				p.remove();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (ServiceException e) {
				e.printStackTrace();
			}
		}

		return getForwardRemove();
	}

	public String removeSelectedAction() {
		String[] values = getSelectedObjects();
		if (values.length != 0) {
			List<Integer> integerValues = new ArrayList<Integer>();
			for (int i = 0; i < values.length; i++) {
				integerValues.add(new Integer(values[i]));
			}
			ServiceLocator.singleton().getPersistentService().removeByIds(
					getLabelRemove(), integerValues, getClassBean());
			setSelectedObjects(new String[] {});
		}

		return getForwardRemove();
	}

	public String detailAction() {
		 if (getIdEncript() != null && !getIdEncript().equals("")) {
			 Persistent p;
			try {
				p = (Persistent) getClassBean().newInstance();
				p.setIdEncript(getIdEncript());
				p = (Persistent)ServiceLocator.singleton().getPersistentService().select(getLabelDetail(),createKeyDetail(p.getId().toString()), getClassBean());
			    setPersistent(p);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			 
		 }
		return getForwardDetail();
	}

	public String listAction()  {
		try {
			updatePager();
			this.genericList = ServiceLocator.singleton().getPersistentService().selectPagingOrderBy(pager.getFirst(),pager.getMax(), getClassBean(), PersistentService.DESC, "id");
			this.pager.setSize(genericList.size());
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return getForwardListAll();
	}

	protected void updatePager() {
		this.pager = (PagerIF) getSession().get(Constants.PAGER);
		if (pager == null) {
			pager = Pager.createDefaultPager();
			getSession().put(Constants.PAGER, pager);
			
		}
		String option = (String) getRequest().getParameter("option");
		if (option != null) {
			System.out.println("pager update first "+ pager.getFirst() + " max " + pager.getMax());
			pager.pageUpdate(option);
		}
	}

	public String updateAction(){
		if (getIdEncript() != null && !getIdEncript().equals("")) {
			 try {
				Persistent p = (Persistent) getClassBean().newInstance();
				p.setIdEncript(getIdEncript());
				System.out.println("id encript " + getIdEncript() + " id persistent " + p.getId());
     			p = (Persistent)ServiceLocator.singleton().getPersistentService().select(getLabelDetail(),createKeyDetail(p.getId().toString()), getClassBean());
				System.out.println("Persistent "  + p);
     			setPersistent(p);
     			
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		 }
		return getForwardUpdate();
	}


	public String getIdEncript() {
		return idEncript;
	}

	public void setIdEncript(String idEncript) {
		this.idEncript = idEncript;
	}

	public List getGenericList() {
		return genericList;
	}

	public void setGenericList(List genericList) {
		this.genericList = genericList;
	}

	public Map getSession() {
		return session;
	}

	public void setSession(Map session) {
		this.session = session;
	}

//	public Map getRequest() {
//		return request;
//	}
//
//	public void setRequest(Map request) {
//		this.request = request;
//	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}
	
	
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse(){
		return response;
	}
	
	public void setResponse(HttpServletResponse response ){
		this.response = response;
	}

	
	
}
