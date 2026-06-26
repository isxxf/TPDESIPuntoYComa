package PuntoYComa.entidades;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "propiedades")
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String direccion;

    @Enumerated(EnumType.STRING)
    private TipoPropiedad tipo;

    private Integer cantidadAmbientes;

    private Double metrosCuadrados;

    private String descripcion;

    private String comodidades;

    @Enumerated(EnumType.STRING)
    private EstadoDisponibilidad estadoDisponibilidad;

    private Boolean eliminada = false;

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private Persona propietario;

    @ManyToOne
    @JoinColumn(name = "ciudad_id")
    private Ciudad ciudad;

    public Propiedad() {}

    // getters y setters
}