package org.example.entidades;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE) // Ajustamos el nivel de acceso del setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Medico extends Persona {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EspecialidadMedica especialidad; // Campo del Medico

    @Embedded
    private Matricula matricula; // Campo del Medico

    @ManyToOne
    @Setter(AccessLevel.PACKAGE)
    private Departamento departamento;

    @OneToMany (mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;

    // === CONSTRUCTOR CRÍTICO PARA INICIALIZAR CAMPOS Y COLECCIONES CON @SuperBuilder ===
    protected Medico(MedicoBuilder<?, ?> builder) {
        super(builder); // Llama al constructor de Persona


        this.especialidad = builder.especialidad;
        this.matricula = builder.matricula;

        // 2. Inicialización manual obligatoria de colecciones
        this.citas = new ArrayList<>();
    }

    // Red de seguridad (es una buena práctica)
    @PostLoad
    @PostPersist
    private void ensureCollections() {
        if (citas == null) citas = new ArrayList<>();
    }

    public void addCita(Cita c) {
        if (c == null) return;
        if (this.citas == null) ensureCollections();
        if (!citas.contains(c)) {
            citas.add(c);
            c.setMedico(this);
        }
    }
    public void removeCita(Cita c) {
        if (this.citas.remove(c)) c.setMedico(null);
    }
}