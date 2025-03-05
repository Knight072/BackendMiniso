package co.miniso.rompefilas.db2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ARTICULO") // Ajusta el nombre real de la tabla y el esquema
public class Article {

    @Id
    @Column(name = "CodigoBarra", nullable = false, length = 50) // Ajusta el length si es necesario
    private String codigoBarra;

    @Column(name = "Sku")
    private Integer sku;

    @Column(name = "NombreDept", length = 100)
    private String nombreDept;

    @Column(name = "Producto", length = 200)
    private String producto;

    @Column(name = "PERM_UNIT_PRICE")
    private Double permUnitPrice;

    @Column(name = "CUR_UNIT_PRICE")
    private Double curUnitPrice;

    // Getters y Setters
    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Integer getSku() {
        return sku;
    }

    public void setSku(Integer sku) {
        this.sku = sku;
    }

    public String getNombreDept() {
        return nombreDept;
    }

    public void setNombreDept(String nombreDept) {
        this.nombreDept = nombreDept;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public Double getPermUnitPrice() {
        return permUnitPrice;
    }

    public void setPermUnitPrice(Double permUnitPrice) {
        this.permUnitPrice = permUnitPrice;
    }

    public Double getCurUnitPrice() {
        return curUnitPrice;
    }

    public void setCurUnitPrice(Double curUnitPrice) {
        this.curUnitPrice = curUnitPrice;
    }
}



