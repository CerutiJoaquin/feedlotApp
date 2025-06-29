package gestor.feedlotapp.Repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import gestor.feedlotapp.entities.Pesaje;

public interface PesajeRepository extends JpaRepository<Pesaje, Integer> {
    List<Pesaje> findByAnimal_AnimalId(int animalId);

    // Historial completo de pesajes de un animal
    List<Pesaje> findByAnimalAnimalIdOrderByFechaAsc(Integer animalId);

    // Trazabilidad por caravana
    List<Pesaje> findByAnimalCaravanaOrderByFechaAsc(String caravana);

    // Filtrar pesajes por rango de fechas
    List<Pesaje> findByFechaBetween(Date desde, Date hasta);
}
