package com.aitamh.agent.core.entidadfinanciera.repository;

import com.aitamh.agent.core.entidadfinanciera.entity.EntidadFinanciera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Entity EntidadFinanciera.
 */
@Repository
public interface EntidadFinancieraRepository extends JpaRepository<EntidadFinanciera, Long> {

    Optional<EntidadFinanciera> findByCodigoEntidad(String codigoEntidad);

    Page<EntidadFinanciera> findByTipoEntidadAndActivo(String tipoEntidad, Boolean activo, Pageable pageable);

    Page<EntidadFinanciera> findByActivo(Boolean activo, Pageable pageable);

    List<EntidadFinanciera> findByActivoAndEstado(Boolean activo, Boolean estado);

    boolean existsByCodigoEntidad(String codigoEntidad);
}

