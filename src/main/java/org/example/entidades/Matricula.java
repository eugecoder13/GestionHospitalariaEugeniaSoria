package org.example.entidades;
/*
•	Usar @Embeddable (patrón Value Object)
•	Validación en constructor: formato MP-\d{4,6} con regex
•	Usado con @Embedded en Medico
*/
import jakarta.persistence.Embeddable;

@Embeddable
public class Matricula {
    private String numero;

    public Matricula(String numero) {
        if (numero == null || !numero.matches("MP-\\d{4,6}")) {
            throw new IllegalArgumentException("Número de matrícula inválido. Debe tener el formato MP-XXXX o MP-XXXXXX");
        }
        this.numero = numero;
    }
    @Deprecated // requerido por JPA
    protected Matricula() {
    }

    public String getNumero() {
        return numero;
    }
}
