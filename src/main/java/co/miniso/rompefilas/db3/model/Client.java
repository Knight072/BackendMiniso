package co.miniso.rompefilas.db3.model;

import jakarta.persistence.*;

@Entity
@Table(name = "customer") // Asegúrate de usar el nombre correcto de la tabla en la base de datos
public class Client {

    @Id
    @Column(name = "identification") // Clave primaria o identificador
    private String document;

    @Column(name = "typeIde") // Tipo Id
    private String typeId;

    @Column(name = "customer_no") // IdAptos
    private String idAptos;

    @Column(name = "email") // Mapea a la columna correcta en la base de datos
    private String email;

    @Column(name = "firstName") // Se podría combinar con last_name si es necesario
    private String name;

    @Column(name = "lastName") // Se podría combinar con last_name si es necesario
    private String lastName;

    @Column(name = "gender") // Se podría combinar con last_name si es necesario
    private String gender;

    // Constructor vacío requerido por JPA
    public Client() {
    }

    // Constructor con parámetros
    public Client(String document, String typeId, String idAptos, String email, String name, String lastName, String gender) {
        this.document = document;
        this.typeId = typeId;
        this.idAptos = idAptos;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.gender = gender;
    }

    // Getters y Setters
    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getIdAptos() {
        return idAptos;
    }

    public void setIdAptos(String idAptos) {
        this.idAptos = idAptos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

}
