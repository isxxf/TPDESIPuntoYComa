package PuntoYComa.servicios;

import PuntoYComa.entidades.Propiedad;

import java.util.List;

public interface PropiedadService {

    Propiedad guardar(Propiedad propiedad);

    Propiedad actualizar(Propiedad propiedad);

    void eliminar(Long id);

    List<Propiedad> listar();

    Propiedad buscarPorId(Long id);
}