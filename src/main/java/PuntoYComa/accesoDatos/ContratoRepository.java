package PuntoYComa.accesoDatos;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import PuntoYComa.entidades.Contrato;
import PuntoYComa.entidades.EstadoContrato;
import PuntoYComa.entidades.Persona;
import PuntoYComa.entidades.Propiedad;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {

    List<Contrato> findByEliminadoFalse();

    List<Contrato> findByEstadoContratoAndEliminadoFalse(
            EstadoContrato estadoContrato
    );

    List<Contrato> findByPropiedadAndEliminadoFalse(
            Propiedad propiedad
    );

    List<Contrato> findByInquilinoAndEliminadoFalse(
            Persona inquilino
    );

    List<Contrato> findByFechaInicioAndEliminadoFalse(
            LocalDate fechaInicio
    );

    boolean existsByPropiedadAndEstadoContratoAndEliminadoFalse(
            Propiedad propiedad,
            EstadoContrato estadoContrato
    );
}