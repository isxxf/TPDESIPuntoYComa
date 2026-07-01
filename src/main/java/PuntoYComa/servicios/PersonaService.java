package PuntoYComa.servicios;

import PuntoYComa.accesoDatos.PersonaRepository;
import PuntoYComa.entidades.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    public List<Persona> listar() {
        return personaRepository.findByEliminadaFalse();
    }

}