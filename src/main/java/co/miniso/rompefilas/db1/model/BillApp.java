package co.miniso.rompefilas.db1.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "OINV_FE_APP")
public class BillApp {

	@Id
	@Column(name = "NumAtCard", nullable = false)
	private String numAtCard;

	@Column(name = "Store", nullable = false)
	private Integer store;

	@Column(name = "DocDate", nullable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date docDate;

	@Column(name = "ConsecutivoFE")
	private Integer consecutivoApp;

	@Column(name = "HoraEmisionAPP")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date horaEmisionApp;

	@Column(name = "VendedorAPP")
	private String vendedorApp;

	@Column(name = "DocTotal", nullable = false)
	private Double docTotal;

	@Column(name = "Tipo_documento")
	private String tipoDocumento;

	@Column(name = "CC_Nit", nullable = false)
	private String ccNit;

	@Column(name = "Nombre_Cliente", nullable = false)
	private String nombreCliente;

	@OneToMany(mappedBy = "billApp", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<InvoiceDetailApp> productos;

	public Integer getStore() {
		return store;
	}

	public void setStore(Integer store) {
		this.store = store;
	}

	public Date getDocDate() {
		return docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	public String getNumAtCard() {
		return numAtCard;
	}

	public void setNumAtCard(String numAtCard) {
		this.numAtCard = numAtCard;
	}

	public Integer getConsecutivoApp() {
		return consecutivoApp;
	}

	public void setConsecutivoApp(Integer consecutivoApp) {
		this.consecutivoApp = consecutivoApp;
	}

	public Date getHoraEmisionApp() {
		return horaEmisionApp;
	}

	public void setHoraEmisionApp(Date horaEmisionApp) {
		this.horaEmisionApp = horaEmisionApp;
	}

	public String getVendedorApp() {
		return vendedorApp;
	}

	public void setVendedorApp(String vendedorApp) {
		this.vendedorApp = vendedorApp;
	}

	public Double getDocTotal() {
		return docTotal;
	}

	public void setDocTotal(Double docTotal) {
		this.docTotal = docTotal;
	}

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getCcNit() {
		return ccNit;
	}

	public void setCcNit(String ccNit) {
		this.ccNit = ccNit;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public List<InvoiceDetailApp> getProductos() {
		return productos;
	}

	public void setProductos(List<InvoiceDetailApp> productos) {
		this.productos = productos;
	}
}
