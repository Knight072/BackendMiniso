package co.miniso.rompefilas.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import co.miniso.rompefilas.db1.model.Login;
import co.miniso.rompefilas.db1.model.Session;
import co.miniso.rompefilas.db1.repository.SessionRepository;
import co.miniso.rompefilas.service.LoginService;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"localhost:8080, localhost:3000, 192.168.84.17:8080"}, allowCredentials = "true")
@RestController
@RequestMapping
public class LoginController {

	private final LoginService loginService;
	private final SessionRepository sessionRepository;

	@Autowired
	public LoginController(LoginService loginService, SessionRepository sessionRepository) {
		this.loginService = loginService;
		this.sessionRepository = sessionRepository;
	}

	@PostMapping("/login")
	public ResponseEntity<Object[]> loginSubmit(@RequestBody Login userLogin, HttpServletResponse response) {
		Login user = loginService.validarUsuario(userLogin);
		if (user == null) {
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}
		Session session = new Session(UUID.randomUUID(), Instant.now(), user);
		sessionRepository.save(session);
		Object[] data = loginService.validateBillData(user);
		// Configurar la cookie HTTP-Only
		Cookie authCookie = new Cookie("authToken", session.getToken().toString());
		authCookie.setHttpOnly(true); // 🔒 No accesible desde JavaScript
		authCookie.setSecure(true); // Solo HTTPS (en producción)
		authCookie.setPath("/"); // Disponible en toda la app
		authCookie.setMaxAge(60 * 60 * 2); // 2 horas de duración
		response.addCookie(authCookie);
		return new ResponseEntity<>(data, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<Boolean> logout(HttpServletResponse response) {
		try{
			Cookie authCookie = new Cookie("authToken", "");
			authCookie.setMaxAge(0); // Expira inmediatamente
			authCookie.setPath("/");
			authCookie.setHttpOnly(true); // Mantener HttpOnly
			response.addCookie(authCookie);
		}catch (Exception e){
			return new ResponseEntity<>(false, HttpStatus.NON_AUTHORITATIVE_INFORMATION);
		}
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody Login user) {
		try {
			loginService.registerNewUser(user);
			return new ResponseEntity<>("Creado", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.CREATED);
		}
	}
}
