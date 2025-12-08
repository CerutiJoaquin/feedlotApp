package gestor.feedlotapp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import gestor.feedlotapp.enums.animal.EstadoAnimal;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import gestor.feedlotapp.entities.Animal;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    Optional<Animal> findByCaravana(String caravana);
    List<Animal> findByCaravanaContainingIgnoreCase(String fragmentoCaravana);

    List<Animal> getByCorral_CorralId(Integer corralId);
    List<Animal> getByRaza(String raza);
    List<Animal> getByEstadoSalud(String estadoSalud);
    List<Animal> getBySexo(boolean sexo);

    long countByCorral_CorralId(Integer corralId);


    boolean existsByCaravana(String caravana);
    void deleteByCaravana(String caravana);

    List<Animal> findByAnimalIdIn(List<Long> ids);
    List<Animal> findByCaravanaIn(Collection<String> caravanas);


    @EntityGraph(attributePaths = {"pesajes","tratamientos","tratamientos.insumo"})
    Optional<Animal> findWithHistoryByCaravana(String caravana);

    long countByCorral_CorralIdAndEstado(Integer corralId, EstadoAnimal estado);
    Optional<Animal> findByAnimalIdAndEstado(Long animalId, EstadoAnimal estado);

    Optional<Animal> findByCaravanaAndEstado(String caravana, EstadoAnimal estado);


    List<Animal> findByEstado(EstadoAnimal estado);

    @Query("select a from Animal a where a.estado = 'ACTIVO'")
    List<Animal> findActivos();

    @Query("select a from Animal a where a.estado = 'ACTIVO' and a.animalId in :ids")
    List<Animal> findActivosByAnimalIdIn(@Param("ids") List<Long> ids);

    @Query("select a from Animal a where a.estado = 'ACTIVO' and a.caravana in :caravanas")
    List<Animal> findActivosByCaravanaIn(@Param("caravanas") List<String> caravanas);

    @Query("""
  select a from Animal a
  where a.ventaDetalleBaja is null
    and lower(a.caravana) like lower(concat('%', :frag, '%'))
""")
    List<Animal> findActivosByCaravanaLike(@Param("frag") String frag);

    @Query("select a.animalId from Animal a where a.animalId in :ids and a.fechaBaja is not null")
    List<Long> findIdsConBaja(@org.springframework.data.repository.query.Param("ids") List<Long> ids);

}
