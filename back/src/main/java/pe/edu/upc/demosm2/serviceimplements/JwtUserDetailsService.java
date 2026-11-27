package pe.edu.upc.demosm2.serviceimplements;

import org.springframework.security.core.userdetails.User;
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
 * Busca la cuenta por correo. El principal autenticado usa el ID estable de la persona.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    private IPersonaRepositories repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Persona persona = repo.buscarPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Persona no encontrada: " + email));

        if (persona.getPasswordPersona() == null || persona.getPasswordPersona().isBlank()) {
            throw new UsernameNotFoundException("La persona " + email + " no tiene contraseña registrada");
        }

        List<GrantedAuthority> roles = new ArrayList<>();
        if (persona.getRol() != null && persona.getRol().getDetalle() != null) {
            String rol = persona.getRol().getDetalle().trim().toUpperCase().replace(' ', '_');
            roles.add(new SimpleGrantedAuthority(rol.startsWith("ROLE_") ? rol : "ROLE_" + rol));
        }

        boolean isEnabled = persona.getEstadoPersona() != null
                && persona.getEstadoPersona().equalsIgnoreCase("ACTIVO");

        return new User(
                persona.getIdPersona().toString(),
                persona.getPasswordPersona(),
                isEnabled, true, true, true, roles
        );
    }
}
