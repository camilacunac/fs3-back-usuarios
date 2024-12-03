package com.example.service;

import com.example.model.Response;
import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private Validator validator;

    @Override
    public ResponseEntity<Response> crearUsuario(Usuario usuario) throws Exception {
        Response response;
        try {
            Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);

            if (!violations.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder();
                for (ConstraintViolation<Usuario> violation : violations) {
                    errorMessages.append(violation.getPropertyPath())
                            .append(": ")
                            .append(violation.getMessage())
                            .append("; ");
                }
                response = new Response("error", null, errorMessages.toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            Usuario alreadyExistUser = usuarioRepository.findByCorreo(usuario.getCorreo());
            if (alreadyExistUser != null) {
                response = new Response("error", null, "El correo ya existe");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            if (!isValidPassword(usuario.getContrasena())) {
                response = new Response("error", null,
                        "La contraseña debe tener entre 8 y 15 caracteres, incluyendo una letra mayúscula, una minúscula, un número y un carácter especial.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            usuario.setFechaRegistro(LocalDate.now());
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
            Usuario newUser = usuarioRepository.save(usuario);
            response = new Response("success", newUser, "");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response = new Response("error", null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<Response> login(String correo, String contrasena) throws Exception {
        Response response;
        try {
            Usuario usuario = usuarioRepository.findByCorreo(correo);
            if (usuario == null) {
                response = new Response("error", null, "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Verificar contraseña
            if (!passwordEncoder.matches(contrasena, usuario.getContrasena())) {
                response = new Response("error", null, "Contraseña incorrecta");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            response = new Response("success", usuario, "");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response = new Response("error", null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<Response> actualizarRol(Long idUsuario, String nuevoRol) throws Exception {
        Response response;
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (!usuarioOpt.isPresent()) {
                response = new Response("error", null, "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Usuario usuario = usuarioOpt.get();
            if (!isValidRole(nuevoRol)) {
                response = new Response("error", null, "Rol no valido");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            usuario.setRol(nuevoRol);
            Usuario updatedUser = usuarioRepository.save(usuario);
            response = new Response("success", updatedUser, "");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response = new Response("error", null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public ResponseEntity<Response> eliminarUsuario(Long idUsuario) throws Exception {
        Response response;
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(idUsuario);
            if (!usuarioOpt.isPresent()) {
                response = new Response("error", null, "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            usuarioRepository.deleteById(idUsuario);
            response = new Response("success", new String("Usuario eliminado con exito"), "");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response = new Response("error", null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

    }

    @Override
    public ResponseEntity<Response> actualizarUsuario(Long id, Usuario updatedUsuario) {
        Response response;
        try {
            // Buscar el usuario por ID
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
            if (!optionalUsuario.isPresent()) {
                response = new Response("error", null, "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            updatedUsuario.setContrasena(optionalUsuario.get().getContrasena());
            updatedUsuario.setRol(optionalUsuario.get().getRol());
            Set<ConstraintViolation<Usuario>> violations = validator.validate(updatedUsuario);

            if (!violations.isEmpty()) {
                StringBuilder errorMessages = new StringBuilder();
                for (ConstraintViolation<Usuario> violation : violations) {
                    errorMessages.append(violation.getPropertyPath())
                            .append(": ")
                            .append(violation.getMessage())
                            .append("; ");
                }
                response = new Response("error", null, errorMessages.toString());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Usuario usuario = optionalUsuario.get();

            // Actualizar los datos del usuario (sin cambiar el correo ni la contraseña)
            usuario.setNombre(updatedUsuario.getNombre());
            usuario.setApellido(updatedUsuario.getApellido());
            usuario.setDireccion(updatedUsuario.getDireccion());
            usuario.setTelefono(updatedUsuario.getTelefono());

            // Guardar los cambios en la base de datos
            Usuario updatedUser = usuarioRepository.save(usuario);

            response = new Response("success", updatedUser, "Usuario actualizado exitosamente");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response = new Response("error", null, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    // Funciones de validación
    public boolean isValidRole(String role) {
        String lowerCaseRole = role.toLowerCase();
        return lowerCaseRole.equals("admin") || lowerCaseRole.equals("cliente");
    }

    public boolean isValidPassword(String password) {
        return password.matches(
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{8,15}$");
    }

}
