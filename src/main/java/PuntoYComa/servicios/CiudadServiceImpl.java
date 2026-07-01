package PuntoYComa.servicios;

import PuntoYComa.accesoDatos.CiudadRepository;
import PuntoYComa.entidades.Ciudad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CiudadServiceImpl implements CiudadService {

    @Autowired
    private CiudadRepository ciudadRepository;

    @Override
    public List<Ciudad> listar() {
        return ciudadRepository.findByEliminadaFalse();
    }
}