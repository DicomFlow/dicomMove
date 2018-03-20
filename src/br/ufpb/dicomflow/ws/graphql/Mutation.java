package br.ufpb.dicomflow.ws.graphql;

import br.ufpb.dicomflow.ws.json.JSONDecorator;

public class Mutation implements GraphqlEntity {
	
	private String query;
	
	public Mutation() throws GraphqlException{
	}

	public void buildQuery(JSONDecorator decorator, String operation, String response) throws GraphqlException {
		if(operation == null || operation.isEmpty()){
			throw new GraphqlException("Operation is null or empty.");
		}
		
		if(decorator == null || decorator.getJSON().isEmpty()){
			throw new GraphqlException("Decorator is null or empty.");
		}
		if(response == null || response.isEmpty()){
			throw new GraphqlException("response is null or empty.");
		}
		StringBuilder builder = new StringBuilder("mutation { ");
		builder.append(operation);
		builder.append("(");
		builder.append(decorator.getJSON());
		builder.append(")");
		builder.append(response);
		builder.append("}");
		query = builder.toString();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	
	



}
