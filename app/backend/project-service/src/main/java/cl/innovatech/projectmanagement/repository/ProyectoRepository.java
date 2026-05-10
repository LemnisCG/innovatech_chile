package cl.innovatech.projectmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.innovatech.projectmanagement.entities.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

}
