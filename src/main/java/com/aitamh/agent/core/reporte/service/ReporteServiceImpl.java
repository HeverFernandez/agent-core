//package com.aitamh.agent.core.reporte.service;
//
//import com.aitamh.agent.core.common.dto.PageResponse;
//import com.aitamh.agent.core.common.exception.EntityNotFoundException;
//import com.aitamh.agent.core.reporte.dto.ReporteRequest;
//import com.aitamh.agent.core.reporte.dto.ReporteResponse;
//import com.aitamh.agent.core.reporte.entity.Reporte;
//import com.aitamh.agent.core.reporte.mapper.ReporteMapper;
//import com.aitamh.agent.core.reporte.repository.ReporteRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDateTime;
//import java.util.stream.Collectors;
//
///**
// * Implementación del servicio para Reporte.
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ReporteServiceImpl implements ReporteService {
//
//    private final ReporteRepository repository;
//    private final ReporteMapper mapper;
//
//    @Override
//    public ReporteResponse create(ReporteRequest request) {
//        Reporte entity = mapper.toEntity(request);
//        entity.setFechaGeneracion(LocalDateTime.now());
//        entity = repository.save(entity);
//
//        log.info("Reporte creado: {}", entity.getId());
//        return mapper.toResponse(entity);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public ReporteResponse findById(Long id) {
//        Reporte entity = repository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException(
//                        String.format("Reporte no encontrado: %d", id)));
//        return mapper.toResponse(entity);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PageResponse<ReporteResponse> findAll(Pageable pageable) {
//        Page<Reporte> page = repository.findByActivo(true, pageable);
//        return buildPageResponse(page);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PageResponse<ReporteResponse> findByTipo(String tipoReporte, Pageable pageable) {
//        Page<Reporte> page = repository.findByTipoReporteAndActivo(tipoReporte, true, pageable);
//        return buildPageResponse(page);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PageResponse<ReporteResponse> findByUsuario(Long usuarioId, Pageable pageable) {
//        Page<Reporte> page = repository.findByUsuarioIdAndActivo(usuarioId, true, pageable);
//        return buildPageResponse(page);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public PageResponse<ReporteResponse> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable) {
//        Page<Reporte> page = repository.findByFechaGeneracionBetweenAndActivo(inicio, fin, true, pageable);
//        return buildPageResponse(page);
//    }
//
//    @Override
//    public ReporteResponse update(Long id, ReporteRequest request) {
//        Reporte entity = repository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException(
//                        String.format("Reporte no encontrado: %d", id)));
//
//        entity.setTipoReporte(request.getTipoReporte());
//        entity.setFiltrosAplicados(request.getFiltrosAplicados());
//        entity.setDatosReporte(request.getDatosReporte());
//        entity.setUsuarioId(request.getUsuarioId());
//
//        entity = repository.save(entity);
//        log.info("Reporte actualizado: {}", id);
//        return mapper.toResponse(entity);
//    }
//
//    @Override
//    public void delete(Long id) {
//        Reporte entity = repository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException(
//                        String.format("Reporte no encontrado: %d", id)));
//
//        entity.setActivo(false);
//        repository.save(entity);
//        log.info("Reporte eliminado: {}", id);
//    }
//
//    private PageResponse<ReporteResponse> buildPageResponse(Page<Reporte> page) {
//        return PageResponse.<ReporteResponse>builder()
//                .content(page.getContent().stream().map(mapper::toResponse).collect(Collectors.toList()))
//                .pageNumber(page.getNumber())
//                .pageSize(page.getSize())
//                .totalElements(page.getTotalElements())
//                .totalPages(page.getTotalPages())
//                .isFirst(page.isFirst())
//                .isLast(page.isLast())
//                .build();
//    }
//}
//
