package PuntoYComa.servicios;

import PuntoYComa.entidades.Propiedad;
import PuntoYComa.entidades.Ciudad;
import PuntoYComa.repositorios.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.time.LocalDateTime;

@Service
public class PropiedadServiceImpl implements PropiedadService {

    @Autowired
    private PropiedadRepository propiedadRepository;

    @Override
    public Propiedad guardar(Propiedad propiedad) {
        // Validaciones de negocio
        if (propiedad.getCantidadAmbientes() <= 0) {
            throw new RuntimeException("La cantidad de ambientes debe ser mayor a 0.");
        }
        if (propiedad.getMetrosCuadrados() <= 0) {
            throw new RuntimeException("Los metros cuadrados deben ser positivos.");
        }

        // Validar duplicado
        if (propiedadRepository.existsByDireccionAndCiudadAndEliminadaFalse(
                propiedad.getDireccion(), propiedad.getCiudad())) {
            throw new RuntimeException("Ya existe una propiedad con la misma dirección y ciudad.");
        }

        // Estado inicial
        if (propiedad.getEstadoDisponibilidad() == null) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        }

        return propiedadRepository.save(propiedad);
    }

    @Override
    public Propiedad actualizar(Propiedad propiedad) {
        // Validar duplicado (no duplicar después de modificar)
        if (propiedad.getCantidadAmbientes() <= 0) {
            throw new RuntimeException("La cantidad de ambientes debe ser mayor a 0.");
        }
        if (propiedad.getMetrosCuadrados() <= 0) {
            throw new RuntimeException("Los metros cuadrados deben ser positivos.");
        }

        // Validar duplicado (si cambió dirección o ciudad)
        Propiedad original = propiedadRepository.findById(propiedad.getId()).orElseThrow(() ->
            new RuntimeException("Propiedad no encontrada."));
        if (!original.getDireccion().equals(propiedad.getDireccion()) || !original.getCiudad().equals(propiedad.getCiudad())) {
            if (propiedadRepository.existsByDireccionAndCiudadAndEliminadaFalse(propiedad.getDireccion(), propiedad.getCiudad())) {
                throw new RuntimeException("Ya existe una propiedad con esa dirección y ciudad.");
            }
        }

        return propiedadRepository.save(propiedad);
    }

    @Override
    public void eliminar(Long id) {
        Propiedad propiedad = propiedadRepository.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada."));

        // Validar si tiene contrato activo
        //  coordinar con el compañero de Epic 3 (Contrato)
        // Si hay contrato activo, no se elimina, se lanza una excepción
        // Si no, se hace baja lógica
        propiedad.setEliminada(true);
        propiedadRepository.save(propiedad);
    }

    @Override
    public List<Propiedad> listar() {
        return propiedadRepository.findByEliminadaFalse();
    }

    @Override
    public Propiedad buscarPorId(Long id) {
        return propiedadRepository.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada."));
    }
}