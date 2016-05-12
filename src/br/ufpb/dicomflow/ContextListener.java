package br.ufpb.dicomflow;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.ufpb.dicomflow.agent.CronSchedule;
import br.ufpb.dicomflow.util.Util;

/**
 * Aplication Context Initializer 
 */
public class ContextListener implements ServletContextListener {



	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg) {		
			
		Util.getLogger(this).debug("################## Iniciando o contexto ################### ");
		Util.singleton().setContext(arg.getServletContext());
		
		
		try {
//			new CronSchedule();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Util.getLogger(this).debug("Contexto incicializado com sucesso");	
		
		
		
	}

	
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {		
		Util.getLogger(this).debug("Contexto fechado com sucesso");
	}
	
}
