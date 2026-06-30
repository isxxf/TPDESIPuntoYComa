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

import PuntoYComa.entidades.Contrato;
import PuntoYComa.entidades.EstadoContrato;
import PuntoYComa.servicios.ContratoService;

import PuntoYComa.accesoDatos.PersonaRepository;
import PuntoYComa.accesoDatos.PropiedadRepository;
import PuntoYComa.entidades.Persona;
import PuntoYComa.entidades.Propiedad;

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
            Model model) {

        List<Contrato> contratos;

        if (estadoContrato != null) {
            contratos = contratoService.buscarPorEstado(estadoContrato);
        } else if (fechaInicio != null) {
            contratos = contratoService.buscarPorFechaInicio(fechaInicio);
        } else {
            contratos = contratoService.listarNoEliminados();
        }

        if (!model.containsAttribute("contrato")) {
            model.addAttribute("contrato", new Contrato());
        }

        model.addAttribute("contratos", contratos);
        model.addAttribute("estadosContrato", EstadoContrato.values());

        return "contratos/contratos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("contrato", new Contrato());
        model.addAttribute("contratos", contratoService.listarNoEliminados());
        model.addAttribute("estadosContrato", EstadoContrato.values());

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
                throw new IllegalArgumentException("Debe ingresar el ID del inquilino");
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
            model.addAttribute("contrato", contrato);
            model.addAttribute("contratos", contratoService.listarNoEliminados());
            model.addAttribute("estadosContrato", EstadoContrato.values());
            model.addAttribute("error", "Los IDs de propiedad e inquilino deben ser números válidos");

            return "contratos/contratos";

        } catch (RuntimeException e) {
            model.addAttribute("contrato", contrato);
            model.addAttribute("contratos", contratoService.listarNoEliminados());
            model.addAttribute("estadosContrato", EstadoContrato.values());
            model.addAttribute("error", e.getMessage());

            return "contratos/contratos";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Contrato contrato = contratoService.buscarPorId(id);

        model.addAttribute("contrato", contrato);
        model.addAttribute("contratos", contratoService.listarNoEliminados());
        model.addAttribute("estadosContrato", EstadoContrato.values());

        return "contratos/contratos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarContrato(@PathVariable Long id, Model model) {
        try {
            contratoService.eliminarLogicamente(id);
            return "redirect:/contratos";
        } catch (RuntimeException e) {
            model.addAttribute("contrato", new Contrato());
            model.addAttribute("contratos", contratoService.listarNoEliminados());
            model.addAttribute("estadosContrato", EstadoContrato.values());
            model.addAttribute("error", e.getMessage());

            return "contratos/contratos";
        }
    }
}