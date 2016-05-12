package br.ufpb.dicomflow.action;

import br.ufpb.dicomflow.bean.Persistent;

public interface GenericActionIF {
	
	public Persistent getPersistent();
	
	public void setPersistent(Persistent p) ;
	
	public String[] getSelectedObjects();

	public void setSelectedObjects(String[] objetosSelecionados);

}
