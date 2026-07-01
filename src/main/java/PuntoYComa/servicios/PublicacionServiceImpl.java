package PuntoYComa.servicios;

import PuntoYComa.accesoDatos.CiudadRepository;
import PuntoYComa.accesoDatos.PropiedadRepository;
import PuntoYComa.accesoDatos.PublicacionRepository;
import PuntoYComa.entidades.*;
import PuntoYComa.excepciones.PublicacionInvalidaException;
import PuntoYComa.excepciones.RecursoNoEncontradoException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PublicacionServiceImpl implements PublicacionService {
    private final PublicacionRepository publicacionRepository;
    private final PropiedadRepository propiedadRepository;
    private final CiudadRepository ciudadRepository;

    public PublicacionServiceImpl(PublicacionRepository publicacionRepository, PropiedadRepository propiedadRepository, CiudadRepository ciudadRepository) {
        this.publicacionRepository = publicacionRepository;
        this.propiedadRepository = propiedadRepository;
        this.ciudadRepository = ciudadRepository;
    }

    @Override
    @Transactional
    public Publicacion crearPublicacion(Publicacion publicacion) {
        if (publicacion.getId() != null) {
            throw new PublicacionInvalidaException("Una nueva publicación no debe tener un ID asignado.");
        }
        if (publicacion.getPrecioMensual() == null || publicacion.getPrecioMensual().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PublicacionInvalidaException("El precio mensual debe ser un número positivo.");
        }
        if (publicacion.getPropiedad() == null || publicacion.getPropiedad().getId() == null) {
            throw new PublicacionInvalidaException("Debe seleccionar una propiedad válida.");
        }
        if (publicacion.getFechaPublicacion() == null) {
            throw new PublicacionInvalidaException("La fecha de publicación es requerida.");
        }
        Propiedad propiedadAsociada = propiedadRepository.findById(publicacion.getPropiedad().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("La propiedad indicada no existe."));

        if (propiedadAsociada.getEliminada() || propiedadAsociada.getEstadoDisponibilidad() != EstadoDisponibilidad.DISPONIBLE){
            throw new PublicacionInvalidaException("Solo pueden publicarse propiedades activas y en estado DISPONIBLE.");
        }
        boolean existeActiva = publicacionRepository
                .existsByPropiedadAndEstadoAndEliminadaFalse(publicacion.getPropiedad(), EstadoPublicacion.ACTIVA);
        if (existeActiva) {
            throw new PublicacionInvalidaException("Ya existe una publicación activa para esta propiedad.");
        }

        publicacion.setEliminada(false);

        HistorialEstadoPublicacion historial = new HistorialEstadoPublicacion();
        historial.setEstadoPublicacion(EstadoPublicacion.ACTIVA);
        historial.setFechaHora(LocalDateTime.now());
        historial.setPublicacion(publicacion);
        if (publicacion.getHistorialEstados() == null) {
            publicacion.setHistorialEstados(new ArrayList<>());
        }
        publicacion.getHistorialEstados().add(historial);

        return publicacionRepository.save(publicacion);
    }

    @Override
    @Transactional
    public void eliminarPublicacion(Long id) {
        Publicacion publicacion = publicacionRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Publicacion no encontrada"));
        if (publicacion.getEstado() != EstadoPublicacion.ACTIVA) {
            throw new PublicacionInvalidaException("No se puede eliminar la publicación." +
                    " Solo pueden eliminarse publicaciones en estado ACTIVA.");
        }
        publicacion.setEliminada(true);
        publicacionRepository.save(publicacion);
    }

    @Override
    @Transactional
    public Publicacion modificarPublicacion(Long id, Publicacion publicacionActualizada) {
        Publicacion publicacionExistente = publicacionRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Publicacion no encontrada"));
        if (publicacionActualizada.getPrecioMensual() == null ||
                publicacionActualizada.getPrecioMensual().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PublicacionInvalidaException("El precio mensual debe ser un número positivo.");
        }
        if (publicacionExistente.getEstado() != EstadoPublicacion.FINALIZADA) {
            publicacionExistente.setCondiciones(publicacionActualizada.getCondiciones());
        } else if (!publicacionExistente.getCondiciones().equals(publicacionActualizada.getCondiciones())) {
            throw new PublicacionInvalidaException("No se pueden modificar las condiciones de una publicación FINALIZADA.");
        }
        if (publicacionActualizada.getEstado() == EstadoPublicacion.ACTIVA) {
            if (!publicacionExistente.getPropiedad().getEstadoDisponibilidad().name().equals("DISPONIBLE")) {
                throw new PublicacionInvalidaException("No se puede activar la publicación. La propiedad no está DISPONIBLE.");
            }
            if (publicacionExistente.getEstado() != EstadoPublicacion.ACTIVA) {
                boolean existeOtraActiva = publicacionRepository.existsByPropiedadAndEstadoAndEliminadaFalse(
                        publicacionExistente.getPropiedad(), EstadoPublicacion.ACTIVA);
                if (existeOtraActiva) {
                    throw new PublicacionInvalidaException("Ya existe otra publicación activa para esta propiedad.");
                }
            }
        }
        if (publicacionExistente.getEstado() != publicacionActualizada.getEstado()) {
            HistorialEstadoPublicacion nuevoHistorial = new HistorialEstadoPublicacion();
            nuevoHistorial.setEstadoPublicacion(publicacionActualizada.getEstado());
            nuevoHistorial.setFechaHora(LocalDateTime.now());
            nuevoHistorial.setPublicacion(publicacionExistente);
            publicacionExistente.getHistorialEstados().add(nuevoHistorial);
            publicacionExistente.setEstado(publicacionActualizada.getEstado());
        }
        publicacionExistente.setPrecioMensual(publicacionActualizada.getPrecioMensual());
        publicacionExistente.setFechaPublicacion(publicacionActualizada.getFechaPublicacion());
        publicacionExistente.setDescripcion(publicacionActualizada.getDescripcion());

        return publicacionRepository.save(publicacionExistente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Publicacion> listarPublicaciones(Long propiedadId, String ciudad,
                                                 EstadoPublicacion estado,
                                                 BigDecimal precioMin, BigDecimal precioMax) {
        String ciudadFiltro = (ciudad != null && ciudad.trim().isEmpty()) ? null : ciudad;
        BigDecimal min = (precioMin != null && precioMin.compareTo(BigDecimal.ZERO) <= 0) ? null : precioMin;
        BigDecimal max = (precioMax != null && precioMax.compareTo(BigDecimal.ZERO) <= 0) ? null : precioMax;
        return publicacionRepository.filtrarPublicaciones(
                propiedadId, ciudadFiltro, estado, min, max);
    }

    @Override
    public Publicacion buscarPublicacion(Long id) {
        return publicacionRepository.findById(id).orElseThrow(()
                -> new RecursoNoEncontradoException("Publicacion no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ciudad> listarCiudades() {
        return ciudadRepository.findAll();
    }
}
