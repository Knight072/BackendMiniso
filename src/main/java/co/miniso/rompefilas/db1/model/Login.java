package co.miniso.rompefilas.db1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "login") // Nombre de la tabla en SQL Server
public class Login {

	@Id
	@Column(name = "document", nullable = false)
	private String document;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;
	
	public Login() {
	}

	public Login(String document, String username, String password) {
		this.document = document;
		this.username = username;
		this.password = password;
	}

	public String getDocument() {
		return document;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
