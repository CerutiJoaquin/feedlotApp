package gestor.feedlotapp.venta.repository;

import gestor.feedlotapp.venta.entities.MagPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MagPrecioRepository extends JpaRepository<MagPrecio, Long> {

    Optional<MagPrecio> findByFechaAndCategoria(LocalDate fecha, String categoria);


    boolean existsByFechaAndCategoria(LocalDate fecha, String categoria);

    List<MagPrecio> findAllByFecha(LocalDate fecha);

    List<MagPrecio> findAllByFechaBetween(LocalDate desde, LocalDate hasta);

    long deleteByFecha(LocalDate fecha);

    @Query("""
    select p.precioProm from MagPrecio p
    where p.categoria = :categoria and p.fecha = :fecha
    order by p.id desc
  """)
    Optional<BigDecimal> precioDeFecha(@Param("categoria") String categoria,
                                       @Param("fecha") LocalDate fecha);

    Optional<MagPrecio> findTopByCategoriaOrderByFechaDescIdDesc(String categoria);


    Optional<MagPrecio> findTopByCategoriaAndFechaLessThanEqualOrderByFechaDescIdDesc(
            String categoria, LocalDate fecha);

    @Query("select max(m.fecha) from MagPrecio m where m.fecha <= :f")
    Optional<LocalDate> maxFechaHasta(LocalDate f);

    Optional<MagPrecio> findByFuenteAndFechaAndCategoria(String fuente,
                                                         LocalDate fecha,
                                                         String categoria);
}