package cl.innovatech.analytics.repositories;

import cl.innovatech.analytics.entities.DimTiempo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DimTiempoRepository extends JpaRepository<DimTiempo, Long> {
    Optional<DimTiempo> findByFecha(LocalDate fecha);
}
