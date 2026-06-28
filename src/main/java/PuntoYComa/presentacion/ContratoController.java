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

@Controller
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
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

        return "contratos";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("contrato", new Contrato());
        model.addAttribute("contratos", contratoService.listarNoEliminados());
        model.addAttribute("estadosContrato", EstadoContrato.values());

        return "contratos";
    }

    @PostMapping("/guardar")
    public String guardarContrato(Contrato contrato, Model model) {
        try {
            contratoService.guardar(contrato);
            return "redirect:/contratos";
        } catch (RuntimeException e) {
            model.addAttribute("contrato", contrato);
            model.addAttribute("contratos", contratoService.listarNoEliminados());
            model.addAttribute("estadosContrato", EstadoContrato.values());
            model.addAttribute("error", e.getMessage());

            return "contratos";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Contrato contrato = contratoService.buscarPorId(id);

        model.addAttribute("contrato", contrato);
        model.addAttribute("contratos", contratoService.listarNoEliminados());
        model.addAttribute("estadosContrato", EstadoContrato.values());

        return "contratos";
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

            return "contratos";
        }
    }
}