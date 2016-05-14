/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either versio3 of the License, or
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


package br.ufpb.dicomflow.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import br.ufpb.dicomflow.bean.User;


/**
 * Classe que intercepta e verifica se o usuário está autorizado a acessar 
 * a página que ele está tentando acesssr
 * 
 */
public class SecurityFilter implements Filter {
	
	//atributos
	
	
	private static final String LOGIN_PAGE_URI = "/login.jsp";
	
	//paginas restritas
	private Set restrictedResources;	
	
	// áreas restritas
	private Set restrictedAreas;

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		
		//Colocar páginas
		this.restrictedResources = new HashSet();
		this.restrictedResources.add("/pagina_teste.jsp");
		
				
		// coloca as áreas (diretórios restritos)
		this.restrictedAreas = new HashSet();
		this.restrictedAreas.add("/admin/");
		

	}
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		Util.getLogger(this).debug("doFilter");
		
		String contextPath = ((HttpServletRequest)req).getContextPath();
		String requestUri = ((HttpServletRequest)req).getRequestURI();
		
		Util.getLogger(this).debug("contextPath = " + contextPath);
		Util.getLogger(this).debug("requestUri = " + requestUri);
		
		if (this.contains(requestUri, contextPath, restrictedResources) && this.authorize((HttpServletRequest)req)) {
			Util.getLogger(this).debug("authorization succeeded normal");
			chain.doFilter(req, res);
		}else if(this.contains(requestUri, contextPath, restrictedAreas) && this.authorize((HttpServletRequest)req)){
			Util.getLogger(this).debug("authorization succeeded area");
			chain.doFilter(req, res);
		}else if(!this.contains(requestUri, contextPath, restrictedResources) 
		        && !this.contains(requestUri, contextPath, restrictedAreas)){

				Util.getLogger(this).debug("authorization succeeded");
				chain.doFilter(req, res);
		}else{ 
			Util.getLogger(this).debug("authorization failed");			
			((HttpServletRequest)req).getRequestDispatcher(LOGIN_PAGE_URI).forward(req, res);
		}
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {} 
	
	/**
	 * ver se o que o usuário está tentando acessar é uma página ou área restrita
	 * @param value a URI da requisição do usuário 
	 * @param contextPath o contexto da aplicação que ele está tentando acessar
	 * @param restrictedResources conjunto de áreas ou páginas restritas
	 * @return true se o que ele está tentando ver precisa de autorização e false caso contrário
	 */
	private boolean contains(String value, String contextPath, Set restrictedResources) {
	    Iterator ite = restrictedResources.iterator();		
		while (ite.hasNext()) {
			String restrictedResource = (String)ite.next();
			if (value.toLowerCase().startsWith((contextPath + restrictedResource).toLowerCase())
					&& value.toLowerCase().indexOf("/site/") == -1) {
			    return true;
			}
		}
		
		return false;
	}
	
	

	private boolean authorize(HttpServletRequest req)  {
		User loggedUser  = (User) req.getSession().getAttribute(Constants.LOGGED_USER);
		if(loggedUser != null){
			return true;	
		}
		return false;
		
	}
}
