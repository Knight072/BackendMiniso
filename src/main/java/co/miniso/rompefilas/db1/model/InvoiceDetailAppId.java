package co.miniso.rompefilas.db1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InvoiceDetailAppId implements Serializable {
    @Column(name = "NumAtCard", nullable = false)
    private String numAtCard;

    @Column(name = "Lineas", nullable = false)  // Note the column name change to match your database
    private Integer lineas;

    @Column(name = "Sku", nullable = false)
    private String sku;

    public InvoiceDetailAppId() {
        this.lineas = 0;  // Provide a default value
    }

    public InvoiceDetailAppId(String numAtCard, Integer lineas, String sku) {
        this.numAtCard = numAtCard;
        this.lineas = (lineas != null) ? lineas : 0;  // Ensure non-null value
        this.sku = sku;
    }

    // Getters and setters
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
        this.lineas = (lineas != null) ? lineas : 0;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    // Update equals and hashCode to use 'lineas'
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceDetailAppId that = (InvoiceDetailAppId) o;
        return Objects.equals(numAtCard, that.numAtCard) &&
                Objects.equals(lineas, that.lineas) &&
                Objects.equals(sku, that.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numAtCard, lineas, sku);
    }

    @Override
    public String toString() {
        return "InvoiceDetailAppId{" +
                "numAtCard='" + numAtCard + '\'' +
                ", lineas=" + lineas +
                ", sku='" + sku + '\'' +
                '}';
    }
}