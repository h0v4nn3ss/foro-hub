package com.forohub.controller;

import com.forohub.domain.usuario.Usuario;
import com.forohub.infra.security.TokenService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AutenticacionController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<DatosJWTToken> login(@RequestBody @Valid DatosAutenticacionUsuario datos) {
        var token = new UsernamePasswordAuthenticationToken(datos.login(), datos.clave());
        var authentication = authenticationManager.authenticate(token);
        var usuario = (Usuario) authentication.getPrincipal();
        var jwtToken = tokenService.generarToken(usuario);
        return ResponseEntity.ok(new DatosJWTToken(jwtToken));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> manejarCredencialesInvalidas() {
        return ResponseEntity.status(401).body(Map.of("error", "Credenciales invalidas"));
    }
}
