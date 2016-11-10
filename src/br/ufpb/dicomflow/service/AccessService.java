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
	static String mailRegex = "[A-Za-z0-9\\._-]+@[A-Za-z0-9]+(\\.[A-Za-z]+)*";
	static String hostRegex = "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+";
	static String portRegex = "[0-9]+";
	static String modalityRegex = "[A-Z]{2}(,[A-Z]{2})*";
	static String permissionRegex =          "(Sharing|Storage|Request|Find|Discovery) (\\*|"+modalityRegex+");";
	static String multiplePermissionRegex = "((Sharing|Storage|Request|Find|Discovery) (\\*|"+modalityRegex+");)+";
	static String acessRegex = mailRegex+"#"+hostRegex+"#"+portRegex+"#"+multiplePermissionRegex;
	
	
	
	
	public static void main(String[] args) {
		
		String mail = "daniloalexandre@gmail.com";
		String host = "ufpb.br";
		String port = "8080";
		String modality = "CT,MR";
		String permission = "Sharing *;";
		String permissions = "Sharing *;Storage CT,MR;Find *;";
		String example = "daniloalexandre@gmail.com#ufpb.br#8080#Sharing *;Storage CT,MR;Find *;";
		
		boolean matches = mail.matches(mailRegex);
		System.out.println("mail matches: " + matches);
		
		matches = host.matches(hostRegex);
		System.out.println("host matches: " + matches);
		
		matches = port.matches(portRegex);
		System.out.println("port matches: " + matches);
		
		matches = modality.matches(modalityRegex);
		System.out.println("modality matches: " + matches);
		
		matches = permission.matches(permissionRegex);
		System.out.println("permission matches: " + matches);
		
		matches = permissions.matches(multiplePermissionRegex);
		System.out.println("permissions matches: " + matches);
		
		matches = example.matches(acessRegex);
		System.out.println("example matches: " + matches);
	}

}
