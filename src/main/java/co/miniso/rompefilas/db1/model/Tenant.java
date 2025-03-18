package co.miniso.rompefilas.db1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @Column(name = "store")
    private int store;

    @Column(name = "name")
    private String name;

    @Column(name = "ip_address")
    private String ipAddress;

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

}

