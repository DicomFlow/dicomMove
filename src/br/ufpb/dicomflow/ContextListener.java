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
			new CronSchedule();
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
