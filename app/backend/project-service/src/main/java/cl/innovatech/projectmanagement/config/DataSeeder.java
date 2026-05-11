package cl.innovatech.projectmanagement.config;

import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.entities.Tarea;
import cl.innovatech.projectmanagement.repository.ProyectoRepository;
import cl.innovatech.projectmanagement.repository.TareaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Carga de datos de prueba para el microservicio de proyectos.
 * Solo inserta datos si la base de datos está vacía (count == 0).
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedProjectData(ProyectoRepository proyectoRepository, TareaRepository tareaRepository) {
        return args -> {
            // Solo sembrar si no hay proyectos existentes
            if (proyectoRepository.count() > 0) {
                System.out.println(">>> [DataSeeder] Datos ya existentes. Saltando la siembra.");
                return;
            }

            System.out.println(">>> [DataSeeder] Base de datos vacía. Insertando datos de prueba...");

            // ==========================================
            // PROYECTO 1: Sistema ERP Corporativo
            // ==========================================
            Proyecto p1 = new Proyecto();
            p1.setNombre("Sistema ERP Corporativo");
            p1.setDescripcion("Desarrollo e implementación del sistema ERP para gestión integral de la empresa, incluyendo módulos de contabilidad, inventario y RRHH.");
            p1.setEstado("EN_PROGRESO");
            p1.setFechaInicio("2026-01-15");
            p1.setFechaFin("2026-07-30");
            p1.setComentarios("Proyecto prioritario para el cliente Alfa. Requiere integración con SAP existente.");
            proyectoRepository.save(p1);

            // Tareas del Proyecto 1
            // idProfesionalAsignado hace referencia al ID del usuario en resource-service
            // 5 = dev_carlos (Backend), 6 = dev_maria (Frontend), 7 = dev_andres (DevOps), 8 = dev_lucia (QA)
            Tarea t1_1 = new Tarea();
            t1_1.setNombre("Diseño de base de datos ERP");
            t1_1.setDescripcion("Modelar el esquema relacional para los módulos de contabilidad e inventario.");
            t1_1.setEstado("COMPLETADO");
            t1_1.setIdProfesionalAsignado(5L); // dev_carlos
            t1_1.setFechaInicio("2026-01-15");
            t1_1.setFechaFin("2026-02-15");
            t1_1.setComentarios("Esquema aprobado por el jefe de proyecto.");
            t1_1.setProyecto(p1);
            tareaRepository.save(t1_1);

            Tarea t1_2 = new Tarea();
            t1_2.setNombre("Módulo de Contabilidad - API REST");
            t1_2.setDescripcion("Implementar los endpoints REST para el módulo de contabilidad.");
            t1_2.setEstado("EN_PROGRESO");
            t1_2.setIdProfesionalAsignado(5L); // dev_carlos
            t1_2.setFechaInicio("2026-02-16");
            t1_2.setFechaFin("2026-04-30");
            t1_2.setComentarios("Sprint 3 en curso.");
            t1_2.setProyecto(p1);
            tareaRepository.save(t1_2);

            Tarea t1_3 = new Tarea();
            t1_3.setNombre("Interfaz de Usuario - Dashboard ERP");
            t1_3.setDescripcion("Desarrollar el dashboard principal con gráficos de KPIs financieros y de inventario.");
            t1_3.setEstado("EN_PROGRESO");
            t1_3.setIdProfesionalAsignado(6L); // dev_maria
            t1_3.setFechaInicio("2026-03-01");
            t1_3.setFechaFin("2026-05-30");
            t1_3.setComentarios("Diseño UI aprobado, en fase de implementación.");
            t1_3.setProyecto(p1);
            tareaRepository.save(t1_3);

            Tarea t1_4 = new Tarea();
            t1_4.setNombre("Configuración de CI/CD");
            t1_4.setDescripcion("Configurar pipelines de Jenkins para integración y despliegue continuo del ERP.");
            t1_4.setEstado("COMPLETADO");
            t1_4.setIdProfesionalAsignado(7L); // dev_andres
            t1_4.setFechaInicio("2026-01-20");
            t1_4.setFechaFin("2026-02-28");
            t1_4.setComentarios("Pipeline operativo en staging y producción.");
            t1_4.setProyecto(p1);
            tareaRepository.save(t1_4);

            Tarea t1_5 = new Tarea();
            t1_5.setNombre("Testing de Integración ERP");
            t1_5.setDescripcion("Ejecutar pruebas de integración entre los módulos de contabilidad, inventario y RRHH.");
            t1_5.setEstado("PENDIENTE");
            t1_5.setIdProfesionalAsignado(8L); // dev_lucia
            t1_5.setFechaInicio("2026-05-01");
            t1_5.setFechaFin("2026-06-30");
            t1_5.setComentarios("Esperando finalización del módulo de contabilidad.");
            t1_5.setProyecto(p1);
            tareaRepository.save(t1_5);

            // ==========================================
            // PROYECTO 2: Plataforma E-Commerce
            // ==========================================
            Proyecto p2 = new Proyecto();
            p2.setNombre("Plataforma E-Commerce");
            p2.setDescripcion("Construcción de una plataforma de comercio electrónico con pasarela de pagos, catálogo de productos y sistema de envíos.");
            p2.setEstado("EN_PROGRESO");
            p2.setFechaInicio("2026-03-01");
            p2.setFechaFin("2026-09-30");
            p2.setComentarios("Cliente Beta requiere integración con Transbank y Chilexpress.");
            proyectoRepository.save(p2);

            Tarea t2_1 = new Tarea();
            t2_1.setNombre("Catálogo de Productos - Backend");
            t2_1.setDescripcion("API REST para CRUD de productos con categorías, precios y stock.");
            t2_1.setEstado("COMPLETADO");
            t2_1.setIdProfesionalAsignado(5L); // dev_carlos
            t2_1.setFechaInicio("2026-03-01");
            t2_1.setFechaFin("2026-04-15");
            t2_1.setComentarios("Endpoints finalizados y documentados con Swagger.");
            t2_1.setProyecto(p2);
            tareaRepository.save(t2_1);

            Tarea t2_2 = new Tarea();
            t2_2.setNombre("Pasarela de Pagos Transbank");
            t2_2.setDescripcion("Integrar WebPay Plus para procesamiento de pagos con tarjetas de crédito y débito.");
            t2_2.setEstado("EN_PROGRESO");
            t2_2.setIdProfesionalAsignado(5L); // dev_carlos
            t2_2.setFechaInicio("2026-04-16");
            t2_2.setFechaFin("2026-06-15");
            t2_2.setComentarios("Integración en ambiente de certificación Transbank.");
            t2_2.setProyecto(p2);
            tareaRepository.save(t2_2);

            Tarea t2_3 = new Tarea();
            t2_3.setNombre("UI del Carrito de Compras");
            t2_3.setDescripcion("Desarrollar la interfaz del carrito con resumen de pedido, cálculo de envío y checkout.");
            t2_3.setEstado("PENDIENTE");
            t2_3.setIdProfesionalAsignado(6L); // dev_maria
            t2_3.setFechaInicio("2026-05-01");
            t2_3.setFechaFin("2026-07-30");
            t2_3.setComentarios("Wireframes aprobados por cliente.");
            t2_3.setProyecto(p2);
            tareaRepository.save(t2_3);

            Tarea t2_4 = new Tarea();
            t2_4.setNombre("Infraestructura Cloud AWS");
            t2_4.setDescripcion("Configurar la infraestructura en AWS: ECS, RDS, S3 para imágenes de productos.");
            t2_4.setEstado("EN_PROGRESO");
            t2_4.setIdProfesionalAsignado(7L); // dev_andres
            t2_4.setFechaInicio("2026-03-15");
            t2_4.setFechaFin("2026-05-30");
            t2_4.setComentarios("Ambiente de staging operativo. Falta configurar producción.");
            t2_4.setProyecto(p2);
            tareaRepository.save(t2_4);

            // ==========================================
            // PROYECTO 3: App Móvil de Gestión Interna
            // ==========================================
            Proyecto p3 = new Proyecto();
            p3.setNombre("App Móvil de Gestión Interna");
            p3.setDescripcion("Aplicación móvil para la gestión interna del equipo: control de asistencia, solicitudes de permisos y comunicación interna.");
            p3.setEstado("PENDIENTE");
            p3.setFechaInicio("2026-06-01");
            p3.setFechaFin("2026-12-15");
            p3.setComentarios("Proyecto interno. Prioridad media. Se usará React Native.");
            proyectoRepository.save(p3);

            Tarea t3_1 = new Tarea();
            t3_1.setNombre("Diseño UX/UI de la App");
            t3_1.setDescripcion("Diseñar los flujos de usuario y las pantallas principales en Figma.");
            t3_1.setEstado("PENDIENTE");
            t3_1.setIdProfesionalAsignado(6L); // dev_maria
            t3_1.setFechaInicio("2026-06-01");
            t3_1.setFechaFin("2026-07-15");
            t3_1.setComentarios("Se requiere validación con RRHH antes de iniciar.");
            t3_1.setProyecto(p3);
            tareaRepository.save(t3_1);

            Tarea t3_2 = new Tarea();
            t3_2.setNombre("API de Asistencia");
            t3_2.setDescripcion("Backend para registro de entrada/salida con geolocalización.");
            t3_2.setEstado("PENDIENTE");
            t3_2.setIdProfesionalAsignado(5L); // dev_carlos
            t3_2.setFechaInicio("2026-07-16");
            t3_2.setFechaFin("2026-09-30");
            t3_2.setComentarios(null);
            t3_2.setProyecto(p3);
            tareaRepository.save(t3_2);

            Tarea t3_3 = new Tarea();
            t3_3.setNombre("Testing QA Integral");
            t3_3.setDescripcion("Plan de pruebas completo: unitarias, integración y UAT con el equipo de RRHH.");
            t3_3.setEstado("PENDIENTE");
            t3_3.setIdProfesionalAsignado(8L); // dev_lucia
            t3_3.setFechaInicio("2026-10-01");
            t3_3.setFechaFin("2026-11-30");
            t3_3.setComentarios("Depende de la finalización de las tareas anteriores.");
            t3_3.setProyecto(p3);
            tareaRepository.save(t3_3);

            System.out.println(">>> [DataSeeder] Datos de prueba insertados exitosamente:");
            System.out.println("    - 3 Proyectos creados");
            System.out.println("    - 12 Tareas creadas con profesionales asignados");
        };
    }
}
