package org.example.entidades;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PROTECTED;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true) // force para final
public class Sala {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String numero;
    @Column(nullable = false)
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false, updatable = false)
    @Setter(PACKAGE)
    private Departamento departamento;


    @OneToMany (mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Cita> citas = new ArrayList<>();



    public void addCita(Cita c) {
        if (c == null) return;
        if (!citas.contains(c)) {
            citas.add(c);
            c.setSala(this);
        }
    }
    public void removeCita(Cita c) {
        if (citas.remove(c)) c.setSala(null);
    }

}
