package org.example.entidades;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.Period;

import static lombok.AccessLevel.PROTECTED;

@MappedSuperclass
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = PROTECTED)
public abstract class Persona {

    protected String nombre;
    protected String apellido;
    protected String dni;
    protected LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    protected TipoSangre tipoSangre;

    /** Edad derivada (no se persiste) */
    @Transient
    public Integer getEdad() {
        return (fechaNacimiento == null) ? null
                : Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /** Validación opcional utilitaria (no override de nada) */
    public void validarDni() {
        if (dni == null || !dni.matches("\\d{7,8}")) {
            throw new IllegalArgumentException("El DNI debe tener 7 u 8 dígitos numéricos");
        }
    }
}
