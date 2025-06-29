package gestor.feedlotapp.Repository;

import gestor.feedlotapp.entities.PlanillaTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

public interface PlanillaTratamientoRepository extends JpaRepository<PlanillaTratamiento, Integer> {

    // Obtener la planilla de un dia
    Optional<PlanillaTratamiento> findByFecha(Date fecha);

    // Listar planillas en un rango de fechas
    List<PlanillaTratamiento> findByFechaBetween(Date desde, Date hasta);
}
