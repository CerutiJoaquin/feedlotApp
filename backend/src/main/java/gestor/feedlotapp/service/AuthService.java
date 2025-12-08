package gestor.feedlotapp.service;

import gestor.feedlotapp.repository.UsuarioRepository;
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
        u.setContrasenia(encoder.encode(rawPass));
        return repo.save(u);
    }

    public Optional<Usuario> login(String email, String rawPass){
        return repo.findByEmail(email)
                .filter(u -> encoder.matches(rawPass, u.getContrasenia()));
    }

    //  Crear seed contraseña
//    @Bean
//    CommandLineRunner seedAdmin(AuthService auth, UsuarioRepository repo){
//        return args -> {
//            String adminEmail = "admin@feedlot.com";
//            if (!repo.existsByEmail(adminEmail)) {
//                auth.register("Admin","", adminEmail, "Admin123456!");
//            }
//        };
//    }

    //    Modificar seed contraseña
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
