package br.ufpb.dicomflow.service;

import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;



public interface EmailService {
	
	public void sendEmail(String to, String from, String subject, String corpo) throws ServiceException;
	
	public void sendEmail(List toList, String from, String subject, String corpo) throws ServiceException;
		
	
}
