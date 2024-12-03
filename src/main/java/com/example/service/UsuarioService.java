package com.example.service;

import com.example.model.Response;
import com.example.model.Usuario;

import java.util.List;

import org.springframework.http.ResponseEntity;

public interface UsuarioService {
    ResponseEntity<Response> crearUsuario(Usuario usuario) throws Exception;

    ResponseEntity<Response> login(String correo, String contrasena) throws Exception;

    ResponseEntity<Response> actualizarRol(Long idUsuario, String nuevoRol) throws Exception;

    ResponseEntity<Response> eliminarUsuario(Long idUsuario) throws Exception;

    ResponseEntity<Response> actualizarUsuario(Long id, Usuario updatedUsuario) throws Exception;

    List<Usuario> getAllUsuarios();
}
