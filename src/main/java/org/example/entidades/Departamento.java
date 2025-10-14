package org.example.entidades;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true) // force para final
@SuperBuilder(toBuilder = true)

public class Departamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter (AccessLevel.NONE) // sin setter
    @NonNull
    private final String especialidad;


    @OneToMany (mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Medico> medicos = new ArrayList<>();
    @OneToMany (mappedBy = "departamento", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Sala> salas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Setter(AccessLevel.PACKAGE)
    private Hospital hospital;

    public void agregarMedico(Medico m) {
        if (m == null || medicos.contains(m)) return;
        if (!m.getEspecialidad().toString().equals(this.especialidad)) {
            throw new IllegalArgumentException("La especialidad del médico no es compatible con el departamento");
        }
        medicos.add(m);
        m.setDepartamento(this);
    }

    public void agregarSala(Sala s){
        if (s == null || salas.contains(s)) return;
        salas.add(s);
        s.setDepartamento(this);
    }

}
