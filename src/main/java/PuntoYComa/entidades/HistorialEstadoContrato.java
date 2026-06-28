package PuntoYComa.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class HistorialEstadoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    private EstadoContrato estadoContrato;

    private LocalDateTime fechaCambio;

    public HistorialEstadoContrato() {
    }

    public HistorialEstadoContrato(Contrato contrato, EstadoContrato estadoContrato) {
        this.contrato = contrato;
        this.estadoContrato = estadoContrato;
        this.fechaCambio = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public EstadoContrato getEstadoContrato() {
        return estadoContrato;
    }

    public void setEstadoContrato(EstadoContrato estadoContrato) {
        this.estadoContrato = estadoContrato;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }
}