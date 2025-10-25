package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Setter // Añadimos setter para hospital (setHospital) y otras propiedades.
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = PROTECTED)
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
    private HistoriaClinica historiaClinica; // Se inicializa en el constructor.

    /** Citas del paciente */
    @OneToMany(mappedBy = "paciente",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Cita> citas; // Inicialización manual.

    /** Dueño de la FK hacia Hospital */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;

    // === CONSTRUCTOR CRÍTICO DE LOMBOK QUE MANEJA EL BUILDER Y LA HC ===
    protected Paciente(PacienteBuilder<?, ?> builder) {
        super(builder);
        // Asignación de campos del builder
        this.direccion = builder.direccion;
        this.telefono = builder.telefono;
        this.hospital = builder.hospital;

        // Inicialización de colecciones y HC (CRÍTICO)
        this.citas = new ArrayList<>();
        asegurarHistoriaClinica();
    }

    // Se elimina el constructor protected Paciente(Hospital hospital) ya que choca con SuperBuilder.


    /** Crea la HC si falta (sin exponer setter público) */
    void asegurarHistoriaClinica() {
        if (this.historiaClinica == null) {
            // El builder de HC debe tomar el Paciente.
            this.historiaClinica = HistoriaClinica.builder().paciente(this).build();
        }
    }

    /* ===== Helpers para mantener bidireccionalidad ===== */
    void setHospital(Hospital h) { this.hospital = h; } // Usado por Hospital.agregarPaciente

    public void addCita(Cita c) {
        if (c == null) return;
        if (this.citas == null) this.citas = new ArrayList<>(); // Red de seguridad
        if (!citas.contains(c)) {
            citas.add(c);
            c.setPaciente(this);
        }
    }

    public void removeCita(Cita c) {
        if (citas.remove(c)) c.setPaciente(null);
    }
}