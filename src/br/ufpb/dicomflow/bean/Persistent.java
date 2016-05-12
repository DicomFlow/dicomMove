/*
 * Created on 13/07/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.ufpb.dicomflow.bean;

import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.util.CriptografiaBase64;

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
