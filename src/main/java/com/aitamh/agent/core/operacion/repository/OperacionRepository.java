package com.aitamh.agent.core.operacion.repository;

import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
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
    Page<Operacion> findByUsuarioIdAndActivo(Long usuarioId, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByTipoOperacionAndIdEntidadFinancieraAndActivo(
            String tipoOperacion, Long idEntidadFinanciera, Boolean activo, Pageable pageable);

    // Métodos para filtrado combinado con estado y rango de fechas
    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByEstadoOperacionAndFechaOperacionBetweenAndActivo(
            EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByTipoOperacionAndEstadoOperacionAndFechaOperacionBetweenAndActivo(
            TipoOperacion tipo, EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByIdEntidadFinancieraAndEstadoOperacionAndFechaOperacionBetweenAndActivo(
            Long idEntidad, EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

    @EntityGraph(attributePaths = {"entidadFinanciera"})
    Page<Operacion> findByIdEntidadFinancieraAndTipoOperacionAndEstadoOperacionAndFechaOperacionBetweenAndActivo(
            Long idEntidad, TipoOperacion tipo, EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);
}
