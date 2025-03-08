package co.miniso.rompefilas.db3.crm.exceptions;

public class ExistEmailException extends Exception{
	private static final long serialVersionUID = 1L;
	public static final String EXIST_EMAIL = "Error:El email ya existe en otro usuario. Por favor, ingresar un correo diferente";
	
	public ExistEmailException(String msg) {
		super(msg);
	}

}
