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

package br.ufpb.dicomflow.agent;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import br.ufpb.dicomflow.util.Util;


public class CronSchedule {

	public CronSchedule() throws Exception {

		Util.getLogger(this).debug("Creating CronSchedule...");		
		SchedulerFactory sf = new StdSchedulerFactory();		
		Scheduler sched = sf.getScheduler();
		
		//Jobs em ordem do fluxo do processo
		
//		//envia o certificado do próprio domínio
//		JobDetail sendCertificateJobDetail = new JobDetail("sendCertificateJobDetail","group1",SendCertificateAgent.class);
//		CronTrigger sendCertificateCronTrigger = new CronTrigger("sendCertificateCronTrigger","group2","0 * * * * ?");		
//		sched.scheduleJob(sendCertificateJobDetail ,sendCertificateCronTrigger);
//		
//		//armazena certificados recebidos de outros domínios
//		JobDetail storeCertificateJobDetail = new JobDetail("storeCertificateJobDetail","group1",StoreCertificateAgent.class);
//		CronTrigger storeCertificateCronTrigger = new CronTrigger("storeCertificateCronTrigger","group2","0 * * * * ?");		
//		sched.scheduleJob(storeCertificateJobDetail ,storeCertificateCronTrigger);
//		
//		//verifica resultado do envio do certificado do próprio domínio 
//		JobDetail findCertificateResultJobDetail = new JobDetail("findCertificateResultJobDetail","group1",FindCertificateResultAgent.class);
//		CronTrigger findCertificateResultCronTrigger = new CronTrigger("findCertificateResultCronTrigger","group2","0 * * * * ?");		
//		sched.scheduleJob(findCertificateResultJobDetail ,findCertificateResultCronTrigger);
		
		//busca de novos estudos medicos
		JobDetail findStudiesJobDetail = new JobDetail("findStudiesJobDetail","group1",FindStudiesAgent.class);
		CronTrigger findStudiesCronTrigger = new CronTrigger("findStudiesCronTrigger","group2","0 * * * * ?");		
		sched.scheduleJob(findStudiesJobDetail ,findStudiesCronTrigger);
		
//		//envia urls de acesso aos novos estudos
		JobDetail sendStudiesURLsJobDetail = new JobDetail("sendStudiesURLsJobDetail","group1",SendStudiesURLsAgent.class);
		CronTrigger sendStudiesURLsCronTrigger = new CronTrigger("sendStudiesURLsCronTrigger","group2","0 * * * * ?");		
		sched.scheduleJob(sendStudiesURLsJobDetail ,sendStudiesURLsCronTrigger);
//		
//		//resolve envio pendende de urls de acesso aos novos estudos
		JobDetail sendPendingStudiesURLsJobDetail = new JobDetail("sendPendingStudiesURLsJobDetail","group1",SendPendingStudiesURLsAgent.class);
		CronTrigger sendPendingStudiesURLsCronTrigger = new CronTrigger("sendPendingStudiesURLsCronTrigger","group2","0 * * * * ?");		
		sched.scheduleJob(sendPendingStudiesURLsJobDetail ,sendPendingStudiesURLsCronTrigger);
		
//		//busca por urls de acessos recebidas para novos estudos
//		JobDetail findStudiesURLsJobDetail = new JobDetail("findStudiesURLsJobDetail","group1",FindStudiesURLsAgent.class);
//		CronTrigger findStudiesURLsCronTrigger = new CronTrigger("findStudiesURLsCronTrigger","group2","0 * * * * ?");
//		sched.scheduleJob(findStudiesURLsJobDetail, findStudiesURLsCronTrigger);
//		
//		//armazena estudos acessados através das urls buscadas
//		JobDetail storeStudiesJobDetail = new JobDetail("storeStudiesJobDetail","group1",StoreStudiesAgent.class);
//		CronTrigger storeStudiesCronTrigger = new CronTrigger("storeStudiesCronTrigger","group2","0 * * * * ?");
//		sched.scheduleJob(storeStudiesJobDetail, storeStudiesCronTrigger);
//		
//		//resolve armazenamento pendente de estudos acessados através das urls buscadas
//		JobDetail storePendingStudiesJobDetail = new JobDetail("storePendingStudiesJobDetail","group1",StorePendingStudiesAgent.class);
//		CronTrigger storePendingStudiesCronTrigger = new CronTrigger("storePendingStudiesCronTrigger","group2","0 * * * * ?");
//		sched.scheduleJob(storePendingStudiesJobDetail, storePendingStudiesCronTrigger);
		
		sched.start();
		Util.getLogger(this).debug("CronSchedule Created!");
		
	}
	public static void main(String args[])
	{
		try{		
		new CronSchedule();
		}catch(Exception e){}
	}
}
