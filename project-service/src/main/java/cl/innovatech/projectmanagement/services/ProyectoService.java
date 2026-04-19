package cl.innovatech.projectmanagement.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import cl.innovatech.projectmanagement.entities.Proyecto;
import cl.innovatech.projectmanagement.repository.ProyectoRepository;

@ApplicationScope
@Service
public class ProyectoService {

    private final ProyectoRepository proyectoRepository;

    // ¡La Inyección de Dependencias en el constructor!
    public ProyectoService(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    public List<Proyecto> getProyectos() {
        return proyectoRepository.findAll();
    }

    public void add(Proyecto nuevoProyecto) {
        proyectoRepository.save(nuevoProyecto);
    }

}
