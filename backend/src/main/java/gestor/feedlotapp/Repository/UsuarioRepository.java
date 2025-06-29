package gestor.feedlotapp.Repository;

import gestor.feedlotapp.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
