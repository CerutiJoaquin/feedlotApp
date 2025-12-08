package gestor.feedlotapp.repository;

import gestor.feedlotapp.entities.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {

    @Query("select vd.animal.animalId from VentaDetalle vd where vd.animal.animalId in :ids")
    List<Long> findAnimalIdsVendidos(@Param("ids") List<Long> ids);


    @Query("select (count(vd) > 0) from VentaDetalle vd where vd.animal.animalId in :ids")
    boolean existsAnyByAnimalIds(@Param("ids") List<Long> ids);

    boolean existsByAnimal_AnimalId(Long animalId);


}
