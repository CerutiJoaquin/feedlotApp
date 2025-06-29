package gestor.feedlotapp.Repository;

import gestor.feedlotapp.entities.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
}
