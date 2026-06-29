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
import PuntoYComa.entidades.EstadoFactura;
import PuntoYComa.entidades.Factura;
import PuntoYComa.entidades.MedioPago;
import PuntoYComa.servicios.ContratoService;
import PuntoYComa.servicios.FacturaService;

@Controller
@RequestMapping("/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final ContratoService contratoService;

    public FacturaController(FacturaService facturaService,
                             ContratoService contratoService) {
        this.facturaService = facturaService;
        this.contratoService = contratoService;
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

        // Parámetros de filtro para repintar el formulario de búsqueda
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroContratoId", contratoId);
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
    public String guardarFactura(Factura factura, Model model) {
        try {
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
            cargarModeloBase(model);
            model.addAttribute("facturas", facturaService.listarNoEliminadas());
            model.addAttribute("factura", factura);
            return "facturas/factura";
        } catch (RuntimeException e) {
            return "redirect:/facturas";
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

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void cargarModeloBase(Model model) {
        // Solo contratos activos y no eliminados para el desplegable del formulario
        List<Contrato> contratosActivos =
                contratoService.buscarPorEstado(EstadoContrato.ACTIVO);
        model.addAttribute("contratosActivos", contratosActivos);
        model.addAttribute("estadosFactura", EstadoFactura.values());
        model.addAttribute("mediosPago", MedioPago.values());
        // Lista completa de contratos no eliminados para el filtro del listado
        model.addAttribute("todosLosContratos",
                contratoService.listarNoEliminados());
    }
}