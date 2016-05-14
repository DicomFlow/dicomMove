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

package br.ufpb.dicomflow.bean;

import java.io.Serializable;

import br.ufpb.dicomflow.service.ServiceException;

public interface Persistent extends Serializable {
	/**
	 * Retorna o valor corespondente ao ID do registro da entidade no banco.
	 * @return ID. 
	 */
	public Long getId();
		
	public void setId(Long newId);
	
	/**
	 * Salva a entidade em forma de registro no banco de dados.
	 * @throws ServiceException Se não foi possivel realizar a operação.
	 */
	public void save() throws ServiceException;
	/**
	 * Remove o registro que representa a entidade no banco de dados.
	 * @throws ServiceException Se não foi possivel realizar a operação.
	 */
	public void remove() throws ServiceException;
	
	public boolean saveOnDb();
	
	public String getIdEncript();

	public void setIdEncript(String id);

}
