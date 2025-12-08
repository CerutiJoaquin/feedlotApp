package gestor.feedlotapp.repository;

import gestor.feedlotapp.entities.PlanillaTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface PlanillaTratamientoRepository extends JpaRepository<PlanillaTratamiento, Integer> {
    Optional<PlanillaTratamiento> findByFecha(Date fecha);
    List<PlanillaTratamiento> findByFechaBetween(Date desde, Date hasta);
}
