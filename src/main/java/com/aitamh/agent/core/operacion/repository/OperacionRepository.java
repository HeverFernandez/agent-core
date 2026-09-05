package com.aitamh.agent.core.operacion.repository;

import com.aitamh.agent.core.operacion.entity.Operacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

/**
 * Repositorio para Entity Operación.
 */
@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long> {

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByActivo(Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByTipoOperacionAndActivo(String tipoOperacion, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByIdEntidadFinancieraAndActivo(Long idEntidadFinanciera, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByUsuarioIdAndActivo(Long usuarioId, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByEstadoOperacionAndActivo(String estadoOperacion, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByFechaOperacionBetweenAndActivo(LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByTipoOperacionAndIdEntidadFinancieraAndActivo(
            String tipoOperacion, Long idEntidadFinanciera, Boolean activo, Pageable pageable);
}
