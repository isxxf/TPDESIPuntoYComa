package PuntoYComa.presentacion;

import PuntoYComa.entidades.Propiedad;
import PuntoYComa.servicios.PropiedadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadService propiedadService;

    @GetMapping
    public String listarPropiedades(Model model) {
        List<Propiedad> propiedades = propiedadService.listar();
        model.addAttribute("propiedades", propiedades);
        return "propiedades/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("propiedad", new Propiedad());
        // agregar  lista de personas, ciudades, etc.
        return "propiedades/formulario";
    }

    @PostMapping("/guardar")
    public String guardarPropiedad(@ModelAttribute Propiedad propiedad) {
        propiedadService.guardar(propiedad);
        return "redirect:/propiedades";
    }

    @GetMapping("/editar/{id}")
    public String editarPropiedad(@PathVariable Long id, Model model) {
        Propiedad propiedad = propiedadService.buscarPorId(id);
        model.addAttribute("propiedad", propiedad);
        //  agregar  listas de personas, ciudades, etc.
        return "propiedades/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarPropiedad(@ModelAttribute Propiedad propiedad) {
        propiedadService.actualizar(propiedad);
        return "redirect:/propiedades";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPropiedad(@PathVariable Long id) {
        propiedadService.eliminar(id);
        return "redirect:/propiedades";
    }
}