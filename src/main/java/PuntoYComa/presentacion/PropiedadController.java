package PuntoYComa.presentacion;

import PuntoYComa.entidades.Propiedad;
import PuntoYComa.servicios.CiudadService;
import PuntoYComa.servicios.PersonaService;
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
    
    @Autowired
    private CiudadService ciudadService;

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public String listarPropiedades(Model model) {
        List<Propiedad> propiedades = propiedadService.listar();
        model.addAttribute("propiedades", propiedades);
        return "propiedades/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {

        model.addAttribute("propiedad", new Propiedad());
        model.addAttribute("ciudades", ciudadService.listar());
        model.addAttribute("personas", personaService.listar());

        return "propiedades/formulario";
    }

    
    @PostMapping("/guardar")
    public String guardarPropiedad(@ModelAttribute Propiedad propiedad) {

        System.out.println("Ciudad ID: " + propiedad.getCiudad().getId());
        System.out.println("Propietario ID: " + propiedad.getPropietario().getId());
        System.out.println(propiedad.getCiudad());

        if (propiedad.getCiudad() != null) {
            System.out.println(propiedad.getCiudad().getId());
        }

        propiedadService.guardar(propiedad);

        return "redirect:/propiedades";
    }

    @GetMapping("/editar/{id}")
    public String editarPropiedad(@PathVariable Long id, Model model) {

        Propiedad propiedad = propiedadService.buscarPorId(id);

        model.addAttribute("propiedad", propiedad);
        model.addAttribute("ciudades", ciudadService.listar());
        model.addAttribute("personas", personaService.listar());

        return "propiedades/formulario";
    }

    @PostMapping("/actualizar")
    public String actualizarPropiedad(@ModelAttribute Propiedad propiedad) {

        System.out.println("ID = " + propiedad.getId());

        propiedadService.actualizar(propiedad);

        return "redirect:/propiedades";
    }

    

    @GetMapping("/eliminar/{id}")
    public String eliminarPropiedad(@PathVariable Long id) {
        propiedadService.eliminar(id);
        return "redirect:/propiedades";
    }
}