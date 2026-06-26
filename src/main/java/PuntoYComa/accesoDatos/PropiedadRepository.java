package PuntoYComa.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import PuntoYComa.entidades.Propiedad;
import PuntoYComa.entidades.Ciudad;

import java.util.List;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByEliminadaFalse();

    boolean existsByDireccionAndCiudadAndEliminadaFalse(String direccion, Ciudad ciudad);

}