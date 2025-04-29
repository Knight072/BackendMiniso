package co.miniso.rompefilas.db1.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "INV_FE_APP")
public class InvoiceDetailApp {

    @EmbeddedId
    private InvoiceDetailAppId id;

    @ManyToOne
    @JoinColumn(name = "numAtCard", referencedColumnName = "numAtCard", insertable = false, updatable = false)
    @JsonBackReference
    private BillApp billApp;

    @Column(name = "Store", nullable = false)
    private int store;

    @Column(name = "Articulo", nullable = false)
    private String articulo;

    @Column(name = "CodigoBarras", nullable = false)
    private String codigoBarras;

    @Column(name = "Cantidad", nullable = false)
    private int cantidad;

    @Column(name = "PrecioSinImpuesto", nullable = false)
    private double precioSinImpuesto;

    @Column(name = "ValorDescuento", nullable = false)
    private double valorDescuento;

    @Column(name = "Impuesto", nullable = false)
    private int impuesto;

    @Column(name = "TotalLinea", nullable = false)
    private double totalLinea;

    @Column(name = "DocDate", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date docDate;

    // Getter and setter for id
    public InvoiceDetailAppId getId() {
        return id;
    }

    public void setId(InvoiceDetailAppId id) {
        this.id = id;
    }

    // Modify getSku and setSku to use the SKU from the composite key
    public String getSku() {
        return id != null ? id.getSku() : null;
    }

    public void setSku(String sku) {
        if (id == null) {
            id = new InvoiceDetailAppId();
        }
        id.setSku(sku);
    }

    public Integer getLineas() {
        return id != null ? id.getLineas() : 0;
    }

    public void setLineas(Integer lineas) {
        if (id == null) {
            id = new InvoiceDetailAppId();
        }
        id.setLineas(lineas);
    }

    public BillApp getBillApp() {
        return billApp;
    }

    public void setBillApp(BillApp billApp) {
        this.billApp = billApp;
    }

    public Integer getStore() {
        return store;
    }

    public void setStore(Integer store) {
        this.store = store;
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

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioSinImpuesto() {
        return precioSinImpuesto;
    }

    public void setPrecioSinImpuesto(double precioSinImpuesto) {
        this.precioSinImpuesto = precioSinImpuesto;
    }

    public double getValorDescuento() {
        return valorDescuento;
    }

    public void setValorDescuento(double valorDescuento) {
        this.valorDescuento = valorDescuento;
    }

    public int getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(int impuesto) {
        this.impuesto = impuesto;
    }

    public double getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(double totalLinea) {
        this.totalLinea = totalLinea;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }
}

