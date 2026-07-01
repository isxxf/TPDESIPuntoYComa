package PuntoYComa.presentacion;

import PuntoYComa.entidades.EstadoPublicacion;
import PuntoYComa.entidades.Propiedad;
import PuntoYComa.entidades.Publicacion;
import PuntoYComa.excepciones.PublicacionInvalidaException;
import PuntoYComa.excepciones.RecursoNoEncontradoException;
import PuntoYComa.servicios.PropiedadService;
import PuntoYComa.servicios.PublicacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionController {
    private final PublicacionService publicacionService;
    private final PropiedadService propiedadService;
    public PublicacionController(PublicacionService publicacionService,
                                 PropiedadService propiedadService) {
        this.publicacionService = publicacionService;
        this.propiedadService = propiedadService;
    }

    private void cargarListadoEnModel(Model model) {
        List<Publicacion> publicaciones = publicacionService.listarPublicaciones(
                null, null, null, null, null);
        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("propiedadesDisponibles", propiedadService.listar());
    }

    @GetMapping
    public String getPublicaciones(@RequestParam(required = false) Long propiedadId,
                                   @RequestParam(required = false) String ciudad,
                                   @RequestParam(required = false) EstadoPublicacion estado,
                                   @RequestParam(required = false) BigDecimal precioMin,
                                   @RequestParam(required = false) BigDecimal precioMax,
                                   Model model) {
        List<Publicacion> publicaciones = publicacionService.listarPublicaciones(
                propiedadId, ciudad, estado, precioMin, precioMax);

        model.addAttribute("fechaHoy", LocalDate.now());
        model.addAttribute("ciudades", publicacionService.listarCiudades());
        model.addAttribute("publicaciones", publicaciones);
        model.addAttribute("propiedadesDisponibles", propiedadService.listar());

        model.addAttribute("filtroPropiedadId", propiedadId);
        model.addAttribute("filtroCiudad", ciudad);
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroPrecioMin", precioMin);
        model.addAttribute("filtroPrecioMax", precioMax);

        return "publicaciones/publicaciones";
    }

    @PostMapping("/crear-publicacion")
    public String crearPublicacion(@ModelAttribute Publicacion publicacion,
                                   @RequestParam("propiedad.id") Long propiedadId,
                                   Model model) {
        try {
            Propiedad propiedad = propiedadService.buscarPorId(propiedadId);
            publicacion.setPropiedad(propiedad);
            publicacionService.crearPublicacion(publicacion);
            model.addAttribute("mensajeExito", "Publicación creada con éxito.");
        } catch (PublicacionInvalidaException | RecursoNoEncontradoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        cargarListadoEnModel(model);
        return "publicaciones/publicaciones";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Publicacion publicacion = publicacionService.buscarPublicacion(id);
        model.addAttribute("publicacion", publicacion);
        return "publicaciones/formulario";
    }

    @PostMapping("/modificar-publicacion/{id}")
    public String modificarPublicacion(@PathVariable Long id, @ModelAttribute Publicacion publicacion, Model model) {
        try {
            publicacionService.modificarPublicacion(id, publicacion);
            model.addAttribute("mensajeExito", "Publicación modificada correctamente.");
        } catch (PublicacionInvalidaException | RecursoNoEncontradoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        cargarListadoEnModel(model);
        return "publicaciones/publicaciones";
    }

    @PostMapping("/eliminar-publicacion/{id}")
    public String eliminarPublicacion(@PathVariable Long id, Model model) {
        try {
            publicacionService.eliminarPublicacion(id);
            model.addAttribute("mensajeExito", "Publicación eliminada correctamente.");
        } catch (PublicacionInvalidaException | RecursoNoEncontradoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        cargarListadoEnModel(model);
        return "publicaciones/publicaciones";
    }
}
