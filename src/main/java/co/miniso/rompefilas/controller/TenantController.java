package co.miniso.rompefilas.controller;

import co.miniso.rompefilas.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"localhost:8080, localhost:3000"}, allowCredentials = "true")
@RestController
@RequestMapping("/tenant")
public class TenantController {
    private final TenantService tenantService;

    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping("/stores")
    public List<Object[]> getStores(){
        return tenantService.getNameStores();
    }
}
