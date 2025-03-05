package co.miniso.rompefilas.db2.model;

import jakarta.persistence.*;

@Entity
@Table(name = "INV_FE")
public class InvoiceDetail {

    @Id
    @Column(name = "Lineas")
    private Integer lineas;

    @Column(name = "Store", nullable = false)
    private Integer store;

    @Column(name = "DocNum", nullable = false)
    private Integer docNum;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "Store", referencedColumnName = "Store", insertable = false, updatable = false),
            @JoinColumn(name = "DocNum", referencedColumnName = "DocNum", insertable = false, updatable = false)
    })
    private Bill bill;

    @Column(name = "Sku")
    private String sku;

    @Column(name = "CodigoBarras")
    private String codigoBarras;

    @Column(name = "Articulo")
    private String articulo;

    @Column(name = "Cantidad")
    private Integer cantidad;

    @Column(name = "PrecioSinImpuesto")
    private Double precioSinImpuesto;
}
