package PuntoYComa.accesoDatos;

import PuntoYComa.entidades.EstadoPublicacion;
import PuntoYComa.entidades.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PublicacionRepository extends JpaRepository<Publicacion, Long> {
    boolean existsByPropiedadAndEstadoAndEliminadaFalse(Propiedad propiedad, EstadoPublicacion estado);

    @Query("SELECT p FROM Publicacion p WHERE p.eliminada = false " +
            "AND (:propiedadId IS NULL OR p.propiedad.id = :propiedadId)" +
            "AND (:ciudad IS NULL OR p.propiedad.ciudad.nombre = :ciudad)" +
            "AND (:estado IS NULL OR p.estado = :estado)" +
            "AND (:precioMin IS NULL OR p.precioMensual >= :precioMin)" +
            "AND (:precioMax IS NULL OR p.precioMensual <= :precioMax)")
    List<Publicacion> filtrarPublicaciones(
            @Param("propiedadId") Long propiedadId,
            @Param("ciudad") String ciudad,
            @Param("estado") EstadoPublicacion estado,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax
    );
}
