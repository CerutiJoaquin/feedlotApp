package gestor.feedlotapp.service;

import gestor.feedlotapp.Repository.UsuarioRepository;
import gestor.feedlotapp.entities.Usuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UsuarioRepository repo;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder =
            new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    public AuthService(UsuarioRepository repo){this.repo = repo; }

    public Usuario register(String nombre, String apellido, String email, String rawPass){
        if(repo.existsByEmail(email)) throw new IllegalArgumentException("Email ya registrado");
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setContrasenia(encoder.encode(rawPass)); //HASH
        return repo.save(u);
    }

    public Optional<Usuario> login(String email, String rawPass){
        return repo.findByEmail(email)
                .filter(u -> encoder.matches(rawPass, u.getContrasenia()));
    }

    @Bean
    CommandLineRunner updateAdminPass(UsuarioRepository repo) {
        return args -> {
            repo.findByEmail("admin@feedlot.com").ifPresent(u -> {
                u.setContrasenia(new BCryptPasswordEncoder().encode("Admin123456!"));
                repo.save(u);
            });
        };
    }
}
