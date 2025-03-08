package co.miniso.rompefilas.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import co.miniso.rompefilas.service.BillService;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/bill")
public class ConsultingController {

	private BillService billService;

	@Autowired
	public ConsultingController(BillService billService) {
		this.billService = billService;
	}

	@GetMapping("{numBill}")
	public ResponseEntity<List<Object[]>> getBillById(@PathVariable("numBill") String numBill, HttpServletRequest request) {
		// Obtener las cookies de la petición
		Cookie[] cookies = request.getCookies();

		// Verificar si existen cookies
		if (cookies != null) {
			boolean hasAuthToken = Arrays.stream(cookies)
					.anyMatch(cookie -> "authToken".equals(cookie.getName()));

			if (hasAuthToken) {
				List<Object[]> result = billService.buscarPorId(numBill);
				return ResponseEntity.ok(result);
			}
		}
		// Si no hay cookie o no es válida, devolver 401 Unauthorized
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	}
}
