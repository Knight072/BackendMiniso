package co.miniso.rompefilas.db2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ARTICULO") // Ajusta el nombre real de la tabla y el esquema
public class Article {

    @Id
    @Column(name = "CodigoBarras", nullable = false) // Ajusta el length si es necesario
    private String barCode;

    @Column(name = "Sku")
    private String sku;

    @Column(name = "NombreDept")
    private String deptName;

    @Column(name = "Articulo")
    private String article;

    @Column(name = "ValorImpuesto")
    private Double taxValue;

    @Column(name = "PrecioSinImpuesto")
    private Double priceWithoutTax;

    @Column(name = "Impuesto")
    private String tax;

    // Getters y Setters corregidos
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public Double getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(Double taxValue) {
        this.taxValue = taxValue;
    }

    public Double getPriceWithoutTax() {
        return priceWithoutTax;
    }

    public void setPriceWithoutTax(Double priceWithoutTax) {
        this.priceWithoutTax = priceWithoutTax;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}
