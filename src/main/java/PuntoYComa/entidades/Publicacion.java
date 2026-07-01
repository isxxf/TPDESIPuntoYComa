package PuntoYComa.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Publicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    private BigDecimal precioMensual;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String condiciones;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotNull
    private LocalDate fechaPublicacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estado;

    private boolean eliminada;

    @ManyToOne
    @JoinColumn(name = "propiedad_id")
    private Propiedad propiedad;

    @OneToMany(mappedBy = "publicacion", cascade = CascadeType.ALL)
    @NotNull
    private List<HistorialEstadoPublicacion> historialEstados = new ArrayList<>();

    public Publicacion() {
    }

    public Publicacion(BigDecimal precioMensual, String condiciones,
                       String descripcion, LocalDate fechaPublicacion,
                       EstadoPublicacion estado, boolean eliminada,
                       List<HistorialEstadoPublicacion> historialEstados) {
        this.precioMensual = precioMensual;
        this.condiciones = condiciones;
        this.descripcion = descripcion;
        this.fechaPublicacion = fechaPublicacion;
        this.estado = estado;
        this.eliminada = eliminada;

        this.historialEstados = historialEstados;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPrecioMensual() {
        return precioMensual;
    }

    public void setPrecioMensual(BigDecimal precioMensual) {
        this.precioMensual = precioMensual;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public EstadoPublicacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoPublicacion estado) {
        this.estado = estado;
    }

    public boolean isEliminada() {
        return eliminada;
    }

    public void setEliminada(boolean eliminada) {
        this.eliminada = eliminada;
    }

    public List<HistorialEstadoPublicacion> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<HistorialEstadoPublicacion> historial) {
        this.historialEstados = historial;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }
}