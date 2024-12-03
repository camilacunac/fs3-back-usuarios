package com.example.controller;

import com.example.model.Response;
import com.example.model.Usuario;
import com.example.service.UsuarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

class UsuarioControllerTest {

        private MockMvc mockMvc;

        @InjectMocks
        private UsuarioController usuarioController;

        @Mock
        private UsuarioService usuarioService;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
                mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        }

        @Test
        void testGetAllUsuarios() throws Exception {
                // Arrange
                when(usuarioService.getAllUsuarios()).thenReturn(List.of(new Usuario()));

                // Act & Assert
                mockMvc.perform(get("/usuarios"))
                                .andExpect(status().isOk());
                verify(usuarioService, times(1)).getAllUsuarios();
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
                Response response = new Response("success", usuario, "");

                when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(ResponseEntity.ok(response));

                // Act & Assert
                mockMvc.perform(post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "correo": "test@example.com",
                                                    "contrasena": "Password1!",
                                                    "nombre": "NombreUsuario",
                                                    "apellido": "ApellidoUsuario",
                                                    "rol": "admin",
                                                    "direccion": "Direccion completa de usuario",
                                                    "telefono": "987654321"
                                                }
                                                """))
                                .andExpect(status().isOk());
                verify(usuarioService, times(1)).crearUsuario(any(Usuario.class));
        }

        @Test
        void testEliminarUsuario_UserNotFound() throws Exception {
                // Arrange
                when(usuarioService.eliminarUsuario(1L)).thenReturn(ResponseEntity.status(404)
                                .body(new Response("error", null, "Usuario no encontrado")));

                // Act & Assert
                mockMvc.perform(delete("/usuarios/1"))
                                .andExpect(status().isNotFound());
                verify(usuarioService, times(1)).eliminarUsuario(1L);
        }

        @Test
        void testActualizarRol_InvalidRole() throws Exception {
                // Arrange
                when(usuarioService.actualizarRol(1L, "invalidRole")).thenReturn(ResponseEntity.status(400)
                                .body(new Response("error", null, "Rol no valido")));

                // Act & Assert
                mockMvc.perform(put("/usuarios/1/rol")
                                .param("nuevoRol", "invalidRole"))
                                .andExpect(status().isBadRequest());
                verify(usuarioService, times(1)).actualizarRol(1L, "invalidRole");
        }

        @Test
        void testLogin_Failure() throws Exception {
                // Arrange
                when(usuarioService.login("wrongemail@example.com", "Password1!")).thenReturn(ResponseEntity.status(400)
                                .body(new Response("error", null, "Usuario no encontrado")));

                // Act & Assert
                mockMvc.perform(post("/usuarios/login")
                                .contentType("application/json")
                                .content("{\"correo\":\"wrongemail@example.com\",\"contrasena\":\"Password1!\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.state").value("error"))
                                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
                verify(usuarioService, times(1)).login("wrongemail@example.com", "Password1!");
        }

        @Test
        void testGetAllUsuarios_EmptyList() throws Exception {
                // Arrange
                when(usuarioService.getAllUsuarios()).thenReturn(List.of());

                // Act & Assert
                mockMvc.perform(get("/usuarios"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("[]")); // Verifica que la respuesta sea una lista vacía
                verify(usuarioService, times(1)).getAllUsuarios();
        }

        @Test
        void testLogin_Exception() throws Exception {
                // Arrange
                when(usuarioService.login(anyString(), anyString())).thenThrow(new RuntimeException("Error interno"));

                // Act & Assert
                mockMvc.perform(post("/usuarios/login")
                                .contentType("application/json")
                                .content("{\"correo\":\"test@example.com\",\"contrasena\":\"Password1!\"}"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.state").value("error"))
                                .andExpect(jsonPath("$.error").value("Error interno durante el inicio de sesión"));
                verify(usuarioService, times(1)).login(anyString(), anyString());
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
                Response response = new Response("success", usuario, "");

                when(usuarioService.actualizarRol(1L, "cliente")).thenReturn(ResponseEntity.ok(response));

                // Act & Assert
                mockMvc.perform(put("/usuarios/1/rol")
                                .param("nuevoRol", "cliente"))
                                .andExpect(status().isOk());
                verify(usuarioService, times(1)).actualizarRol(1L, "cliente");
        }

        @Test
        void testCrearUsuario_Exception() throws Exception {
                // Arrange
                when(usuarioService.crearUsuario(any(Usuario.class))).thenThrow(new RuntimeException("Error interno"));

                // Act & Assert
                mockMvc.perform(post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "correo": "test@example.com",
                                                    "contrasena": "Password1!",
                                                    "nombre": "NombreUsuario",
                                                    "apellido": "ApellidoUsuario",
                                                    "rol": "admin",
                                                    "direccion": "Direccion completa de usuario",
                                                    "telefono": "987654321"
                                                }
                                                """))
                                .andExpect(status().isInternalServerError());
                verify(usuarioService, times(1)).crearUsuario(any(Usuario.class));
        }

        @Test
        void testCrearUsuario_MissingFields() throws Exception {
                // Act & Assert
                mockMvc.perform(post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "correo": "",
                                                    "contrasena": "",
                                                    "nombre": "",
                                                    "apellido": "",
                                                    "rol": "",
                                                    "direccion": "",
                                                    "telefono": ""
                                                }
                                                """))
                                .andExpect(status().isBadRequest());
                verify(usuarioService, never()).crearUsuario(any(Usuario.class));
        }

        @Test
        void testActualizarRol_Exception() throws Exception {
                // Arrange
                when(usuarioService.actualizarRol(1L, "admin")).thenThrow(new RuntimeException("Error interno"));

                // Act & Assert
                mockMvc.perform(put("/usuarios/1/rol")
                                .param("nuevoRol", "admin"))
                                .andExpect(status().isInternalServerError());
                verify(usuarioService, times(1)).actualizarRol(1L, "admin");
        }

        @Test
        void testEliminarUsuario_Exception() throws Exception {
                // Arrange
                when(usuarioService.eliminarUsuario(1L)).thenThrow(new RuntimeException("Error interno"));

                // Act & Assert
                mockMvc.perform(delete("/usuarios/1"))
                                .andExpect(status().isInternalServerError());
                verify(usuarioService, times(1)).eliminarUsuario(1L);
        }

        @Test
        void testCrearUsuario_InvalidJson() throws Exception {
                // Act & Assert
                mockMvc.perform(post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}"))
                                .andExpect(status().isBadRequest());
                verify(usuarioService, never()).crearUsuario(any(Usuario.class));
        }

        @Test
        void testCrearUsuario_BadRequestMalformedJson() throws Exception {
                // Act & Assert
                mockMvc.perform(post("/usuarios/registro")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{correo: 'test@example.com'"))
                                .andExpect(status().isBadRequest());
                verify(usuarioService, never()).crearUsuario(any(Usuario.class));
        }

        @Test
        void testEliminarUsuario_Success() throws Exception {
                // Arrange
                Response response = new Response("success", "Usuario eliminado con exito", "");
                when(usuarioService.eliminarUsuario(1L)).thenReturn(ResponseEntity.ok(response));

                // Act & Assert
                mockMvc.perform(delete("/usuarios/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.state").value("success"))
                                .andExpect(jsonPath("$.res").value("Usuario eliminado con exito"));
                verify(usuarioService, times(1)).eliminarUsuario(1L);
        }

        @Test
        void testLogin_IncorrectPassword() throws Exception {
                // Arrange
                Response response = new Response("error", null, "Contraseña incorrecta");
                when(usuarioService.login(anyString(), anyString()))
                                .thenReturn(ResponseEntity.badRequest().body(response));

                // Act & Assert
                mockMvc.perform(post("/usuarios/login")
                                .contentType("application/json")
                                .content("{\"correo\":\"test@example.com\",\"contrasena\":\"incorrectPassword\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.state").value("error"))
                                .andExpect(jsonPath("$.error").value("Contraseña incorrecta"));
                verify(usuarioService, times(1)).login(anyString(), anyString());
        }

        @Test
        void testGetAllUsuarios_WithResults() throws Exception {
                // Arrange
                Usuario usuario = new Usuario("test@example.com", "Password1!", "NombreUsuario", "ApellidoUsuario",
                                "admin",
                                "Direccion completa de usuario", "987654321", LocalDate.now());
                when(usuarioService.getAllUsuarios()).thenReturn(List.of(usuario));

                // Act & Assert
                mockMvc.perform(get("/usuarios"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].correo").value("test@example.com"))
                                .andExpect(jsonPath("$[0].nombre").value("NombreUsuario"));
                verify(usuarioService, times(1)).getAllUsuarios();
        }

        @Test
        void testActualizarUsuario_NotFound() throws Exception {
                // Arrange
                when(usuarioService.actualizarUsuario(eq(1L), any())).thenReturn(
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new Response("error", null, "Usuario no encontrado")));

                // Act & Assert
                mockMvc.perform(put("/usuarios/actualizar/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Updated Name\"}"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
        }

        @Test
        void testActualizarUsuario_ValidationError() throws Exception {
                // Arrange
                when(usuarioService.actualizarUsuario(eq(1L), any())).thenReturn(
                                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                                .body(new Response("error", null, "Validation Error")));

                // Act & Assert
                mockMvc.perform(put("/usuarios/actualizar/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Invalid Name\"}"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Validation Error"));
        }

        @Test
        void testActualizarUsuario_Success() throws Exception {
                // Arrange
                Usuario updatedUsuario = new Usuario();
                updatedUsuario.setNombre("Updated Name");
                Response response = new Response("success", updatedUsuario, "Usuario actualizado exitosamente");

                when(usuarioService.actualizarUsuario(eq(1L), any())).thenReturn(ResponseEntity.ok(response));

                // Act & Assert
                mockMvc.perform(put("/usuarios/actualizar/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Updated Name\"}"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.state").value("success"))
                                .andExpect(jsonPath("$.error").value("Usuario actualizado exitosamente"));
        }

        @Test
        void testActualizarUsuario_Exception() throws Exception {
                // Arrange
                when(usuarioService.actualizarUsuario(eq(1L), any()))
                                .thenThrow(new RuntimeException("Unexpected error"));

                // Act & Assert
                mockMvc.perform(put("/usuarios/actualizar/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"nombre\":\"Updated Name\"}"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.error").value("Error interno al actualizar el usuario"));
        }

}
