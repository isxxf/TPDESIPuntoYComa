package PuntoYComa.accesoDatos;

import PuntoYComa.entidades.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    List<Persona> findByEliminadaFalse();

}