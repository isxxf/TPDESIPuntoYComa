package PuntoYComa.servicios;

import java.time.LocalDate;
import java.util.List;

import PuntoYComa.entidades.Contrato;
import PuntoYComa.entidades.EstadoContrato;
import PuntoYComa.entidades.Persona;
import PuntoYComa.entidades.Propiedad;

public interface ContratoService {

    Contrato guardar(Contrato contrato);

    Contrato buscarPorId(Long id);

    List<Contrato> listarNoEliminados();

    List<Contrato> buscarPorEstado(EstadoContrato estadoContrato);

    List<Contrato> buscarPorPropiedad(Propiedad propiedad);

    List<Contrato> buscarPorInquilino(Persona inquilino);

    List<Contrato> buscarPorFechaInicio(LocalDate fechaInicio);

    void eliminarLogicamente(Long id);

    boolean existeContratoActivoParaPropiedad(Propiedad propiedad);
}