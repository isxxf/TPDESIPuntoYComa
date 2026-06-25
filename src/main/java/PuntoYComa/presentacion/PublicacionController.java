package PuntoYComa.presentacion;

import PuntoYComa.entidades.EstadoPublicacion;
import PuntoYComa.entidades.Publicacion;
import PuntoYComa.excepciones.PublicacionInvalidaException;
import PuntoYComa.excepciones.RecursoNoEncontradoException;
import PuntoYComa.servicios.PublicacionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/publicaciones")
public class PublicacionController {
    private final PublicacionService publicacionService;

    public PublicacionController(PublicacionService publicacionService) {
        this.publicacionService = publicacionService;
    }

    private void cargarListadoEnModel(Model model) {
        List<Publicacion> publicaciones = publicacionService.listarPublicaciones(
                null, null, null, null, null);
        model.addAttribute("publicaciones", publicaciones);
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

        model.addAttribute("publicaciones", publicaciones);
        return "publicaciones";
    }

    @PostMapping("/crear-publicacion")
    public String crearPublicacion(@ModelAttribute Publicacion publicacion, Model model) {
        try {
            publicacionService.crearPublicacion(publicacion);
            model.addAttribute("mensajeExito", "Publicación creada con éxito.");
        } catch (PublicacionInvalidaException | RecursoNoEncontradoException e) {
            model.addAttribute("mensajeError", e.getMessage());
        }
        cargarListadoEnModel(model);
        return "publicaciones";
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
        return "publicaciones";
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
        return "publicaciones";
    }
}
