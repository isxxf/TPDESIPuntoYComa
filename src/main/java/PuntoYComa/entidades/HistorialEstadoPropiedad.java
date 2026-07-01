package PuntoYComa.entidades;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "historial_estado_propiedad")
public class HistorialEstadoPropiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoDisponibilidad estado;

    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;

    public HistorialEstadoPropiedad() {}

    // getters y setters
}