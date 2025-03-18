package co.miniso.rompefilas.controller;

import co.miniso.rompefilas.db1.model.BillApp;
import co.miniso.rompefilas.service.BillingService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/facturacion")
public class BillingController {

    private final BillingService billingService;

    @Autowired
    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    public ResponseEntity<Boolean> billingRequest(@RequestBody BillApp bill, HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            boolean hasAuthToken = Arrays.stream(cookies).anyMatch(
                    cookie -> "authToken".equals(cookie.getName()));

            if (hasAuthToken) {
                billingService.saveBill(bill);
                return ResponseEntity.ok(true);
            }
        }
        // Si no hay cookie o no es válida, devolver 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
