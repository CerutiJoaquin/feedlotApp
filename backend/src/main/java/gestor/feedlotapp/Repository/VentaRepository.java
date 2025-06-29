package gestor.feedlotapp.Repository;

import gestor.feedlotapp.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
}
