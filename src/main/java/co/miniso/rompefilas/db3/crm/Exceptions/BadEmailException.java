package co.miniso.rompefilas.db3.crm.Exceptions;

public class BadEmailException extends Exception{
	private static final long serialVersionUID = 1L;
	public static final String BAD_EMAIL = "Error: El email no tiene el formato correcto.";
	
	public BadEmailException(String msg) {
		super(msg);
	}

}
