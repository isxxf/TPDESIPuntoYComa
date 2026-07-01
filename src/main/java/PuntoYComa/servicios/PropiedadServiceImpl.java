package PuntoYComa.servicios;

import PuntoYComa.entidades.EstadoDisponibilidad;
import PuntoYComa.accesoDatos.CiudadRepository;
import PuntoYComa.accesoDatos.PersonaRepository;
import PuntoYComa.entidades.Persona;
import PuntoYComa.entidades.Propiedad;
import PuntoYComa.entidades.Ciudad;
import PuntoYComa.accesoDatos.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class PropiedadServiceImpl implements PropiedadService {

	@Autowired
	private CiudadRepository ciudadRepository;

	@Autowired
	private PersonaRepository personaRepository;
	
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
        if (propiedadRepository.existsByDireccionAndCiudadIdAndEliminadaFalse(
                propiedad.getDireccion(), propiedad.getCiudad().getId())) {
            throw new RuntimeException("Ya existe una propiedad con la misma dirección y ciudad.");
        }

        // Estado inicial
        if (propiedad.getEstadoDisponibilidad() == null) {
            propiedad.setEstadoDisponibilidad(EstadoDisponibilidad.DISPONIBLE);
        }

        Ciudad ciudad = ciudadRepository.findById(propiedad.getCiudad().getId())
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

        Persona propietario = personaRepository.findById(propiedad.getPropietario().getId())
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        propiedad.setCiudad(ciudad);
        propiedad.setPropietario(propietario);
        
        return propiedadRepository.save(propiedad);
    }

    @Override
    public Propiedad actualizar(Propiedad propiedad) {

        if (propiedad.getCantidadAmbientes() <= 0) {
            throw new RuntimeException("La cantidad de ambientes debe ser mayor a 0.");
        }

        if (propiedad.getMetrosCuadrados() <= 0) {
            throw new RuntimeException("Los metros cuadrados deben ser positivos.");
        }

        if (propiedadRepository.existsByDireccionAndCiudadIdAndEliminadaFalseAndIdNot(
                propiedad.getDireccion(),
                propiedad.getCiudad().getId(),
                propiedad.getId())) {

            throw new RuntimeException("Ya existe una propiedad con esa dirección y ciudad.");
        }

        Ciudad ciudad = ciudadRepository.findById(propiedad.getCiudad().getId())
                .orElseThrow(() -> new RuntimeException("Ciudad no encontrada"));

        Persona propietario = personaRepository.findById(propiedad.getPropietario().getId())
                .orElseThrow(() -> new RuntimeException("Propietario no encontrado"));

        propiedad.setCiudad(ciudad);
        propiedad.setPropietario(propietario);

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