package org.example.entidades;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;



@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)

public class Medico extends Persona {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EspecialidadMedica especialidad;

    @Embedded
    private Matricula matricula;

    @ManyToOne
    @Setter(AccessLevel.PACKAGE)
    private Departamento departamento;

    @OneToMany (mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Cita> citas = new ArrayList<>();


    protected Medico(MedicoBuilder<?, ?> builder) {
        super(builder);
        this.citas = new ArrayList<>();
    }

    @PostLoad
    @PostPersist
    private void ensureCollections() {
        if (citas == null) citas = new ArrayList<>();
    }


    public void addCita(Cita c) {
        if (c == null) return;
        if (!citas.contains(c)) {
            citas.add(c);
            c.setMedico(this);
        }
    }
    public void removeCita(Cita c) {
        if (citas.remove(c)) c.setMedico(null);
    }


}