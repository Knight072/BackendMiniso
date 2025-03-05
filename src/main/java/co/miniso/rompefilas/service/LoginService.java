package co.miniso.rompefilas.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.miniso.rompefilas.db1.model.Login;
import co.miniso.rompefilas.db1.repository.LoginRepository;
import java.util.Base64;
import java.util.Optional;

@Service
public class LoginService {

	private LoginRepository loginRepository;

	@Autowired
	public LoginService(LoginRepository loginRepository) {

		this.loginRepository = loginRepository;
	}

	public Login registerNewUser(Login user) throws NoSuchAlgorithmException {
		if (loginRepository.findByDocument(user.getDocument()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}
		user.setPassword(hashPassword(user.getPassword()));
		return loginRepository.save(user);
	}

	public Login validarUsuario(Login LogInUser) {
		Optional<Login> user = loginRepository.findByDocument(LogInUser.getDocument());

		if (user.isPresent()) {
			try {
				if (hashPassword(LogInUser.getPassword()).equals(user.get().getPassword())) {
					return user.get();
				}
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Error al hashear la contraseña", e);
			}
		}
		return null; // Retorna null si el usuario no existe o la contraseña no coincide
	}

	String hashPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(password.getBytes());
		return Base64.getEncoder().encodeToString(hash);
	}
}