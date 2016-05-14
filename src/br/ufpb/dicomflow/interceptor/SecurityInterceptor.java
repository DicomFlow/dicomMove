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

package br.ufpb.dicomflow.interceptor;

import br.ufpb.dicomflow.bean.User;
import br.ufpb.dicomflow.util.Constants;
import br.ufpb.dicomflow.util.ForwardConstants;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

public class SecurityInterceptor implements Interceptor {



	/**
	 * 
	 */
	private static final long serialVersionUID = 7712431475766963855L;

	public String intercept(ActionInvocation invocation) throws Exception {
		StringBuffer action = new StringBuffer();
		action.append(invocation.getInvocationContext().getActionInvocation().getProxy().getNamespace());
		action.append(invocation.getInvocationContext().getActionInvocation().getProxy().getActionName());
		System.err.print("Action: " + action.toString() + "|");
		// colocar o ip de acesso do usuario
		if (action.toString().contains("admin")) {
			System.err.print("Acesso restrito |");
			User user = (User) invocation.getInvocationContext().getSession().get(Constants.LOGGED_USER);
			if (user != null) {
				if (!user.getBlock() && user.getLogin() != null && !user.getLogin().equals("") ) {
					System.err.println("Acesso Autorizado " + user.getLogin());
					return invocation.invoke();
				}
				
				
			}
			
			System.err.print("Acesso negado usuario não logado" );
			return ForwardConstants.LOGIN_FAIL;
		}
				
		return invocation.invoke();
	}

	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	
	public void init() {
		// TODO Auto-generated method stub
		
	}

}
