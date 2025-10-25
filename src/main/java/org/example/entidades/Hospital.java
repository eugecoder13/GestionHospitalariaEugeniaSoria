package org.example.entidades;
import jakarta.persistence.*;
import lombok.*;
import static lombok.AccessLevel.PROTECTED;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;

@Entity
@Getter
@Setter // Permite la inicialización de campos no finales
@Builder(toBuilder = true)
// 1. Necesario para JPA: Genera el constructor vacío y usa force=true para el campo final.
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
// 2. Necesario para @Builder: Genera el constructor con TODOS los campos (incluido 'telefono').
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Hospital {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE) // Previene que el teléfono se modifique después de la construcción
    private final String telefono; // Campo inmutable (final)

    // === El constructor manual fue ELIMINADO ya que choca con @AllArgsConstructor ===

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Asegura que el Builder inicialice la colección si no se pasa.
    private List<Departamento> departamentos = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Asegura que el Builder inicialice la colección si no se pasa.
    private List<Paciente> pacientes = new ArrayList<>();

    // ===================================================================================
    // MÉTODOS HELPER (Bidireccionalidad - Requisito RN-005)
    // ===================================================================================

    public void agregarDepartamento(Departamento d) {
        if (d == null || departamentos.contains(d)) return;
        departamentos.add(d);
        d.setHospital(this); // Mantiene la bidireccionalidad
    }

    public void agregarPaciente(Paciente p) {
        if (p == null || pacientes.contains(p)) return;

        // Se asume que la Historia Clínica se crea dentro del constructor de Paciente (RN-003).

        pacientes.add(p);
        p.setHospital(this); // Mantiene la bidireccionalidad
    }
}