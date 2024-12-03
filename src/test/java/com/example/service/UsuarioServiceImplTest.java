package com.example.service;

import com.example.model.Response;
import com.example.model.Usuario;
import com.example.repository.UsuarioRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UsuarioServiceImplTest {

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private Validator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearUsuario_Success() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());
        usuario.setCorreo("test@example.com");
        usuario.setContrasena("Password1!");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        ResponseEntity<Response> response = usuarioService.crearUsuario(usuario);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getBody().getState());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setContrasena("$2a$10$8Yn/nWqz0..xr2SQOzDTgu5UotcyqFnYzcoSyD2dNdo.LaZoKG5fi");

        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(usuario);

        // Act
        ResponseEntity<Response> response = usuarioService.login("test@example.com", "Caracoles123#");

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getBody().getState());
    }

    @Test
    void testLogin_UserNotFound() throws Exception {
        // Arrange
        when(usuarioRepository.findByCorreo("nonexistent@example.com")).thenReturn(null);

        // Act
        ResponseEntity<Response> response = usuarioService.login("nonexistent@example.com", "Password1!");

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals("Usuario no encontrado", response.getBody().getError());
    }

    @Test
    void testActualizarRol_ValidRole() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarRol(1L, "cliente");

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getBody().getState());
        assertEquals("cliente", ((Usuario) response.getBody().getRes()).getRol());
    }

    @Test
    void testActualizarRol_UserNotFound() throws Exception {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarRol(1L, "admin");

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario_UserNotFound() throws Exception {
        // Arrange
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Response> response = usuarioService.eliminarUsuario(1L);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        verify(usuarioRepository, never()).deleteById(1L);
    }

    @Test
    void testActualizarRol_InvalidRole() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarRol(1L, "invalidRole");

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testLogin_InvalidPassword() throws Exception {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setCorreo("test@example.com");
        usuario.setContrasena("$2a$10$validpasswordhash");

        when(usuarioRepository.findByCorreo("test@example.com")).thenReturn(usuario);

        // Act
        ResponseEntity<Response> response = usuarioService.login("test@example.com", "wrongpassword");

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
    }

    @Test
    void testCrearUsuario_Exception() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());

        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new RuntimeException("Error interno"));

        // Act
        ResponseEntity<Response> response = usuarioService.crearUsuario(usuario);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals("Error interno", response.getBody().getError());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario_Exception() throws Exception {
        // Arrange
        when(usuarioRepository.findById(1L)).thenThrow(new RuntimeException("Error interno"));

        // Act
        ResponseEntity<Response> response = usuarioService.eliminarUsuario(1L);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals("Error interno", response.getBody().getError());
    }

    @Test
    void testActualizarRol_Success() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarRol(1L, "cliente");

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getBody().getState());
        assertEquals("cliente", ((Usuario) response.getBody().getRes()).getRol());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testEliminarUsuario_Success() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).deleteById(1L);

        // Act
        ResponseEntity<Response> response = usuarioService.eliminarUsuario(1L);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getBody().getState());
        assertEquals("Usuario eliminado con exito", response.getBody().getRes());
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void testCrearUsuario_InvalidData() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "invalid-email",
                "short",
                "Nom",
                "Ape",
                "invalidRole",
                "Short",
                "123",
                LocalDate.now());
        when(validator.validate(any(Usuario.class))).thenReturn(Set.of());

        // Act
        ResponseEntity<Response> response = usuarioService.crearUsuario(usuario);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_InvalidPassword() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "short", // Contraseña inválida
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());

        // Act
        ResponseEntity<Response> response = usuarioService.crearUsuario(usuario);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals(
                "La contraseña debe tener entre 8 y 15 caracteres, incluyendo una letra mayúscula, una minúscula, un número y un carácter especial.",
                response.getBody().getError());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testLogin_Exception() throws Exception {
        // Arrange
        when(usuarioRepository.findByCorreo(anyString())).thenThrow(new RuntimeException("Error interno"));

        // Act
        ResponseEntity<Response> response = usuarioService.login("test@example.com", "Password1!");

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals("Error interno", response.getBody().getError());
    }

    @Test
    void testActualizarRol_InvalidRoleSpecific() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarRol(1L, "invalidRole123");

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals("Rol no valido", response.getBody().getError());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testCrearUsuario_ValidationError() throws Exception {
        // Arrange
        Usuario usuario = new Usuario(
                "", // Correo vacío
                "short", // Contraseña no válida
                "Nom",
                "Ape",
                "invalidRole",
                "Short",
                "123",
                LocalDate.now());

        // Simular violaciones de validación
        Set<ConstraintViolation<Usuario>> violations = Set.of(
                mockConstraintViolation("correo", "no puede estar vacío"),
                mockConstraintViolation("contrasena", "no cumple los criterios de complejidad"));

        when(validator.validate(any(Usuario.class))).thenReturn(violations);

        // Act
        ResponseEntity<Response> response = usuarioService.crearUsuario(usuario);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertTrue(response.getBody().getError().contains("correo"));
        assertTrue(response.getBody().getError().contains("contrasena"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    private ConstraintViolation<Usuario> mockConstraintViolation(String property, String message) {
        ConstraintViolation<Usuario> violation = mock(ConstraintViolation.class);

        // Crear un mock para Path
        Path mockPath = mock(Path.class);
        when(mockPath.toString()).thenReturn(property);

        // Configurar el mock de ConstraintViolation
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(violation.getMessage()).thenReturn(message);

        return violation;
    }

    @Test
    void testIsValidPassword() {
        // Valid passwords
        assertTrue(usuarioService.isValidPassword("Password1!"));
        assertTrue(usuarioService.isValidPassword("Valid123#"));

        // Invalid passwords
        assertFalse(usuarioService.isValidPassword("short1!"));
        assertFalse(usuarioService.isValidPassword("noSpecialChar123"));
        assertFalse(usuarioService.isValidPassword("NOSPECIALCHARS!"));
        assertFalse(usuarioService.isValidPassword("12345678!"));
    }

    @Test
    void testIsValidRole() {
        // Valid roles
        assertTrue(usuarioService.isValidRole("admin"));
        assertTrue(usuarioService.isValidRole("cliente"));

        // Invalid roles
        assertFalse(usuarioService.isValidRole("user"));
        assertFalse(usuarioService.isValidRole("adm"));
        assertFalse(usuarioService.isValidRole("cli"));
    }

    @Test
    void testActualizarUsuario_Success() {
        // Arrange
        Usuario existingUsuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());

        Usuario updatedUsuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NuevoNombre",
                "NuevoApellido",
                "admin",
                "Nueva Direccion",
                "123456789",
                LocalDate.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));
        when(validator.validate(any(Usuario.class))).thenReturn(Set.of());
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(updatedUsuario);

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarUsuario(1L, updatedUsuario);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getBody().getState());
        assertEquals("NuevoNombre", ((Usuario) response.getBody().getRes()).getNombre());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_UserNotFound() {
        // Arrange
        Usuario updatedUsuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NuevoNombre",
                "NuevoApellido",
                "admin",
                "Nueva Direccion",
                "123456789",
                LocalDate.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarUsuario(1L, updatedUsuario);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals("Usuario no encontrado", response.getBody().getError());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_ValidationError() {
        // Arrange
        Usuario existingUsuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NombreUsuario",
                "ApellidoUsuario",
                "admin",
                "Direccion completa de usuario",
                "987654321",
                LocalDate.now());

        Usuario updatedUsuario = new Usuario(
                "test@example.com",
                "Password1!",
                "",
                "",
                "admin",
                "Short",
                "123",
                LocalDate.now());

        // Simular violaciones de validación
        Set<ConstraintViolation<Usuario>> violations = Set.of(
                mockConstraintViolation("nombre", "no puede estar vacío"),
                mockConstraintViolation("apellido", "no puede estar vacío"));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existingUsuario));
        when(validator.validate(any(Usuario.class))).thenReturn((Set) violations);

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarUsuario(1L, updatedUsuario);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertTrue(response.getBody().getError().contains("nombre: no puede estar vacío"));
        assertTrue(response.getBody().getError().contains("apellido: no puede estar vacío"));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testActualizarUsuario_Exception() {
        // Arrange
        Usuario updatedUsuario = new Usuario(
                "test@example.com",
                "Password1!",
                "NuevoNombre",
                "NuevoApellido",
                "admin",
                "Nueva Direccion",
                "123456789",
                LocalDate.now());

        when(usuarioRepository.findById(1L)).thenThrow(new RuntimeException("Error interno"));

        // Act
        ResponseEntity<Response> response = usuarioService.actualizarUsuario(1L, updatedUsuario);

        // Assert
        assertNotNull(response);
        assertEquals("error", response.getBody().getState());
        assertEquals("Error interno", response.getBody().getError());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

}
