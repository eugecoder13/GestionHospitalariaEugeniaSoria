package org.example.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED, force = true) // JPA + campo final
public class Cita {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Setters de paquete (solo para helpers dentro del agregado)
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    @Setter(PACKAGE)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    @Setter(PACKAGE)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "sala_id", nullable = false)
    @Setter(PACKAGE)
    private Sala sala;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    // Campo inmutable: sin setter
    @Column(nullable = false, precision = 12, scale = 2)
    @Setter(NONE)
    private final BigDecimal costo;

    // Setters públicos (controlados)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter // público
    @NonNull // Lombok agrega null-check en el setter
    private EstadoCita estado;

    @Column(length = 1000)
    @Setter // público
    private String observaciones;

    public Cita(Paciente paciente,
                Medico medico,
                Sala sala,
                LocalDateTime fechaHora,
                BigDecimal costo,
                EstadoCita estado,
                String observaciones) {
        this.paciente = java.util.Objects.requireNonNull(paciente);
        this.medico = java.util.Objects.requireNonNull(medico);
        this.sala = java.util.Objects.requireNonNull(sala);
        this.fechaHora = java.util.Objects.requireNonNull(fechaHora);
        this.costo = java.util.Objects.requireNonNull(costo);
        this.estado = java.util.Objects.requireNonNull(estado);
        this.observaciones = observaciones;
    }
}
