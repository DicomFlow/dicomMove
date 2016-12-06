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
package br.ufpb.dicomflow.util;

import java.util.UUID;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.service.ServiceLocator;

public class CredentialUtil {

	public static String generateCredentialKey(){
		return UUID.randomUUID().toString();
		
	}

	public static Access getDomain(){
		
		return (Access) ServiceLocator.singleton().getPersistentService().select("type", Access.IN, Access.class);
	}

}
