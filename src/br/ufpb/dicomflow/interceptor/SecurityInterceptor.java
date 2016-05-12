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
