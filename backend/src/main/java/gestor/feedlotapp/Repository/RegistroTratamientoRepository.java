package gestor.feedlotapp.repository;

import gestor.feedlotapp.entities.RegistroTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RegistroTratamientoRepository extends JpaRepository<RegistroTratamiento, Integer> {

    List<RegistroTratamiento> findByAnimal_AnimalId(Long animalId);


    List<RegistroTratamiento> findByAnimal_Caravana(String caravana);

    List<RegistroTratamiento> findByFechaBetween(LocalDate desde, LocalDate hasta);

    List<RegistroTratamiento> findByAnimal_AnimalIdAndFechaBetweenOrderByFechaAsc(
            Long animalId, LocalDate desde, LocalDate hasta
    );
}
