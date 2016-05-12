/*
 * Created on 13/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.ufpb.dicomflow.service;


public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9128699633993438674L;

	/**
	 * contrutor que recebe uma exceção de qualquer serviço do sistema.
	 * @param e a exceção.
	 */
	public ServiceException(Exception e) {
		super(e);
	}

}
