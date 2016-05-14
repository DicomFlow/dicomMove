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


package br.ufpb.dicomflow.service;


public class ServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9128699633993438674L;

	/**
	 * contrutor que recebe uma exce��o de qualquer servi�o do sistema.
	 * @param e a exce��o.
	 */
	public ServiceException(Exception e) {
		super(e);
	}

}
