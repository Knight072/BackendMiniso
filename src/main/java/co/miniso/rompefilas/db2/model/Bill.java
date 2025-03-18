package co.miniso.rompefilas.db2.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "OINV_FE")
public class Bill {

	@Id
	@Column(name = "DocNum")
	private Integer docNum;

	@Column(name = "Store", nullable = false)
	private Integer store;

	@Column(name = "NumAtCard")
	private String numAtCard;

	@Column(name = "HoraEmisionPOS")
	private Date horaEmisionPos;

	@Column(name = "DocTotal")
	private Double docTotal;

	@Column(name = "CC_Nit")
	private String ccNit;

	@Column(name = "Nombre_Cliente")
	private String nombreCliente;

	@Column(name = "VendedorPos")
	private String cajero;

	@OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<InvoiceDetail> invoiceDetails;

	public String getNumAtCard() {
		return numAtCard;
	}
}
