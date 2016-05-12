/*
 * Project: BalcomBanner
 * Copyright: Copyright (c) 2003 
 * $Id: GenericActionAdapter.java,v 1.1 2011/03/27 22:38:48 danilo Exp $
 */

package br.ufpb.dicomflow.action;




/**
 * Managed Bean Adapter
 */
public abstract class GenericActionAdapter extends GenericAction {

	
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.action.GenericAction#getCollectionLabelRemove()
	 */
	protected String getCollectionLabelRemove() {
		return null;
	}

	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.action.GenericAction#getCollectionLabelRemove()
	 */
	protected String getObjectLabelDetail() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.action.GenericAction#getCollectionLabeListAll()
	 */
	protected String getCollectionLabeListAll() {
		return null;
	}

	/* (non-Javadoc)
	 * @see br.org.paqtc.siri.action.GenericAction#getFormNameUpdate()
	 */
	protected String getFormNameUpdate() {

		return null;
	}

	 


}
