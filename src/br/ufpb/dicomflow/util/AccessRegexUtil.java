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

import java.util.StringTokenizer;

public class AccessRegexUtil {

	public static final String IN = "IN";
	public static final String OUT = "OUT";
	public static final String ACCESS_DELIM = "#";
	public static final String PERMISSION_DELIM = ";";
	public static final String TYPE_REGEX = "("+IN+"|"+OUT+")";
	public static final String CODE_REGEX = "[A-Za-z0-9]+";
	public static final String MAIL_REGEX = "[A-Za-z0-9\\._-]+@[A-Za-z0-9]+(\\.[A-Za-z]+)*";
//	public static final String HOST_REGEX = "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*";
	public static final String HOST_REGEX = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]\\.[^\\s]{2,})";
	public static final String PORT_REGEX = "[0-9]+";
	public static final String MODALITY_REGEX = "[A-Z]{2}(,[A-Z]{2})*";
	public static final String PERMISSION_REGEX =          "(Sharing|Storage|Request|Find|Discovery) (\\*|"+MODALITY_REGEX+")";
	public static final String MULTI_PERMISSION_REGEX = PERMISSION_REGEX+"(;"+PERMISSION_REGEX+")*";
	public static final String ACCESS_REGEX = TYPE_REGEX + ACCESS_DELIM + MAIL_REGEX + ACCESS_DELIM + CODE_REGEX + ACCESS_DELIM + HOST_REGEX + ACCESS_DELIM+ PORT_REGEX + ACCESS_DELIM + MULTI_PERMISSION_REGEX;
	
	
	public static boolean accessMatches(String access){
		return access.matches(ACCESS_REGEX);
	}
	
	public static boolean permissionsMatches(String permissions){
		return permissions.matches(MULTI_PERMISSION_REGEX);
	}
	
	public static String getType(String access){
		if(accessMatches(access))
			return getElement(access, ACCESS_DELIM, 0);
		return null;
	}
	
	public static String getMail(String access){
		if(accessMatches(access))
			return getElement(access, ACCESS_DELIM, 1);
		return null;
	}
	
	public static String getCode(String access){
		if(accessMatches(access))
			return getElement(access, ACCESS_DELIM, 2);
		return null;
	}
	
	public static String getHost(String access){
		if(accessMatches(access))
			return getElement(access, ACCESS_DELIM, 3);
		return null;
	}
	
	public static String getPort(String access){
		if(accessMatches(access))
			return getElement(access, ACCESS_DELIM, 4);
		return null;
	}
	
	public static String getPermissions(String access){
		if(accessMatches(access))
			return getElement(access, ACCESS_DELIM, 5);
		return null;
	}
	
	public static int countPermissions(String permissions){
		if(permissionsMatches(permissions)){
			StringTokenizer tokenizer = new StringTokenizer(permissions, PERMISSION_DELIM);
			return tokenizer.countTokens();
		}
		return 0;
	}
	
	public static String getPermission(String permissions, int index){
		if(permissionsMatches(permissions))
			return getElement(permissions, PERMISSION_DELIM, index);
		return null;
	}
	
	public static String getElement(String value, String delim, int index) {
		StringTokenizer tokenizer = new StringTokenizer(value, delim);
		return getToken(tokenizer, index);
	}
	
	/**
	 * Returns the token at index. Null if index is invalid or out of range
	 * @param tokenizer
	 * @param index
	 * @return
	 */
	private static String getToken(StringTokenizer tokenizer, int index){
		String token = null;
		for (int i = 0; i <= index; i++) {
			if(tokenizer.hasMoreTokens())
				token = tokenizer.nextToken();
			else
				return null;
		}
		return token;
	}
	
	
	public static void main(String[] args) {
		String type = "IN";
		String code = "cjfjw15n4roav0177zg3e0zqy";
		String mail = "email@domain.com";
		String host = "http://domain.com";
		String port = "8080";
		String modality = "CT,MR";
		String permission = "Sharing *";
		String permissions = "Sharing *;Storage CT,MR;Find *";
		String example = "OUT#email@domain.com#cjfjw15n4roav0177zg3e0zqy#http://domain.com#8080#Sharing *;Storage CT,MR;Find *";
		
		String example2 = "IN#protocolointegracao@gmail.com#cjfjw15n4roav0177zg3e0zqy#http://localhost.com#8444#Storage *";
		
		boolean matches = type.matches(TYPE_REGEX);
		System.out.println("type matches: " + matches);
		
		matches = mail.matches(MAIL_REGEX);
		System.out.println("mail matches: " + matches);
		
		matches = code.matches(CODE_REGEX);
		System.out.println("code matches: " + matches);
		
		matches = host.matches(HOST_REGEX);
		System.out.println("host matches: " + matches);
		
		matches = port.matches(PORT_REGEX);
		System.out.println("port matches: " + matches);
		
		matches = modality.matches(MODALITY_REGEX);
		System.out.println("modality matches: " + matches);
		
		matches = permission.matches(PERMISSION_REGEX);
		System.out.println("permission matches: " + matches);
		
		matches = permissions.matches(MULTI_PERMISSION_REGEX);
		System.out.println("permissions matches: " + matches);
		
		matches = example2.matches(ACCESS_REGEX);
		System.out.println("example2 matches: " + matches);
		
		System.out.println("Type: "+ getType(example));
		System.out.println("E-mail: "+ getMail(example));
		System.out.println("Code: "+ getCode(example));
		System.out.println("Host: "+ getHost(example));
		System.out.println("Port: "+ getPort(example));
		System.out.println("Permissions: "+ getPermissions(example));
		
		int count = countPermissions(permissions);
		System.out.println("Total Permissions: "+ count);
		
		for (int i = 0; i < count; i++) {
			System.out.println("Permission: " + getPermission(permissions, i));
		}
		
	}
}
