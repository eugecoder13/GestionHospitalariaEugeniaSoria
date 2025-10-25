package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter(AccessLevel.PACKAGE) // Añadido para setHospital
@Builder(toBuilder = true) // Usamos Builder simple
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED) // Necesario para que Builder funcione bien
public class Departamento {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter (AccessLevel.NONE)
    private EspecialidadMedica especialidad; // Campo simple (String)

    // ELIMINAMOS el constructor manual que causaba el conflicto y el error persistente.
    /*
    protected Departamento(DepartamentoBuilder<?, ?> builder) { ... }
    */

    // Se usa @Builder.Default para asegurar la inicialización de las listas
    @OneToMany (mappedBy = "departamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Medico> medicos = new ArrayList<>();

    @OneToMany (mappedBy = "departamento", cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Sala> salas = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Setter(AccessLevel.PACKAGE)
    private Hospital hospital; // Ya inicializado por @AllArgsConstructor/Builder


    public void agregarMedico(Medico m) {
        if (m == null || medicos.contains(m)) return;

        // Validaciones: Ahora this.especialidad ya no será null gracias a @AllArgsConstructor
        if (m.getEspecialidad() == null) { // Solo necesitamos validar m.getEspecialidad()
            throw new IllegalArgumentException("El médico debe tener especialidad asignada.");
        }

        if (!m.getEspecialidad().equals(this.especialidad)) {
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