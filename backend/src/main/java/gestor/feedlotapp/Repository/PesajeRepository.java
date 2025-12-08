package gestor.feedlotapp.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import gestor.feedlotapp.entities.Pesaje;

public interface PesajeRepository extends JpaRepository<Pesaje, Long> {
    List<Pesaje> findByAnimal_AnimalId(Long animalId);
    List<Pesaje> findByAnimalAnimalIdOrderByFechaAsc(Long animalId);
    List<Pesaje> findByAnimal_AnimalIdOrderByFechaAsc(Long animalId);
    List<Pesaje> findByAnimalCaravanaOrderByFechaAsc(String caravana);

    List<Pesaje> findByFechaBetween(Date desde, Date hasta);

}
