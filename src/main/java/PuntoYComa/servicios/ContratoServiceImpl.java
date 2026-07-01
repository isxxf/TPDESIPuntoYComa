package PuntoYComa.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import PuntoYComa.accesoDatos.ContratoRepository;
import PuntoYComa.entidades.Contrato;
import PuntoYComa.entidades.EstadoContrato;
import PuntoYComa.entidades.EstadoDisponibilidad;
import PuntoYComa.entidades.Persona;
import PuntoYComa.entidades.Propiedad;

@Service
public class ContratoServiceImpl implements ContratoService {

    private final ContratoRepository contratoRepository;

    public ContratoServiceImpl(ContratoRepository contratoRepository) {
        this.contratoRepository = contratoRepository;
    }

    @Override
    @Transactional
    public Contrato guardar(Contrato contrato) {
        validarDatosObligatorios(contrato);

        Contrato contratoAnterior = null;
        EstadoContrato estadoAnterior = null;

        // Si el contrato ya existe, se recupera su estado anterior
        // para validar la transición y registrar el historial si corresponde.
        if (contrato.getId() != null) {
            contratoAnterior = buscarPorId(contrato.getId());
            estadoAnterior = contratoAnterior.getEstadoContrato();

            validarTransicionEstado(estadoAnterior, contrato.getEstadoContrato());
        }

        // Antes de activar un contrato se validan las reglas de negocio
        // relacionadas con la disponibilidad de la propiedad.
        validarActivacion(contrato);

        // Al activar un contrato, la propiedad asociada pasa a estado ALQUILADA.
        if (contrato.getEstadoContrato() == EstadoContrato.ACTIVO) {
            contrato.getPropiedad()
                    .setEstadoDisponibilidad(EstadoDisponibilidad.ALQUILADA);
        }

        // Al finalizar o rescindir un contrato, la propiedad vuelve a estar DISPONIBLE.
        if (contrato.getEstadoContrato() == EstadoContrato.FINALIZADO
                || contrato.getEstadoContrato() == EstadoContrato.RESCINDIDO) {

            contrato.getPropiedad()
                    .setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        }

        boolean esNuevo = contrato.getId() == null;
        boolean cambioEstado = esNuevo
                || estadoAnterior != contrato.getEstadoContrato();

        // Se registra historial al crear el contrato o cuando cambia su estado.
        if (cambioEstado) {
            contrato.agregarHistorialEstado(contrato.getEstadoContrato());
        }

        return contratoRepository.save(contrato);
    }

