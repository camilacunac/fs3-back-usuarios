package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "direccion", nullable = true)
    private String direccion;

    @Column(name = "telefono", nullable = true)
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
