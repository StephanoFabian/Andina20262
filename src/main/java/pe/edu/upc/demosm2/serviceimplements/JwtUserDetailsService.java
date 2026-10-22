package pe.edu.upc.demosm2.serviceimplements;

import pe.edu.upc.demosm2.entities.Persona;
import pe.edu.upc.demosm2.repositories.IPersonaRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Login de Persona: el "username" es el ID de la persona (idPersona) y la clave es su passwordPersona (BCrypt).
 * SecurityConfig lo usa en su authenticationProvider para validar el login.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private IPersonaRepositories repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Long idPersona;
        try {
            idPersona = Long.parseLong(username.trim());
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("ID de persona inválido: " + username);
        }
        Persona persona = repo.findById(idPersona)
                .orElseThrow(() -> new UsernameNotFoundException("Persona no encontrada: " + username));
        if (persona.getPasswordPersona() == null || persona.getPasswordPersona().isBlank()) {
            throw new UsernameNotFoundException("La persona " + username + " no tiene contraseña registrada");
        }
        List<GrantedAuthority> roles = new ArrayList<>();
        if (persona.getRol() != null && persona.getRol().getDetalle() != null) {
            String rol = persona.getRol().getDetalle().trim().toUpperCase().replace(' ', '_');
            roles.add(new SimpleGrantedAuthority(rol.startsWith("ROLE_") ? rol : "ROLE_" + rol));
        }
        boolean isEnabled = persona.getEstadoPersona() != null && persona.getEstadoPersona().equalsIgnoreCase("ACTIVO");
        return new org.springframework.security.core.userdetails.User(
                String.valueOf(persona.getIdPersona()),
                persona.getPasswordPersona(),
                isEnabled,
                true,
                true,
                true,
                roles
        );
    }
}
