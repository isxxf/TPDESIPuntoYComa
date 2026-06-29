package PuntoYComa.accesoDatos;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import PuntoYComa.entidades.EstadoFactura;
import PuntoYComa.entidades.Factura;

public interface FacturaRepository extends JpaRepository<Factura, Long> {

    List<Factura> findByEliminadaFalse();

    @Query("""
            SELECT f FROM Factura f
            WHERE f.eliminada = false
              AND (:estado IS NULL OR f.estado = :estado)
              AND (:contratoId IS NULL OR f.contrato.id = :contratoId)
              AND (:propiedadId IS NULL OR f.contrato.propiedad.id = :propiedadId)
              AND (:inquilinoId IS NULL OR f.contrato.inquilino.id = :inquilinoId)
              AND (:fechaDesde IS NULL OR f.fechaVencimiento >= :fechaDesde)
              AND (:fechaHasta IS NULL OR f.fechaVencimiento <= :fechaHasta)
            """)
    List<Factura> buscarConFiltros(
            @Param("estado") EstadoFactura estado,
            @Param("contratoId") Long contratoId,
            @Param("propiedadId") Long propiedadId,
            @Param("inquilinoId") Long inquilinoId,
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta
    );
}