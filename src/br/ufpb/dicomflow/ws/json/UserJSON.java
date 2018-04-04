package br.ufpb.dicomflow.ws.json;

import java.util.Iterator;
import java.util.Set;

import br.ufpb.dicomflow.bean.PatientIF;

public class UserJSON implements JSONDecorator {
	
	private String name;
	private String email;
	
	

	public UserJSON(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}

	@Override
	public String getJSON() {
		
		StringBuilder json = new StringBuilder("");
		
		if(name != null && !name.isEmpty() && email != null  && !email.isEmpty()){
			
			
			json.append("name: \"");
			json.append(name);
			json.append("\", ");
			
			json.append("email: \"");
			json.append(email);
			json.append("\" ");
		
			return json.toString();
			
		}
		
		return "";
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	
	
	

}
