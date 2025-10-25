package org.example;

import jakarta.persistence.*;
import org.example.entidades.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hospital-persistence-unit");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            // ===== 1) Hospital (Aggregate Root) =====
            Hospital hospital = Hospital.builder()
                    .nombre("Hospital Central")
                    .direccion("Av. Libertador 1234")
                    .telefono("011-4567-8901")
                    .build();

            // ===== 2) Departamentos =====
            Departamento cardio = Departamento.builder()
                    .nombre("Cardiología")
                    .especialidad(EspecialidadMedica.CARDIOLOGIA)
                    .build();

            Departamento pedia = Departamento.builder()
                    .nombre("Pediatría")
                    .especialidad(EspecialidadMedica.PEDIATRIA)
                    .build();

            Departamento trauma = Departamento.builder()
                    .nombre("Traumatología")
                    .especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                    .build();

            hospital.agregarDepartamento(cardio);
            hospital.agregarDepartamento(pedia);
            hospital.agregarDepartamento(trauma);

            // ===== 3) Salas por departamento =====
            Sala s1 = Sala.builder().numero("CARD-101").tipo("Consultorio").build();
            Sala s2 = Sala.builder().numero("PEDI-201").tipo("Consultorio").build();
            Sala s3 = Sala.builder().numero("TRAU-301").tipo("Quirófano").build();

            // asumimos helpers en Departamento para mantener la bidireccionalidad
            pedia.agregarSala(s2);
            cardio.agregarSala(s1);
            trauma.agregarSala(s3);

            // ===== 4) Médicos =====
            Medico mCardio = Medico.builder()
                    .nombre("Carlos").apellido("González").dni("12345678")
                    .fechaNacimiento(LocalDate.of(1975, 5, 15))
                    .tipoSangre(TipoSangre.A_POSITIVO)
                    .matricula(new Matricula("MP-12345"))
                    .especialidad(EspecialidadMedica.CARDIOLOGIA)
                    .build();
            cardio.agregarMedico(mCardio);

            Medico mPedia = Medico.builder()
                    .nombre("Ana").apellido("Paredes").dni("23456789")
                    .fechaNacimiento(LocalDate.of(1980, 7, 10))
                    .tipoSangre(TipoSangre.O_POSITIVO)
                    .matricula(new Matricula("MP-23456"))
                    .especialidad(EspecialidadMedica.PEDIATRIA)
                    .build();
            pedia.agregarMedico(mPedia);

            Medico mTrauma = Medico.builder()
                    .nombre("Luis").apellido("Rivas").dni("34567890")
                    .fechaNacimiento(LocalDate.of(1982, 3, 22))
                    .tipoSangre(TipoSangre.B_NEGATIVO)
                    .matricula(new Matricula("MP-34567"))
                    .especialidad(EspecialidadMedica.TRAUMATOLOGIA)
                    .build();
            trauma.agregarMedico(mTrauma);

            // ===== 5) Pacientes (HC se crea automáticamente en el constructor/builder) =====
            Paciente p1 = Paciente.builder()
                    .nombre("María").apellido("López").dni("11111111")
                    .fechaNacimiento(LocalDate.of(1985, 12, 5))
                    .tipoSangre(TipoSangre.A_POSITIVO)
                    .telefono("011-1111-1111").direccion("Calle Falsa 123")
                    .build();
            hospital.agregarPaciente(p1);

            Paciente p2 = Paciente.builder()
                    .nombre("Jorge").apellido("Suárez").dni("22222222")
                    .fechaNacimiento(LocalDate.of(1990, 2, 1))
                    .tipoSangre(TipoSangre.O_NEGATIVO)
                    .telefono("011-2222-2222").direccion("Av. Rivadavia 999")
                    .build();
            hospital.agregarPaciente(p2);

            Paciente p3 = Paciente.builder()
                    .nombre("Lucía").apellido("Ramos").dni("33333333")
                    .fechaNacimiento(LocalDate.of(2000, 9, 25))
                    .tipoSangre(TipoSangre.AB_POSITIVO)
                    .telefono("011-3333-3333").direccion("San Martín 456")
                    .build();
            hospital.agregarPaciente(p3);

            // Algunos datos en HC (diagnósticos, etc.)
            p1.getHistoriaClinica().agregarDiagnostico("Hipertensión arterial");
            p2.getHistoriaClinica().agregarAlergia("Penicilina");
            p3.getHistoriaClinica().agregarTratamiento("Kinesiología preventiva");

            // ===== 6) Persistencia (cascading desde Hospital) =====
            em.persist(hospital); // departamentos, salas, médicos y pacientes caen por cascade
            // (HistoriaClinica se persiste por cascade desde Paciente)

            // ===== 7) Citas (3 futuras, especialidades distintas) =====
            Cita c1 = new Cita(p1, mCardio, s1,
                    LocalDateTime.now().plusDays(2).withHour(10).withMinute(0),
                    new BigDecimal("15000.00"),
                    EstadoCita.PROGRAMADA,
                    "Chequeo post-operatorio");
            p1.addCita(c1); mCardio.addCita(c1); s1.addCita(c1);
            em.persist(c1); // la rúbrica pide persistir Cita explícitamente

            Cita c2 = new Cita(p2, mPedia, s2,
                    LocalDateTime.now().plusDays(3).withHour(9).withMinute(30),
                    new BigDecimal("12000.00"),
                    EstadoCita.PROGRAMADA,
                    "Control anual pediátrico");
            p2.addCita(c2); mPedia.addCita(c2); s2.addCita(c2);
            em.persist(c2);

            Cita c3 = new Cita(p3, mTrauma, s3,
                    LocalDateTime.now().plusDays(5).withHour(14).withMinute(0),
                    new BigDecimal("22000.00"),
                    EstadoCita.PROGRAMADA,
                    "Dolor de rodilla derecha");
            p3.addCita(c3); mTrauma.addCita(c3); s3.addCita(c3);
            em.persist(c3);

            em.getTransaction().commit();

            try {
                // Inicias una nueva transacción para la operación de escritura (UPDATE)
                em.getTransaction().begin();

                // 1. Modificas el estado de la cita c1
                c1.setEstado(EstadoCita.COMPLETADA);

                // 2. Usas em.merge() para persistir el cambio en la base de datos
                em.merge(c1);

                em.getTransaction().commit(); // Confirma la transacción de actualización
                System.out.println("ACTUALIZACIÓN: Cita " + c1.getId() + " marcada como COMPLETA.");

            } catch (Exception e) {
                if (em.getTransaction().isActive()) em.getTransaction().rollback();
                System.err.println("Error durante la actualización: " + e.getMessage());
                // Es crucial lanzar la excepción si el rollback ocurre, o el flujo normal
                // de tu `try-catch-finally` principal se hará cargo.
            }

            // ===== 8) Consultas JPQL (TypedQuery) =====
            // 8.1 SELECT hospitales
            TypedQuery<Hospital> qHosp = em.createQuery(
                    "SELECT h FROM Hospital h", Hospital.class);
            List<Hospital> hospitales = qHosp.getResultList();
            System.out.println("HOSPITALES: " + hospitales.size()); // muestra cantidad
            // 8.2 Médicos por especialidad (WHERE + parámetro)
            TypedQuery<Medico> qMedEsp = em.createQuery(
                    "SELECT m FROM Medico m WHERE m.especialidad = :esp", Medico.class);
            qMedEsp.setParameter("esp", EspecialidadMedica.PEDIATRIA);
            System.out.println("MÉDICOS PEDIATRÍA: " + qMedEsp.getResultList().size());
            // 8.3 Citas ordenadas por fecha (ORDER BY)
            TypedQuery<Cita> qCitasOrd = em.createQuery(
                    "SELECT c FROM Cita c ORDER BY c.fechaHora", Cita.class);
            System.out.println("PRIMERA CITA: " + qCitasOrd.getResultList().get(0).getFechaHora());
            // 8.4 COUNT por estado
            TypedQuery<Long> qCount = em.createQuery(
                    "SELECT COUNT(c) FROM Cita c WHERE c.estado = :estado", Long.class);
            qCount.setParameter("estado", EstadoCita.PROGRAMADA);
            System.out.println("CITAS PROGRAMADAS: " + qCount.getSingleResult());

            System.out.println("SISTEMA EJECUTADO EXITOSAMENTE ✅");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
