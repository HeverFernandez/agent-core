package com.aitamh.agent.core.saldo.repository;

import com.aitamh.agent.core.saldo.entity.Saldo;
import com.aitamh.agent.core.saldo.enums.EstadoSaldo;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Entity Saldo.
 */
@Repository
public interface SaldoRepository extends JpaRepository<Saldo, Long> {

    Page<Saldo> findByEntidadFinancieraId(Long entidadFinancieraId, Pageable pageable);

    Page<Saldo> findByEstado(EstadoSaldo estado, Pageable pageable);

    List<Saldo> findByEntidadFinancieraIdAndEstado(Long entidadFinancieraId, EstadoSaldo estado);

    boolean existsByEntidadFinancieraIdAndEstadoIn(Long entidadFinancieraId, Collection<EstadoSaldo> estados);

    /**
     * Obtiene el saldo activo más reciente para una entidad financiera aplicando bloqueo pesimista.
     * Esto ayuda a evitar condiciones de carrera al procesar operaciones concurrentes.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Saldo s where s.entidadFinancieraId = :entidadFinancieraId and s.estado = :estado order by s.id desc")
    Optional<Saldo> findTopByEntidadFinancieraIdAndEstadoForUpdate(Long entidadFinancieraId, EstadoSaldo estado);
}
