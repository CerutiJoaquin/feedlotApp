package gestor.feedlotapp.Repository;

import gestor.feedlotapp.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {


}
