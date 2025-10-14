package org.example.entidades;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import static lombok.AccessLevel.PROTECTED;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)

public class HistoriaClinica {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String numeroHistoria;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;


    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false, unique = true)
    private Paciente paciente;

    @ElementCollection
    @CollectionTable(name = "hc_diagnosticos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "diagnostico", nullable = false)
    private List<String> diagnosticos = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "hc_tratamientos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "tratamiento", nullable = false)
    private List<String> tratamientos = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "hc_alergias", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "alergia", nullable = false)
    private List<String> alergias = new ArrayList<>();



    public HistoriaClinica(Paciente paciente) {
        this.paciente = Objects.requireNonNull(paciente, "paciente es requerido");
    }
    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.numeroHistoria == null) {
            final String dni = Objects.requireNonNull(paciente.getDni(), "DNI del paciente requerido");
            final String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(fechaCreacion);
            this.numeroHistoria = "HC-" + dni + "-" + ts;
        } }

    public void agregarDiagnostico(String diagnostico) {
        if (diagnostico != null && !diagnostico.isBlank()) {
            diagnosticos.add(diagnostico);
        }
    }
    public void agregarTratamiento(String tratamiento) {
        if (tratamiento != null && !tratamiento.isBlank()) {
            tratamientos.add(tratamiento);
        }
    }
    public void agregarAlergia(String alergia) {
        if (alergia != null && !alergia.isBlank()) {
            alergias.add(alergia);
        }
    }
}
