package co.miniso.rompefilas.db1.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "INV_FE_APP")
public class InvoiceDetailApp {

    @Column(name = "Store", nullable = false)
    private Integer store;

    @Column(name = "NumAtCard", nullable = false)
    private String numAtCard;

    @Column(name = "Lineas", nullable = false)
    private Integer lineas;

    @Id
    @Column(name = "Sku", nullable = false)
    private String sku;

    @Column(name = "Articulo", nullable = false)
    private String articulo;

    @Column(name = "CodigoBarras", nullable = false)
    private String codigoBarras;

    @Column(name = "Cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "PrecioSinImpuesto", nullable = false)
    private float precioSinImpuesto;

    @Column(name = "ValorDescuento", nullable = false)
    private float valorDescuento;

    @Column(name = "Impuesto", nullable = false)
    private float impuesto;

    @Column(name = "TotalLinea", nullable = false)
    private float totalLinea;

    @Column(name = "DocDate", nullable = false)
    private Date docDate;

    // Getters y Setters
    public Integer getStore() {
        return store;
    }

    public void setStore(Integer store) {
        this.store = store;
    }

    public String getNumAtCard() {
        return numAtCard;
    }

    public void setNumAtCard(String numAtCard) {
        this.numAtCard = numAtCard;
    }

    public Integer getLineas() {
        return lineas;
    }

    public void setLineas(Integer lineas) {
        this.lineas = lineas;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public float getPrecioSinImpuesto() {
        return precioSinImpuesto;
    }

    public void setPrecioSinImpuesto(float precioSinImpuesto) {
        this.precioSinImpuesto = precioSinImpuesto;
    }

    public float getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(float valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public float getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(float impuesto) {
        this.impuesto = impuesto;
    }

    public float getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(float totalLinea) {
        this.totalLinea = totalLinea;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }
}
