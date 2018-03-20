package br.ufpb.dicomflow.ws.graphql;

public class GraphqlException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6404487673823230671L;

	public GraphqlException() {
		super();
	}

	public GraphqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GraphqlException(String message, Throwable cause) {
		super(message, cause);
	}

	public GraphqlException(String message) {
		super(message);
	}

	public GraphqlException(Throwable cause) {
		super(cause);
	}
	
	

}
