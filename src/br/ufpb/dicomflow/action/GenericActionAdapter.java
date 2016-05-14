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
