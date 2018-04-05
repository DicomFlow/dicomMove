package br.ufpb.dicomflow.ws.json;

public class UserJSON implements JSONDecorator {
	
	private String id;
	private String name;
	private String email;
	
	
	public UserJSON(String id) {
		super();
		this.id = id;
	}

	public UserJSON(String name, String email) {
		super();
		this.name = name;
		this.email = email;
	}

	@Override
	public String getJSON() {
		
		StringBuilder json = new StringBuilder("");
		
		if(id != null && !id.isEmpty()){
			json.append(" \"");
			json.append(id);
			json.append("\" ");
			
			return json.toString();
		}
		
		else if(name != null && !name.isEmpty() && email != null  && !email.isEmpty()){
			
			
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

	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
