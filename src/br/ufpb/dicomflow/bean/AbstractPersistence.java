package br.ufpb.dicomflow.bean;

import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import sun.security.action.GetBooleanAction;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CriptografiaBase64;

public abstract class AbstractPersistence implements Serializable, Persistent {

	public void save() throws ServiceException{
		ServiceLocator.singleton().getPersistentService().saveOrUpdate(this);
	}
	
	
	
	public void remove() throws ServiceException {
		ServiceLocator.singleton().getPersistentService().remove(this);
		
	}
	
	
	public boolean saveOnDb() {
		if (getId() != null && !getId().equals(new Long(0))) {
			return true;
		}
		return false;
	}
	
	
	public String getIdEncript() {
		String idEncript = "";
		try {
			if (getId() != null) {
				idEncript = CriptografiaBase64.encrypt(getId().toString());	
			}
			
			
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return idEncript;
	}
	

	public void setIdEncript(String id) {
		try {
			if (id != null && !id.equals("")) {
				setId(new Long(CriptografiaBase64.decrypt(id)));	
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	
	public abstract Long getId();
	
	public abstract void setId(Long id);

	
}
