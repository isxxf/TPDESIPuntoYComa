package PuntoYComa.presentacion;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import PuntoYComa.accesoDatos.PersonaRepository;
import PuntoYComa.accesoDatos.PropiedadRepository;
import PuntoYComa.entidades.Contrato;
import PuntoYComa.entidades.EstadoContrato;
import PuntoYComa.entidades.Persona;
import PuntoYComa.entidades.Propiedad;
import PuntoYComa.servicios.ContratoService;

@Controller
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService contratoService;
    private final PropiedadRepository propiedadRepository;
    private final PersonaRepository personaRepository;

    public ContratoController(
            ContratoService contratoService,
            PropiedadRepository propiedadRepository,
            PersonaRepository personaRepository) {

        this.contratoService = contratoService;
        this.propiedadRepository = propiedadRepository;
        this.personaRepository = personaRepository;
    }

    @GetMapping
    public String listarContratos(
            @RequestParam(required = false) EstadoContrato estadoContrato,
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) String propiedadId,
            Model model) {

        List<Contrato> contratos = contratoService.listarNoEliminados();

        try {
            if (estadoContrato != null) {
                contratos = contratos.stream()
                        .filter(contrato -> contrato.getEstadoContrato() == estadoContrato)
                        .toList();
            }

            if (fechaInicio != null) {
                contratos = contratos.stream()
                        .filter(contrato -> fechaInicio.equals(contrato.getFechaInicio()))
                        .toList();
            }

            if (propiedadId != null && !propiedadId.isBlank()) {
                Long idPropiedad = Long.parseLong(propiedadId);

                contratos = contratos.stream()
                        .filter(contrato -> contrato.getPropiedad() != null
                                && idPropiedad.equals(contrato.getPropiedad().getId()))
                        .toList();
            }

        } catch (NumberFormatException e) {
            model.addAttribute("error", "El filtro de propiedad debe ser un número válido");
        }

        if (!model.containsAttribute("contrato")) {
            model.addAttribute("contrato", new Contrato());
        }

        model.addAttribute("contratos", contratos);
        model.addAttribute("estadosContrato", EstadoContrato.values());

        model.addAttribute("propiedades", propiedadRepository.findByEliminadaFalse());
        model.addAttribute("personas", personaRepository.findByEliminadaFalse());

        model.addAttribute("filtroEstadoContrato", estadoContrato);
        model.addAttribute("filtroFechaInicio", fechaInicio);

        return "contratos/contratos";
    }

    @PostMapping("/guardar")
    public String guardarContrato(
            Contrato contrato,
            @RequestParam(required = false) String propiedadId,
            @RequestParam(required = false) String inquilinoId,
            Model model) {

        try {
            if (propiedadId == null || propiedadId.isBlank()) {
                throw new IllegalArgumentException("Debe ingresar el ID de la propiedad");
            }

            if (inquilinoId == null || inquilinoId.isBlank()) {
                throw new IllegalArgumentException("Debe seleccionar un inquilino");
            }

            Long idPropiedad = Long.parseLong(propiedadId);
            Long idInquilino = Long.parseLong(inquilinoId);

            Propiedad propiedad = propiedadRepository.findById(idPropiedad)
                    .orElseThrow(() ->
                            new IllegalArgumentException("No existe la propiedad indicada")
                    );

            Persona inquilino = personaRepository.findById(idInquilino)
                    .orElseThrow(() ->
                            new IllegalArgumentException("No existe el inquilino indicado")
                    );

            contrato.setPropiedad(propiedad);
            contrato.setInquilino(inquilino);

            contratoService.guardar(contrato);

            return "redirect:/contratos";

        } catch (NumberFormatException e) {
            cargarDatosVista(
                    model,
                    contrato,
                    contratoService.listarNoEliminados(),
                    null,
                    null,
                    propiedadId,
                    inquilinoId
            );

            model.addAttribute("error", "Los IDs de propiedad e inquilino deben ser números válidos");

            return "contratos/contratos";

        } catch (RuntimeException e) {
            cargarDatosVista(
                    model,
                    contrato,
                    contratoService.listarNoEliminados(),
                    null,
                    null,
                    propiedadId,
                    inquilinoId
            );

            model.addAttribute("error", e.getMessage());

            return "contratos/contratos";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Contrato contrato = contratoService.buscarPorId(id);

        cargarDatosVista(
                model,
                contrato,
                contratoService.listarNoEliminados(),
                null,
                null,
                contrato.getPropiedad() != null ? contrato.getPropiedad().getId().toString() : null,
                contrato.getInquilino() != null ? contrato.getInquilino().getId().toString() : null
        );

        return "contratos/contratos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarContrato(@PathVariable Long id, Model model) {
        try {
            contratoService.eliminarLogicamente(id);
            return "redirect:/contratos";

        } catch (RuntimeException e) {
            cargarDatosVista(
                    model,
                    new Contrato(),
                    contratoService.listarNoEliminados(),
                    null,
                    null,
                    null,
                    null
            );

            model.addAttribute("error", e.getMessage());

            return "contratos/contratos";
        }
    }

    private void cargarDatosVista(
            Model model,
            Contrato contrato,
            List<Contrato> contratos,
            EstadoContrato estadoContrato,
            LocalDate fechaInicio,
            String propiedadId,
            String inquilinoId) {

        model.addAttribute("contrato", contrato);
        model.addAttribute("contratos", contratos);
        model.addAttribute("estadosContrato", EstadoContrato.values());

        model.addAttribute("propiedades", propiedadRepository.findByEliminadaFalse());
        model.addAttribute("personas", personaRepository.findByEliminadaFalse());

        model.addAttribute("filtroEstadoContrato", estadoContrato);
        model.addAttribute("filtroFechaInicio", fechaInicio);
    }
}