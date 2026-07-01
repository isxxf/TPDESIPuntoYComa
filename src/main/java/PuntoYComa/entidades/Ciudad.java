package PuntoYComa.entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "ciudades")
public class Ciudad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
    @Column(nullable = false)
    private boolean eliminada;

    
    public void setId(Long id) {
        this.id = id;
    }
    
    
    public Ciudad() {
    }

    public Ciudad(String nombre, boolean eliminada) {
        this.nombre = nombre;
        this.eliminada = eliminada;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEliminada() {
        return eliminada;
    }

    public void setEliminada(boolean eliminada) {
        this.eliminada = eliminada;
    }
}
