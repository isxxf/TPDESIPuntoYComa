package PuntoYComa.servicios;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import PuntoYComa.accesoDatos.ContratoRepository;
import PuntoYComa.accesoDatos.FacturaRepository;
import PuntoYComa.entidades.Contrato;
import PuntoYComa.entidades.EstadoContrato;
import PuntoYComa.entidades.EstadoFactura;
import PuntoYComa.entidades.Factura;
import PuntoYComa.excepciones.FacturaInvalidaException;
import PuntoYComa.excepciones.RecursoNoEncontradoException;

@Service
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final ContratoRepository contratoRepository;

    public FacturaServiceImpl(FacturaRepository facturaRepository,
                              ContratoRepository contratoRepository) {
        this.facturaRepository = facturaRepository;
        this.contratoRepository = contratoRepository;
    }

    @Override
    @Transactional
    public Factura guardar(Factura factura) {
        validarDatosObligatorios(factura);

        boolean esNueva = factura.getId() == null;

        if (esNueva) {
            // HU 4.1: si no se eligió estado, el default es PENDIENTE
            if (factura.getEstado() == null) {
                factura.setEstado(EstadoFactura.PENDIENTE);
            }
            validarContratoParaAlta(factura.getContrato());
            // Si el estado elegido es PAGADA, validar datos de pago antes de registrar historial
            if (factura.getEstado() == EstadoFactura.PAGADA) {
                validarDatosPago(factura);
            }
            factura.agregarHistorialEstado(factura.getEstado());
        } else {
            Factura facturaExistente = buscarPorId(factura.getId());

            // HU 4.2: no se puede modificar una factura ANULADA ni PAGADA
            if (facturaExistente.getEstado() == EstadoFactura.ANULADA) {
                throw new FacturaInvalidaException(
                        "No se puede modificar una factura anulada");
            }
            if (facturaExistente.getEstado() == EstadoFactura.PAGADA) {
                throw new FacturaInvalidaException(
                        "No se puede modificar una factura pagada");
            }

            // HU 4.2: el contrato no se puede cambiar
            factura.setContrato(facturaExistente.getContrato());

            // HU 4.2: validar transición de estado
            if (facturaExistente.getEstado() != factura.getEstado()) {
                validarTransicionEstado(facturaExistente.getEstado(), factura.getEstado());
                factura.agregarHistorialEstado(factura.getEstado());
            }

            // Conservar historial previo
            factura.getHistorialEstados().addAll(0, facturaExistente.getHistorialEstados());
        }

        // HU 4.2: si pasa a PAGADA, los datos de pago son obligatorios
        if (factura.getEstado() == EstadoFactura.PAGADA) {
            validarDatosPago(factura);
        } else {
            // Si no es PAGADA, los datos de pago deben estar vacíos
            limpiarDatosPago(factura);
        }

        return facturaRepository.save(factura);
    }

    @Override
    public Factura buscarPorId(Long id) {
        return facturaRepository.findById(id)
                .filter(f -> !Boolean.TRUE.equals(f.getEliminada()))
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró una factura con el identificador " + id));
    }

    @Override
    public List<Factura> listarNoEliminadas() {
        return facturaRepository.findByEliminadaFalse();
    }

    @Override
    public List<Factura> buscarConFiltros(EstadoFactura estado, Long contratoId,
                                          Long propiedadId, Long inquilinoId,
                                          LocalDate fechaDesde, LocalDate fechaHasta) {
        return facturaRepository.buscarConFiltros(
                estado, contratoId, propiedadId, inquilinoId, fechaDesde, fechaHasta);
    }

    @Override
    @Transactional
    public void eliminarLogicamente(Long id) {
        Factura factura = buscarPorId(id);

        // HU 4.3: no se puede eliminar una factura pagada
        if (factura.getEstado() == EstadoFactura.PAGADA) {
            throw new FacturaInvalidaException(
                    "No se puede eliminar una factura pagada");
        }

        factura.setEliminada(true);
        facturaRepository.save(factura);
    }

    // -------------------------------------------------------------------------
    // Métodos privados de validación
    // -------------------------------------------------------------------------

    private void validarDatosObligatorios(Factura factura) {
        if (factura == null) {
            throw new FacturaInvalidaException("La factura no puede ser nula");
        }
        if (factura.getContrato() == null || factura.getContrato().getId() == null) {
            throw new FacturaInvalidaException("El contrato es obligatorio");
        }
        if (factura.getConceptoFacturado() == null
                || factura.getConceptoFacturado().isBlank()) {
            throw new FacturaInvalidaException("El concepto facturado es obligatorio");
        }
        if (factura.getFechaEmision() == null) {
            throw new FacturaInvalidaException("La fecha de emisión es obligatoria");
        }
        if (factura.getFechaVencimiento() == null) {
            throw new FacturaInvalidaException("La fecha de vencimiento es obligatoria");
        }
        if (factura.getFechaVencimiento().isBefore(factura.getFechaEmision())) {
            throw new FacturaInvalidaException(
                    "La fecha de vencimiento debe ser igual o posterior a la fecha de emisión");
        }
        if (factura.getImporte() == null || factura.getImporte().signum() <= 0) {
            throw new FacturaInvalidaException(
                    "El importe debe ser un número positivo");
        }
    }

    private void validarContratoParaAlta(Contrato contrato) {
        Contrato contratoReal = contratoRepository.findById(contrato.getId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "No se encontró el contrato con id " + contrato.getId()));

        if (Boolean.TRUE.equals(contratoReal.getEliminado())) {
            throw new FacturaInvalidaException(
                    "No se puede crear una factura para un contrato eliminado");
        }

        EstadoContrato estado = contratoReal.getEstadoContrato();
        if (estado == EstadoContrato.FINALIZADO
                || estado == EstadoContrato.RESCINDIDO
                || estado == EstadoContrato.BORRADOR) {
            throw new FacturaInvalidaException(
                    "No se puede crear una factura para un contrato en estado " + estado);
        }
    }

    private void validarTransicionEstado(EstadoFactura anterior, EstadoFactura nuevo) {
        boolean valida = switch (anterior) {
            case PENDIENTE -> nuevo == EstadoFactura.PAGADA
                           || nuevo == EstadoFactura.VENCIDA
                           || nuevo == EstadoFactura.ANULADA;
            case VENCIDA   -> nuevo == EstadoFactura.PAGADA;
            default        -> false; // PAGADA y ANULADA no tienen transiciones válidas
        };

        if (!valida) {
            throw new FacturaInvalidaException(
                    "Transición de estado no permitida: " + anterior + " → " + nuevo);
        }
    }

    private void validarDatosPago(Factura factura) {
        if (factura.getFechaPago() == null) {
            throw new FacturaInvalidaException(
                    "La fecha de pago es obligatoria cuando la factura está pagada");
        }
        if (factura.getMedioPago() == null) {
            throw new FacturaInvalidaException(
                    "El medio de pago es obligatorio cuando la factura está pagada");
        }
        if (factura.getImportePagado() == null
                || factura.getImportePagado().signum() <= 0) {
            throw new FacturaInvalidaException(
                    "El importe pagado debe ser un número positivo");
        }
    }

    private void limpiarDatosPago(Factura factura) {
        factura.setFechaPago(null);
        factura.setMedioPago(null);
        factura.setImportePagado(null);
        factura.setInteres(null);
    }
}