package co.miniso.rompefilas.controller;

import co.miniso.rompefilas.db3.model.Client;
import co.miniso.rompefilas.service.ClientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("{doc}")
    public ResponseEntity<?> getClientByDoc(@PathVariable("doc") String doc, HttpServletRequest request) {
        // Obtener cookies de la solicitud
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            boolean hasAuthToken = Arrays.stream(cookies)
                    .anyMatch(cookie -> "authToken".equals(cookie.getName()));

            if (hasAuthToken) {
                Client client = clientService.getClientByDoc(doc);
                return ResponseEntity.ok(client);
            }
        }

        // Si no hay cookie o no es válida, devolver 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado: Sesión expirada o inválida.");
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestBody List<Client> clientDataOldNew, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            boolean hasAuthToken = Arrays.stream(cookies)
                    .anyMatch(cookie -> "authToken".equals(cookie.getName()));

            if (hasAuthToken) {
                String response = clientService.updateClient(clientDataOldNew.get(0),clientDataOldNew.get(1));
                return ResponseEntity.ok(response);
            }
        }

        // Si no hay cookie o no es válida, devolver 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso denegado: Sesión expirada o inválida.");
    }
}
