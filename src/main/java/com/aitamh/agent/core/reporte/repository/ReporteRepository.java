package com.aitamh.agent.core.reporte.repository;

import com.aitamh.agent.core.operacion.entity.Operacion;
import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Operacion, Long> {

    List<Operacion> findByEstadoAndFechaBetweenAndActivo(
            EstadoOperacion estado,
            LocalDateTime inicio,
            LocalDateTime fin,
            Boolean activo
    );

    List<Operacion> findByTipoAndEstadoAndFechaBetweenAndActivo(
            TipoOperacion tipo,
            EstadoOperacion estado,
            LocalDateTime inicio,
            LocalDateTime fin,
            Boolean activo
    );

    List<Operacion> findByEntidadBancoAndEstadoAndFechaBetweenAndActivo(
            Entidad entidad,
            EstadoOperacion estado,
            LocalDateTime inicio,
            LocalDateTime fin,
            Boolean activo
    );

    List<Operacion> findByEntidadBancoAndTipoAndEstadoAndFechaBetweenAndActivo(
            Entidad entidad,
            TipoOperacion tipo,
            EstadoOperacion estado,
            LocalDateTime inicio,
            LocalDateTime fin,
            Boolean activo
    );
}
