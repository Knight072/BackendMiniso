package co.miniso.rompefilas.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import co.miniso.rompefilas.db1.model.Tenant;
import co.miniso.rompefilas.db1.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.miniso.rompefilas.db1.model.Login;
import co.miniso.rompefilas.db1.repository.LoginRepository;
import java.util.Base64;
import java.util.Optional;

@Service
public class LoginService {

	private final LoginRepository loginRepository;
	private final TenantRepository tenantRepository;

	@Autowired
	public LoginService(LoginRepository loginRepository, TenantRepository tenantRepository) {
		this.loginRepository = loginRepository;
		this.tenantRepository = tenantRepository;
	}

	public void registerNewUser(Login user) throws NoSuchAlgorithmException {
		if (loginRepository.findByDocument(user.getDocument()).isPresent()) {
			throw new RuntimeException("Username already exists");
		}
		user.setPassword(hashPassword(user.getPassword()));
		loginRepository.save(user);
	}

	public Login validarUsuario(Login logInUser) {
		Optional<Login> user = loginRepository.findByDocument(logInUser.getDocument());
		if (user.isPresent()) {
			try {
				if (hashPassword(logInUser.getPassword()).equals(user.get().getPassword())
						&& logInUser.getStore() == user.get().getStore()) {
					return user.get();
				}
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("Error al hashear la contraseña", e);
			}
		}
		return null; // Retorna null si el usuario no existe o la contraseña no coincide
	}

	public Object[] validateBillData(Login user){
		Object[] data = new Object[3];
		Optional<Tenant> tenant = tenantRepository.findById(Integer.toString(user.getStore()));
		if(tenant.isPresent()){
			data[0] = user.getDocument();
			data[1] = tenant.get().getPrefix();
			data[2] = tenant.get().getConsecutive();
			return data;
		}
		return null;
	}

	String hashPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hash = md.digest(password.getBytes());
		return Base64.getEncoder().encodeToString(hash);
	}
}