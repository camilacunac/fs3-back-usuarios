package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo debe tener un formato válido.")
    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria.")
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 5, message = "El nombre debe tener al menos 5 caracteres.")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio.")
    @Size(min = 5, message = "El apellido debe tener al menos 5 caracteres.")
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @NotBlank(message = "El rol es obligatorio.")
    @Pattern(regexp = "^(admin|cliente)$", message = "El rol debe ser 'admin' o 'cliente'.")
    @Column(name = "rol", nullable = false)
    private String rol;

    @Size(min = 15, message = "La dirección debe tener al menos 15 caracteres.")
    @Column(name = "direccion")
    private String direccion;

    @Pattern(regexp = "\\d{9,}", message = "El teléfono debe contener solo números y tener al menos 9 dígitos.")
    @Column(name = "telefono")
    private String telefono;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro;

    // Constructor vacío para JPA
    public Usuario() {
    }

    // Constructor con parámetros
    public Usuario(String correo, String contrasena, String nombre, String apellido, String rol, String direccion,
            String telefono, LocalDate fechaRegistro) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
        this.direccion = direccion;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters

    public Long getId() {
        return idUsuario;
    }

    public void setId(Long id) {
        this.idUsuario = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
