package gestor.feedlotapp.repository;

import gestor.feedlotapp.dto.venta.VentaListDto;
import gestor.feedlotapp.entities.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Integer> {
    @EntityGraph(attributePaths = {"detalles", "detalles.animal", "cliente"})
    Optional<Venta> findWithDetallesByVentaId(Long ventaId);

    @Query(value = """
        select v.venta_id            as ventaId,
               v.fecha               as fecha,
               v.total               as total,
               coalesce(c.nombre || ' ' || c.apellido, 'Sin cliente') as cliente,
               coalesce(string_agg(a.caravana, ', ' order by a.caravana), '') as caravanas
        from venta v
        left join cliente c      on c.cliente_id = v.cliente_id
        left join venta_detalle vd on vd.venta_id = v.venta_id
        left join animal a       on a.animal_id = vd.animal_id
        group by v.venta_id, v.fecha, v.total, cliente
        order by v.fecha desc, v.venta_id desc
        """, nativeQuery = true)
    List<VentaListDto> listar();

    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.animal"})
    List<Venta> findAllByOrderByFechaDescVentaIdDesc();

    @Query(value = """
        SELECT 
            v.venta_id                                               AS venta_id,
            v.fecha                                                  AS fecha,
            v.total                                                  AS total,
            COALESCE(c.nombre || ' ' || c.apellido, 'Sin cliente')   AS cliente,
            STRING_AGG(a.caravana, ', ' ORDER BY a.caravana)         AS caravanas
        FROM venta v
        LEFT JOIN cliente c        ON c.cliente_id = v.cliente_id
        LEFT JOIN venta_detalle vd ON vd.venta_id = v.venta_id
        LEFT JOIN animal a         ON a.animal_id = vd.animal_id
        WHERE (:clienteId IS NULL OR v.cliente_id = :clienteId)
        GROUP BY v.venta_id, v.fecha, v.total, cliente
        ORDER BY v.fecha DESC, v.venta_id DESC
        """, nativeQuery = true)
    List<Object[]> historialRaw(@Param("clienteId") Long clienteId);

}
