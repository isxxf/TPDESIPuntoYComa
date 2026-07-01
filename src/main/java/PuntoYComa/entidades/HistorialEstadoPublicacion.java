package PuntoYComa.entidades;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class HistorialEstadoPublicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estado;

    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "publicacion_id")
    private Publicacion publicacion;

    public HistorialEstadoPublicacion() {
    }

    public HistorialEstadoPublicacion(EstadoPublicacion estado, LocalDateTime fechaHora, Publicacion publicacion) {
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.publicacion = publicacion;
    }

    public Long getId() {
        return id;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public void setEstadoPublicacion(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Publicacion getPublicacion() {
        return publicacion;
    }

    public void setPublicacion(Publicacion publicacion) {
        this.publicacion = publicacion;
    }
}
