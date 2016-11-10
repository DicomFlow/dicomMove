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

public class AccessService {
	
	String regex = "[A-Za-z0-9\\._-]+@[A-Za-z0-9]+(\\.[A-Za-z]+)*#[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+#[0-9]+#([Sharing|Storage|Request|Find|Discovery] *|([A-Za-z]{2}(,[A-Za-z]{2})+)+";

}
