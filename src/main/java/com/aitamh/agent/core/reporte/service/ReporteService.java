//package com.aitamh.agent.core.reporte.service;
//
//import com.aitamh.agent.core.common.dto.PageResponse;
//import com.aitamh.agent.core.reporte.dto.ReporteRequest;
//import com.aitamh.agent.core.reporte.dto.ReporteResponse;
//import org.springframework.data.domain.Pageable;
//import java.time.LocalDateTime;
//
///**
// * Interfaz de servicio para Reporte.
// */
//public interface ReporteService {
//
//    ReporteResponse create(ReporteRequest request);
//
//    ReporteResponse findById(Long id);
//
//    PageResponse<ReporteResponse> findAll(Pageable pageable);
//
//    PageResponse<ReporteResponse> findByTipo(String tipoReporte, Pageable pageable);
//
//    PageResponse<ReporteResponse> findByUsuario(Long usuarioId, Pageable pageable);
//
//    PageResponse<ReporteResponse> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);
//
//    ReporteResponse update(Long id, ReporteRequest request);
//
//    void delete(Long id);
//}
//
