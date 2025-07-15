package gestor.feedlotapp.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gestor.feedlotapp.entities.Animal;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Integer> {



    // Buscar animal por caravana exacta o parcial
    Optional<Animal> getByCaravana(String caravana);
    List<Animal> findByCaravanaContainingIgnoreCase(String fragmentoCaravana);

    // Filtrar animales por corral, raza, salud o sexo
    List<Animal> getByCorral_CorralId(Integer corralId);
    List<Animal> getByRaza(String raza);
    List<Animal> getByEstadoSalud(String estadoSalud);
    List<Animal> getBySexo(boolean sexo);

    // Validaciones y contadores
    boolean existsByCaravana(String carvana);
    long countByCorral_CorralId(Integer corralId);
    void deleteByCaravana(String caravana);

    @EntityGraph(attributePaths = {
    "pesajes",
    "tratamientos",
    "tratamientos.insumo"
  })
  Optional<Animal> findWithHistoryByCaravana(String caravana);
}
