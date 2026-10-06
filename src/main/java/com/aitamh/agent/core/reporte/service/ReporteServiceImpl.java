package com.aitamh.agent.core.reporte.service;

import com.aitamh.agent.core.common.exception.BusinessException;
import com.aitamh.agent.core.common.exception.EntityNotFoundException;
import com.aitamh.agent.core.common.exception.OperacionInvalidaException;
import com.aitamh.agent.core.entidadfinanciera.entity.Entidad;
import com.aitamh.agent.core.entidadfinanciera.repository.EntidadRepository;
import com.aitamh.agent.core.operacion.constants.EstadoOperacion;
import com.aitamh.agent.core.operacion.constants.TipoOperacion;
import com.aitamh.agent.core.operacion.entity.Operacion;
import com.aitamh.agent.core.reporte.repository.ReporteRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Implementación del servicio para Reporte.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private final ReporteRepository repository;
    private final EntidadRepository entidadRepository;

    @Override
    public byte[] exportarOperacionesExcel(String tipoOperacion, String estadoOperacion, Long entidad, String finicio, String ffin) {
        List<Operacion> operaciones = obtenerOperacionesFiltradas(tipoOperacion, estadoOperacion, entidad, finicio, ffin);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Operaciones");

            Row header = sheet.createRow(0);
            String[] columns = {
                    "ID",
                    "Tipo",
                    "Monto",
                    "Comisión",
                    "Descripción",
                    "N° Referencia",
                    "Fecha",
                    "Entidad",
                    "Servicio",
                    "Usuario",
                    "Estado"
            };

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIndex = 1;
            for (Operacion operacion : operaciones) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(operacion.getId() != null ? operacion.getId() : 0);
                row.createCell(1).setCellValue(operacion.getTipo() != null ? operacion.getTipo().name() : "");
                row.createCell(2).setCellValue(operacion.getMonto() != null ? operacion.getMonto().doubleValue() : 0D);
                row.createCell(3).setCellValue(operacion.getComision() != null ? operacion.getComision().doubleValue() : 0D);
                row.createCell(4).setCellValue(operacion.getDescripcion() != null ? operacion.getDescripcion() : "");
                row.createCell(5).setCellValue(operacion.getNumeroReferencia() != null ? operacion.getNumeroReferencia() : "");
                row.createCell(6).setCellValue(operacion.getFecha() != null ? operacion.getFecha().toString() : "");
                row.createCell(7).setCellValue(operacion.getEntidadBanco() != null && operacion.getEntidadBanco().getDenominacion() != null
                        ? operacion.getEntidadBanco().getDenominacion() : "");
                row.createCell(8).setCellValue(operacion.getEntidadServicio() != null && operacion.getEntidadServicio().getDenominacion() != null
                        ? operacion.getEntidadServicio().getDenominacion() : "");
                row.createCell(9).setCellValue(operacion.getUsuario() != null && operacion.getUsuario().getId() != null
                        ? operacion.getUsuario().getId() : 0L);
                row.createCell(10).setCellValue(operacion.getEstado() != null ? operacion.getEstado().name() : "");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error al generar el archivo Excel de operaciones", e);
            throw new BusinessException("No se pudo generar el reporte Excel");
        }
    }

    @Override
    public byte[] exportarOperacionesPdf(String tipoOperacion, String estadoOperacion, Long entidad, String finicio, String ffin) {
        List<Operacion> operaciones = obtenerOperacionesFiltradas(tipoOperacion, estadoOperacion, entidad, finicio, ffin);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Reporte de Operaciones", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(11);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10f);

            String[] headers = {
                    "ID", "Tipo", "Monto", "Comisión", "Descripción", "N° Referencia",
                    "Fecha", "Entidad", "Servicio", "Usuario", "Estado"
            };

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.HELVETICA, 9, Font.BOLD)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (Operacion operacion : operaciones) {
                table.addCell(String.valueOf(operacion.getId() != null ? operacion.getId() : 0L));
                table.addCell(operacion.getTipo() != null ? operacion.getTipo().name() : "");
                table.addCell(operacion.getMonto() != null ? operacion.getMonto().toPlainString() : "0");
                table.addCell(operacion.getComision() != null ? operacion.getComision().toPlainString() : "0");
                table.addCell(operacion.getDescripcion() != null ? operacion.getDescripcion() : "");
                table.addCell(operacion.getNumeroReferencia() != null ? operacion.getNumeroReferencia() : "");
                table.addCell(operacion.getFecha() != null ? operacion.getFecha().toString() : "");
                table.addCell(operacion.getEntidadBanco() != null && operacion.getEntidadBanco().getDenominacion() != null
                        ? operacion.getEntidadBanco().getDenominacion() : "");
                table.addCell(operacion.getEntidadServicio() != null && operacion.getEntidadServicio().getDenominacion() != null
                        ? operacion.getEntidadServicio().getDenominacion() : "");
                table.addCell(operacion.getUsuario() != null && operacion.getUsuario().getId() != null
                        ? String.valueOf(operacion.getUsuario().getId()) : "");
                table.addCell(operacion.getEstado() != null ? operacion.getEstado().name() : "");
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            log.error("Error al generar el archivo PDF de operaciones", e);
            throw new BusinessException("No se pudo generar el reporte PDF");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Operacion> obtenerOperacionesFiltradas(
            String tipoOperacion,
            String estadoOperacion,
            Long entidad,
            String finicio,
            String ffin) {

        LocalDateTime fechaInicio;
        LocalDateTime fechaFin;

        if (finicio == null || finicio.trim().isEmpty()) {
            fechaInicio = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        } else {
            fechaInicio = LocalDateTime.parse(finicio.trim());
        }

        if (ffin == null || ffin.trim().isEmpty()) {
            fechaFin = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        } else {
            fechaFin = LocalDateTime.parse(ffin.trim());
        }

        TipoOperacion tipo = null;
        if (tipoOperacion != null && !tipoOperacion.trim().isEmpty() && !"Todos".equalsIgnoreCase(tipoOperacion.trim())) {
            try {
                tipo = TipoOperacion.valueOf(tipoOperacion.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new OperacionInvalidaException("Tipo de operación no válido: " + tipoOperacion);
            }
        }

        EstadoOperacion estado;
        try {
            estado = EstadoOperacion.valueOf(estadoOperacion != null ? estadoOperacion.trim().toUpperCase() : "COMPLETADA");
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException("Estado de operación no válido: " + estadoOperacion);
        }

        if (entidad == null || entidad == 0) {
            if (tipo == null) {
                return repository.findByEstadoAndFechaBetweenAndActivo(estado, fechaInicio, fechaFin, true);
            }
            return repository.findByTipoAndEstadoAndFechaBetweenAndActivo(tipo, estado, fechaInicio, fechaFin, true);
        }

        Entidad entidadFinanciera = entidadRepository.findById(entidad)
                .orElseThrow(() -> new EntityNotFoundException("Entidad financiera no encontrada: " + entidad));

        if (tipo == null) {
            return repository.findByEntidadBancoAndEstadoAndFechaBetweenAndActivo(entidadFinanciera, estado, fechaInicio, fechaFin, true);
        }

        return repository.findByEntidadBancoAndTipoAndEstadoAndFechaBetweenAndActivo(
                entidadFinanciera,
                tipo,
                estado,
                fechaInicio,
                fechaFin,
                true
        );
    }
}
