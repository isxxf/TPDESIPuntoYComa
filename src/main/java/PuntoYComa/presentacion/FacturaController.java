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
import PuntoYComa.entidades.Contrato;
import PuntoYComa.entidades.EstadoContrato;
import PuntoYComa.entidades.EstadoFactura;
import PuntoYComa.entidades.Factura;
import PuntoYComa.entidades.MedioPago;
import PuntoYComa.servicios.ContratoService;
import PuntoYComa.servicios.FacturaService;
import PuntoYComa.servicios.PropiedadService;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final ContratoService contratoService;
    private final PropiedadService propiedadService;
    private final PersonaRepository personaRepository;

    public FacturaController(FacturaService facturaService,
                             ContratoService contratoService,
                             PropiedadService propiedadService,
                             PersonaRepository personaRepository) {
        this.facturaService = facturaService;
        this.contratoService = contratoService;
        this.propiedadService = propiedadService;
        this.personaRepository = personaRepository;
    }

    // HU 4.4 — Listado con filtros opcionales
    @GetMapping
    public String listarFacturas(
            @RequestParam(required = false) EstadoFactura estado,
            @RequestParam(required = false) Long contratoId,
            @RequestParam(required = false) Long propiedadId,
            @RequestParam(required = false) Long inquilinoId,
            @RequestParam(required = false) LocalDate fechaDesde,
            @RequestParam(required = false) LocalDate fechaHasta,
            Model model) {

        boolean hayFiltros = estado != null || contratoId != null
                || propiedadId != null || inquilinoId != null
                || fechaDesde != null || fechaHasta != null;

        List<Factura> facturas = hayFiltros
                ? facturaService.buscarConFiltros(
                        estado, contratoId, propiedadId, inquilinoId,
                        fechaDesde, fechaHasta)
                : facturaService.listarNoEliminadas();

        cargarModeloBase(model);
        model.addAttribute("facturas", facturas);
        model.addAttribute("factura", new Factura());

        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroContratoId", contratoId);
        model.addAttribute("filtroPropiedadId", propiedadId);
        model.addAttribute("filtroInquilinoId", inquilinoId);
        model.addAttribute("filtroFechaDesde", fechaDesde);
        model.addAttribute("filtroFechaHasta", fechaHasta);

        return "facturas/factura";
    }

    // HU 4.1 — Mostrar formulario de alta
    @GetMapping("/nuevo")
    public String mostrarFormularioAlta(Model model) {
        cargarModeloBase(model);
        model.addAttribute("facturas", facturaService.listarNoEliminadas());
        model.addAttribute("factura", new Factura());
        return "facturas/factura";
    }

    // HU 4.1 / 4.2 — Guardar (alta o modificación)
    @PostMapping("/guardar")
    public String guardarFactura(
            Factura factura,
            @RequestParam(value = "contratoId", required = false) Long contratoId,
            Model model) {
        try {
            if (contratoId != null) {
                Contrato contrato = contratoService.buscarPorId(contratoId);
                factura.setContrato(contrato);
            }
            facturaService.guardar(factura);
            return "redirect:/facturas";
        } catch (RuntimeException e) {
            cargarModeloBase(model);
            model.addAttribute("facturas", facturaService.listarNoEliminadas());
            model.addAttribute("factura", factura);
            model.addAttribute("error", e.getMessage());
            return "facturas/factura";
        }
    }

    // HU 4.2 — Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        try {
            Factura factura = facturaService.buscarPorId(id);

            if (factura.getEstado() == EstadoFactura.PAGADA) {
                cargarModeloBase(model);
                model.addAttribute("facturas", facturaService.listarNoEliminadas());
                model.addAttribute("factura", new Factura());
                model.addAttribute("error", "No se puede modificar la factura #" + id + " porque está PAGADA.");
                return "facturas/factura";
            }
            if (factura.getEstado() == EstadoFactura.ANULADA) {
                cargarModeloBase(model);
                model.addAttribute("facturas", facturaService.listarNoEliminadas());
                model.addAttribute("factura", new Factura());
                model.addAttribute("error", "No se puede modificar la factura #" + id + " porque está ANULADA.");
                return "facturas/factura";
            }

            cargarModeloBase(model);
            model.addAttribute("facturas", facturaService.listarNoEliminadas());
            model.addAttribute("factura", factura);
            return "facturas/factura";
        } catch (RuntimeException e) {
            cargarModeloBase(model);
            model.addAttribute("facturas", facturaService.listarNoEliminadas());
            model.addAttribute("factura", new Factura());
            model.addAttribute("error", e.getMessage());
            return "facturas/factura";
        }
    }

    // HU 4.3 — Eliminación lógica
    @PostMapping("/eliminar/{id}")
    public String eliminarFactura(@PathVariable Long id, Model model) {
        try {
            facturaService.eliminarLogicamente(id);
            return "redirect:/facturas";
        } catch (RuntimeException e) {
            cargarModeloBase(model);
            model.addAttribute("facturas", facturaService.listarNoEliminadas());
            model.addAttribute("factura", new Factura());
            model.addAttribute("error", e.getMessage());
            return "facturas/factura";
        }
    }

    private void cargarModeloBase(Model model) {
        model.addAttribute("contratosActivos",
                contratoService.buscarPorEstado(EstadoContrato.ACTIVO));
        model.addAttribute("todosLosContratos",
                contratoService.listarNoEliminados());
        model.addAttribute("todasLasPropiedades",
                propiedadService.listar());
        model.addAttribute("todosLosInquilinos",
                personaRepository.findAll());
        model.addAttribute("estadosFactura", EstadoFactura.values());
        model.addAttribute("mediosPago", MedioPago.values());
    }
}