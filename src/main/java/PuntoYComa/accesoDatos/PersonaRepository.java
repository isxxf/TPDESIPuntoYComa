package PuntoYComa.accesoDatos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import PuntoYComa.entidades.Persona;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
}