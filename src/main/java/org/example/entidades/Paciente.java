package org.example.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = PROTECTED) // requerido por JPA
public class Paciente extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String direccion;
    private String telefono;

    /** 1:1 bidireccional con cascada y orphanRemoval */
    @OneToOne(mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private HistoriaClinica historiaClinica;

    /** Citas del paciente */
    @OneToMany(mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();

    /** Dueño de la FK hacia Hospital */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    /** === Ctor protegido “de negocio” que crea la HC automáticamente === */
    protected Paciente(Hospital hospital) {
        this.hospital = Objects.requireNonNull(hospital);
        asegurarHistoriaClinica();
    }

    /** Red de seguridad: si alguien construye sin HC, se crea antes de persistir */
    @PrePersist
    private void prePersist() {
        asegurarHistoriaClinica();
    }

    /** Crea la HC si falta (sin exponer setter público) */
    void asegurarHistoriaClinica() {
        if (this.historiaClinica == null) {
            this.historiaClinica = new HistoriaClinica(this); // lado dueño está en HistoriaClinica
        }
    }

    /* ===== Helpers para mantener bidireccionalidad ===== */

    // Visibilidad de paquete: lo usa Hospital.agregarPaciente(...)
    void setHospital(Hospital h) { this.hospital = h; }

    public void addCita(Cita c) {
        if (c == null) return;
        if (!citas.contains(c)) {
            citas.add(c);
            c.setPaciente(this); // lado dueño de la relación Cita↔Paciente
        }
    }

    public void removeCita(Cita c) {
        if (citas.remove(c)) c.setPaciente(null);
    }
}
