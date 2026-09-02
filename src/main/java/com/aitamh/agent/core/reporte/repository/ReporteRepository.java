//package com.aitamh.agent.core.reporte.repository;
//
//import com.aitamh.agent.core.reporte.entity.Reporte;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//import java.time.LocalDateTime;
//
///**
// * Repositorio para Entity Reporte.
// */
//@Repository
//public interface ReporteRepository extends JpaRepository<Reporte, Long> {
//
//    Page<Reporte> findByActivo(Boolean activo, Pageable pageable);
//
//    Page<Reporte> findByTipoReporteAndActivo(String tipoReporte, Boolean activo, Pageable pageable);
//
//    Page<Reporte> findByUsuarioIdAndActivo(Long usuarioId, Boolean activo, Pageable pageable);
//
//    Page<Reporte> findByFechaGeneracionBetweenAndActivo(LocalDateTime inicio, LocalDateTime fin, Boolean activo, Pageable pageable);
//}
//
