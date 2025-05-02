package co.miniso.rompefilas.controller;

import co.miniso.rompefilas.configuration.DynamicDataSource;
import co.miniso.rompefilas.db2.model.Article;
import co.miniso.rompefilas.db2.model.Bill;
import co.miniso.rompefilas.service.TenantService;
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

//@CrossOrigin(origins = {"localhost:8080, localhost:3000, 192.168.84.17:8080"}, allowCredentials = "true")
@RestController
@RequestMapping("/bill")
public class ConsultingController {

	private final BillService billService;
	private final TenantService tenantService;

	@Autowired
	public ConsultingController(BillService billService, TenantService tenantService) {
		this.billService = billService;
		this.tenantService = tenantService;
	}

	@GetMapping("{numBill}")
	public ResponseEntity<List<Object[]>> getBillById(@PathVariable("numBill") String numBill,
													  @RequestHeader("X-Tienda") String tienda,
													  HttpServletRequest request) {
		// Obtener las cookies de la petición
		Cookie[] cookies = request.getCookies();

		// Verificar si existen cookies
		if (cookies != null) {
			boolean hasAuthToken = Arrays.stream(cookies)
					.anyMatch(cookie -> "authToken".equals(cookie.getName()));

			if (hasAuthToken) {
				if (!tenantService.getAllTenants().containsKey(tienda)) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
							.body(null);
				}
				// 🔹 Cambiar la base de datos activa
				DynamicDataSource.setCurrentTenant(tienda);
				try {
					List<Object[]> factura = billService.buscarPorId(numBill);
					return ResponseEntity.ok(factura);
				} finally {
					DynamicDataSource.clear(); // 🔹 Limpia la tienda después de la petición
				}
			}
		}
		// Si no hay cookie o no es válida, devolver 401 Unauthorized
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	}
}
