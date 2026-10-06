package com.aitamh.agent.core.operacion.repository;

import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.operacion.entity.Operacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repositorio para Entity Operación.
 */
@Repository
public interface OperacionRepository extends JpaRepository<Operacion, Long> {

//    @EntityGraph(attributePaths = {"entidadBanco"})
    Page<Operacion> findByTipoAndEntidadBancoAndActivo(
            String tipoOperacion, Long idEntidadFinanciera, Boolean activo, Pageable pageable);

    // Métodos para filtrado combinado con estado y rango de fechas
//    @EntityGraph(attributePaths = {"entidadBanco"})
    Page<Operacion> findByEstadoAndFechaBetweenAndActivo(
            EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

//    @EntityGraph(attributePaths = {"entidadBanco"})
    Page<Operacion> findByTipoAndEstadoAndFechaBetweenAndActivo(
            TipoOperacion tipo, EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

//    @EntityGraph(attributePaths = {"entidadBanco"})
    Page<Operacion> findByEntidadBancoAndEstadoAndFechaBetweenAndActivo(
            Entidad idEntidad, EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

//    @EntityGraph(attributePaths = {"entidadBanco"})
    Page<Operacion> findByEntidadBancoAndTipoAndEstadoAndFechaBetweenAndActivo(
            Entidad idEntidad, TipoOperacion tipo, EstadoOperacion estado, LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(o.comision), 0)
        FROM Operacion o
        WHERE o.activo = :activo
          AND o.estado = :estado
          AND o.fecha BETWEEN :inicio AND :fin
          AND (:tipo IS NULL OR o.tipo = :tipo)
          AND (:entidadId IS NULL OR o.entidadBanco.id = :entidadId)
        """)
    BigDecimal sumComisionByFilters(
            @Param("estado") EstadoOperacion estado,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("tipo") TipoOperacion tipo,
            @Param("entidadId") Long entidadId,
            @Param("activo") Boolean activo
    );
}
