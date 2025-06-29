package gestor.feedlotapp.Repository;

import gestor.feedlotapp.entities.RegistroTratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;
import java.util.List;

public interface RegistroTratamientoRepository extends JpaRepository<RegistroTratamiento, Integer> {

    // Obtener todos los tratamientos de un animal
    List<RegistroTratamiento> findByAnimal_AnimalId(Integer animalId);

    // Buscar registro por caravana
    List<RegistroTratamiento> findByAnimalCaravana(String caravana);

    // Filtrar tratamientos por rango de fechas
    List<RegistroTratamiento> findByFechaBetween(Date desde, Date hasta);

    // Tratamientos de un animal en rango de fechas
    List<RegistroTratamiento> findByAnimal_AnimalIdAndFechaBetweenOrderByFechaAsc(Integer animalId, Date desde, Date hasta);
}
