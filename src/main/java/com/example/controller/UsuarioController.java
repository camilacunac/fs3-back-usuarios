package com.example.controller;

import com.example.model.Response;
import com.example.model.Usuario;
import com.example.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    // Crear un nuevo usuario
    @PostMapping("/registro")
    public ResponseEntity<Response> crearUsuario(@RequestBody Usuario usuario) {
        try {
            return usuarioService.crearUsuario(usuario);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("error", null, "Error interno al crear el usuario"));
        }
    }

    // Inicio de sesión (Login)
    @PostMapping("/login")
    public ResponseEntity<Response> login(@RequestParam String correo, @RequestParam String contrasena) {
        try {
            return usuarioService.login(correo, contrasena);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new Response("error", null, "Error interno durante el inicio de sesión"));
        }
    }

    // Actualizar el rol de un usuario
    @PutMapping("/{id}/rol")
    public ResponseEntity<Response> actualizarRol(@PathVariable Long id, @RequestParam String nuevoRol) {
        try {
            return usuarioService.actualizarRol(id, nuevoRol);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("error", null, "Error interno al actualizar el rol"));
        }
    }

    // Eliminar un usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> eliminarUsuario(@PathVariable Long id) {
        try {
            return usuarioService.eliminarUsuario(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Response("error", null, "Error interno al eliminar el usuario"));
        }
    }
}
