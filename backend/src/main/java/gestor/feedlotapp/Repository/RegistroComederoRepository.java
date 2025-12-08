package gestor.feedlotapp.repository;

import gestor.feedlotapp.entities.RegistroComedero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroComederoRepository extends JpaRepository<RegistroComedero, Integer> {

    List<RegistroComedero> findByCorral_CorralIdAndFechaBetweenOrderByFechaAsc(
            Integer corralId, LocalDate desde, LocalDate hasta);

    List<RegistroComedero> findByFechaBetweenOrderByFechaAsc(LocalDate desde, LocalDate hasta);
}
