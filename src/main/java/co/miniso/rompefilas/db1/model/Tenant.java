package co.miniso.rompefilas.db1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "MAPEOTIENDA")
public class Tenant {

    @Id
    @Column(name = "idTienda")
    private int store;

    @Column(name = "nombreTienda")
    private String name;

    @Column(name = "ipPOS")
    private String ipAddress;

    @Column(name = "prefijo")
    private String prefix;

    @Column(name = "consecutivo")
    private int consecutive;

    @Column(name = "resolucion")
    private String resolution;

    @Column(name = "fechaAprobacion")
    private Date approvalDate;

    @Column(name = "fechaExpedicion")
    private Date issueDate;

    @Column(name = "sucursalEbill")
    private int ebillBranch;

    @Column(name = "municipio")
    private String municipality;

    @Column(name = "codigoMunicipio")
    private int codeMunicipality;

    @Column(name = "departamento")
    private String department;

    @Column(name = "codigoDepartamento")
    private int codeDepartment;

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(int consecutive) {
        this.consecutive = consecutive;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public int getEbillBranch() {
        return ebillBranch;
    }

    public void setEbillBranch(int ebillBranch) {
        this.ebillBranch = ebillBranch;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public int getCodeMunicipality() {
        return codeMunicipality;
    }

    public void setCodeMunicipality(int codeMunicipality) {
        this.codeMunicipality = codeMunicipality;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getCodeDepartment() {
        return codeDepartment;
    }

    public void setCodeDepartment(int codeDepartment) {
        this.codeDepartment = codeDepartment;
    }
}