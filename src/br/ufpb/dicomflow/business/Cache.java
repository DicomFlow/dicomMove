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


package br.ufpb.dicomflow.business;



public class Cache {
	
	private static Cache singleton=null;
	
	private boolean makeCache;
	
	private Cache(){
		
	}
	
	public static Cache getInstance(){
		if (singleton == null) {
			singleton = new Cache();
		}
		return singleton;
	}
	
	
	
	
	
	/**
	 * monta o cache pela primeira vez
	 */
	public void makeCache(){
		if (!makeCache) {
			System.err.println("Fazendo o CHACHE!!!!!");
			makeCache=true;
		}
		
	}


	
	



}
