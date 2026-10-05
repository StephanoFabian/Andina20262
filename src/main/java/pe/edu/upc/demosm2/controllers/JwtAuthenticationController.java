package pe.edu.upc.demosm2.controllers;

import pe.edu.upc.demosm2.dtos.JwtRequestDTO;
import pe.edu.upc.demosm2.dtos.JwtResponseDTO;
import pe.edu.upc.demosm2.securities.JwtTokenService;
import pe.edu.upc.demosm2.serviceimplements.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    // Login: ID de la persona + contraseña -> token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequestDTO req) {
        if (req.getIdPersona() == null || req.getPasswordPersona() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Envía idPersona y passwordPersona");
        }
        String username = String.valueOf(req.getIdPersona());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, req.getPasswordPersona()));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La persona no está ACTIVA");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ID de persona o contraseña incorrectos");
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String token = jwtTokenService.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }
}
