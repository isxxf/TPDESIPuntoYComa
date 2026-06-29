package PuntoYComa.servicios;

import java.time.LocalDate;
import java.util.List;
import PuntoYComa.entidades.EstadoFactura;
import PuntoYComa.entidades.Factura;

public interface FacturaService {

    Factura guardar(Factura factura);

    Factura buscarPorId(Long id);

    List<Factura> listarNoEliminadas();

    List<Factura> buscarConFiltros(
            EstadoFactura estado,
            Long contratoId,
            Long propiedadId,
            Long inquilinoId,
            LocalDate fechaDesde,
            LocalDate fechaHasta
    );

    void eliminarLogicamente(Long id);
}