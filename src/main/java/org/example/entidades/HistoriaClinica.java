package org.example.entidades;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import static lombok.AccessLevel.PROTECTED;
import java.util.List;
import lombok.Builder;


@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Builder(toBuilder = true)
// ANOTACIÓN CLAVE: Le dice a @Builder que use un constructor con todos los campos.
@AllArgsConstructor(access = PROTECTED)
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
    // CRÍTICO: Indica que el Builder debe obligar a pasar este campo.
    @NonNull
    private Paciente paciente;

    @ElementCollection
    @CollectionTable(name = "hc_diagnosticos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "diagnostico", nullable = false)
    // Indica al Builder que use este valor por defecto si no se especifica.
    @Builder.Default
    private List<String> diagnosticos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "hc_tratamientos", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "tratamiento", nullable = false)
    @Builder.Default
    private List<String> tratamientos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "hc_alergias", joinColumns = @JoinColumn(name = "historia_id"))
    @Column(name = "alergia", nullable = false)
    @Builder.Default
    private List<String> alergias = new ArrayList<>();

    // ... Los métodos PrePersist y Helper (agregarDiagnostico, etc.) van aquí ...
    @PrePersist
    private void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.numeroHistoria == null) {
            // Se asume que paciente no es null gracias a @NonNull en el Builder
            final String dni = Objects.requireNonNull(paciente.getDni(), "DNI del paciente requerido");
            final String ts = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(fechaCreacion);
            this.numeroHistoria = "HC-" + dni + "-" + ts;
        }
    }

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