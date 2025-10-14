package org.example.entidades;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.*;
import lombok.experimental.SuperBuilder;
import static lombok.AccessLevel.PROTECTED;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)


public class Hospital {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private String direccion;
    @Column(nullable = false)
    private final String telefono;

    public Hospital(String nombre, String direccion, String telefono) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }


    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Departamento> departamentos = new ArrayList<>();

    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Paciente> pacientes = new ArrayList<>();

    public void agregarDepartamento(Departamento d) {
        if (d == null || departamentos.contains(d)) return;
        departamentos.add(d);
        d.setHospital(this);
    }

    public void agregarPaciente(Paciente p) {
        if (p == null || pacientes.contains(p)) return;
        pacientes.add(p);
        p.setHospital(this);
        if (p.getHistoriaClinica() == null) {

            new HistoriaClinica(p);
        }

    }
}
