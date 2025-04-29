package co.miniso.rompefilas.db1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "LOGIN")
public class Login {

	@Id
	@Column(name = "document", nullable = false)
	private String document;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "idTienda", nullable = false)
	private int store;

	public Login() {
	}

	public Login(String document, String username, String password, int store) {
		this.document = document;
		this.username = username;
		this.password = password;
		this.store = store;
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

	public int getStore() {
		return store;
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

	public void setStore(int store) {
		this.store = store;
	}
}
