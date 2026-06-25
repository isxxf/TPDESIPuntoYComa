package PuntoYComa.servicios;

import PuntoYComa.entidades.EstadoPublicacion;
import PuntoYComa.entidades.Publicacion;

import java.math.BigDecimal;
import java.util.List;

public interface PublicacionService {
        // Historia 2.1 Alta de una publicación
        Publicacion crearPublicacion(Publicacion publicacion);
        // Historia 2.2 Eliminación de una publicación
        void eliminarPublicacion(Long id);
        // Historia 2.3 Modificación de una publicación
        Publicacion modificarPublicacion(Long id, Publicacion publicacion);
        // Historia 2.4 Listado de publicaciones
        List<Publicacion> listarPublicaciones(
                Long propiedadId,
                String ciudad,
                EstadoPublicacion estado,
                BigDecimal precioMin,
                BigDecimal precioMax
        );
}