    @Override
    public Contrato buscarPorId(Long id) {
        return contratoRepository.findById(id)
                .filter(contrato -> !Boolean.TRUE.equals(contrato.getEliminado()))
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontró un contrato con el identificador " + id
                    )
                );
    }

    @Override
    public List<Contrato> listarNoEliminados() {
        return contratoRepository.findByEliminadoFalse();
    }

    @Override
    public List<Contrato> buscarPorEstado(EstadoContrato estadoContrato) {
        return contratoRepository
                .findByEstadoContratoAndEliminadoFalse(estadoContrato);
    }

    @Override
    public List<Contrato> buscarPorPropiedad(Propiedad propiedad) {
        return contratoRepository
                .findByPropiedadAndEliminadoFalse(propiedad);
    }

    @Override
    public List<Contrato> buscarPorInquilino(Persona inquilino) {
        return contratoRepository
                .findByInquilinoAndEliminadoFalse(inquilino);
    }

    @Override
    public List<Contrato> buscarPorFechaInicio(LocalDate fechaInicio) {
        return contratoRepository
                .findByFechaInicioAndEliminadoFalse(fechaInicio);
    }

    @Override
    @Transactional
    public void eliminarLogicamente(Long id) {
        Contrato contrato = buscarPorId(id);

        // La eliminación es lógica y solo se permite para contratos en BORRADOR.
        if (contrato.getEstadoContrato() != EstadoContrato.BORRADOR) {
            throw new IllegalStateException(
                "Solo se pueden eliminar contratos en estado BORRADOR"
            );
        }

        contrato.setEliminado(true);
        contratoRepository.save(contrato);
    }

    @Override
    public boolean existeContratoActivoParaPropiedad(Propiedad propiedad) {
        return contratoRepository
                .existsByPropiedadAndEstadoContratoAndEliminadoFalse(
                    propiedad,
                    EstadoContrato.ACTIVO
                );
    }

    // Valida los datos obligatorios y las restricciones básicas del contrato.
    private void validarDatosObligatorios(Contrato contrato) {
        if (contrato == null) {
            throw new IllegalArgumentException(
                "El contrato no puede ser nulo"
            );
        }

        if (contrato.getPropiedad() == null) {
            throw new IllegalArgumentException(
                "La propiedad es obligatoria"
            );
        }

        if (contrato.getInquilino() == null) {
            throw new IllegalArgumentException(
                "El inquilino es obligatorio"
            );
        }

        if (contrato.getFechaInicio() == null) {
            throw new IllegalArgumentException(
                "La fecha de inicio es obligatoria"
            );
        }

        if (contrato.getDuracionMeses() == null
                || contrato.getDuracionMeses() <= 0) {
            throw new IllegalArgumentException(
                "La duración debe ser un número positivo"
            );
        }

        if (contrato.getImporteMensual() == null
                || contrato.getImporteMensual().signum() <= 0) {
            throw new IllegalArgumentException(
                "El importe mensual debe ser positivo"
            );
        }

        if (contrato.getDiaVencimientoMensual() == null
                || contrato.getDiaVencimientoMensual() < 1
                || contrato.getDiaVencimientoMensual() > 31) {
            throw new IllegalArgumentException(
                "El día de vencimiento debe estar entre 1 y 31"
            );
        }
    }

    // Controla que los cambios de estado respeten las transiciones válidas:
    // BORRADOR -> ACTIVO
    // ACTIVO -> FINALIZADO
    // ACTIVO -> RESCINDIDO
    // No se permite volver desde FINALIZADO o RESCINDIDO.
    private void validarTransicionEstado(
            EstadoContrato estadoAnterior,
            EstadoContrato estadoNuevo) {

        if (estadoAnterior == null || estadoNuevo == null) {
            return;
        }

        if (estadoAnterior == estadoNuevo) {
            return;
        }

        if (estadoAnterior == EstadoContrato.FINALIZADO
                || estadoAnterior == EstadoContrato.RESCINDIDO) {

            throw new IllegalStateException(
                "No se puede modificar el estado de un contrato finalizado o rescindido"
            );
        }

        if (estadoAnterior == EstadoContrato.BORRADOR
                && estadoNuevo != EstadoContrato.ACTIVO) {

            throw new IllegalStateException(
                "Un contrato en borrador solo puede pasar a activo"
            );
        }

        if (estadoAnterior == EstadoContrato.ACTIVO
                && estadoNuevo != EstadoContrato.FINALIZADO
                && estadoNuevo != EstadoContrato.RESCINDIDO) {

            throw new IllegalStateException(
                "Un contrato activo solo puede pasar a finalizado o rescindido"
            );
        }
    }

    // Reglas necesarias para activar un contrato:
    // la propiedad no debe estar eliminada, debe estar disponible
    // y no debe existir otro contrato activo para la misma propiedad.
    private void validarActivacion(Contrato contrato) {
        if (contrato.getEstadoContrato() != EstadoContrato.ACTIVO) {
            return;
        }

        if (Boolean.TRUE.equals(contrato.getPropiedad().getEliminada())) {
            throw new IllegalStateException(
                "No se puede activar un contrato para una propiedad eliminada"
            );
        }

        if (contrato.getPropiedad().getEstadoDisponibilidad()
                != EstadoDisponibilidad.DISPONIBLE) {
            throw new IllegalStateException(
                "La propiedad debe estar disponible para activar el contrato"
            );
        }

        boolean existeActivo =
                existeContratoActivoParaPropiedad(contrato.getPropiedad());

        if (existeActivo) {
            throw new IllegalStateException(
                "La propiedad ya tiene un contrato activo"
            );
        }
    }
}
