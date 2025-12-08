package gestor.feedlotapp.repository;

import gestor.feedlotapp.entities.Insumo;
import gestor.feedlotapp.enums.insumo.Categoria;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

    @Query("""
        select i from Insumo i
        where i.cantidad <= i.cantidadMinima
    """)
    List<Insumo> findAllBelowMin();

    @Query("""
        select i from Insumo i
        where i.tipo = :tipo and i.cantidad <= i.cantidadMinima
    """)
    List<Insumo> findBelowMinByTipo(@Param("tipo") String tipo);

    List<Insumo> findByCategoria(Categoria categoria);

    Page<Insumo> findByCategoriaAndNombreContainingIgnoreCase(
            Categoria categoria, String nombre, Pageable pageable);

    Page<Insumo> findByCategoriaAndCodigoContainingIgnoreCase(
            Categoria categoria, String codigo, Pageable pageable);

    Optional<Insumo> findByCodigoIgnoreCase(String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Insumo i where i.insumoId = :id")
    Optional<Insumo> findByIdForUpdate(@Param("id") Integer id);

    List<Insumo> findByCategoriaOrderByNombreAsc(Categoria categoria);
}

