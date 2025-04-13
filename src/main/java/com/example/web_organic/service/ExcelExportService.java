package com.example.web_organic.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ExcelExportService {

    @Autowired
    private DashboardService dashboardService;

    public ByteArrayInputStream exportDashboardToExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Tạo các styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Tạo sheet cho thống kê tổng quan
            createSummarySheet(workbook, headerStyle, dataStyle);

            // Tạo sheet cho doanh thu theo ngày
            createDailyRevenueSheet(workbook, headerStyle, dataStyle);

            // Tạo sheet cho doanh thu theo tháng
            createMonthlyRevenueSheet(workbook, headerStyle, dataStyle);

            // Tạo sheet cho phân phối trạng thái đơn hàng
            createOrderStatusSheet(workbook, headerStyle, dataStyle);

            // Tạo sheet cho sản phẩm hot
            createHotProductsSheet(workbook, headerStyle, dataStyle);

            // Tạo sheet cho cảnh báo tồn kho
            createStockAlertSheet(workbook, headerStyle, dataStyle);

            // Xuất file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private void createSummarySheet(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Tổng quan");

        // Lấy dữ liệu từ DashboardService
        var stats = dashboardService.getDashboardStats();

        // Tạo header
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Chỉ số", headerStyle);
        createHeaderCell(headerRow, 1, "Tháng này", headerStyle);
        createHeaderCell(headerRow, 2, "Tháng trước", headerStyle);
        createHeaderCell(headerRow, 3, "Tăng trưởng", headerStyle);

        // Dữ liệu Doanh thu
        Row revenueRow = sheet.createRow(1);
        createCell(revenueRow, 0, "Doanh thu", dataStyle);
        createCell(revenueRow, 1, stats.getCurrentMonthRevenue().toString(), dataStyle);
        createCell(revenueRow, 2, stats.getPreviousMonthRevenue().toString(), dataStyle);
        createCell(revenueRow, 3, stats.getRevenueGrowthRate() + "%", dataStyle);

        // Dữ liệu Đơn hàng
        Row orderRow = sheet.createRow(2);
        createCell(orderRow, 0, "Đơn hàng", dataStyle);
        createCell(orderRow, 1, String.valueOf(stats.getCurrentMonthOrders()), dataStyle);
        createCell(orderRow, 2, String.valueOf(stats.getPreviousMonthOrders()), dataStyle);
        createCell(orderRow, 3, stats.getOrderGrowthRate() + "%", dataStyle);

        // Dữ liệu Khách hàng mới
        Row customerRow = sheet.createRow(3);
        createCell(customerRow, 0, "Khách hàng mới", dataStyle);
        createCell(customerRow, 1, String.valueOf(stats.getNewCustomersCurrentMonth()), dataStyle);
        createCell(customerRow, 2, String.valueOf(stats.getNewCustomersPreviousMonth()), dataStyle);
        createCell(customerRow, 3, stats.getCustomerGrowthRate() + "%", dataStyle);

        // Auto-size các cột
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createDailyRevenueSheet(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Doanh thu theo ngày");

        // Lấy dữ liệu từ DashboardService
        List<Map<String, Object>> dailyRevenue = dashboardService.getDailyRevenue();

        // Tạo header
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Ngày", headerStyle);
        createHeaderCell(headerRow, 1, "Doanh thu", headerStyle);

        // Điền dữ liệu
        int rowNum = 1;
        for (Map<String, Object> day : dailyRevenue) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, day.get("date").toString(), dataStyle);
            createCell(row, 1, day.get("revenue").toString(), dataStyle);
        }

        // Auto-size các cột
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createMonthlyRevenueSheet(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Doanh thu theo tháng");

        // Lấy dữ liệu từ DashboardService
        List<Map<String, Object>> monthlyRevenue = dashboardService.getMonthlyRevenue();

        // Tạo header
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Tháng", headerStyle);
        createHeaderCell(headerRow, 1, "Doanh thu", headerStyle);

        // Điền dữ liệu
        int rowNum = 1;
        for (Map<String, Object> month : monthlyRevenue) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, month.get("month").toString(), dataStyle);
            createCell(row, 1, month.get("revenue").toString(), dataStyle);
        }

        // Auto-size các cột
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createOrderStatusSheet(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Trạng thái đơn hàng");

        // Lấy dữ liệu từ DashboardService
        List<Map<String, Object>> orderStatusDistribution = dashboardService.getOrderStatusDistribution();

        // Tạo header
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Trạng thái", headerStyle);
        createHeaderCell(headerRow, 1, "Số lượng", headerStyle);

        // Điền dữ liệu
        int rowNum = 1;
        for (Map<String, Object> status : orderStatusDistribution) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, status.get("status").toString(), dataStyle);
            createCell(row, 1, status.get("count").toString(), dataStyle);
        }

        // Auto-size các cột
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createHotProductsSheet(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Sản phẩm bán chạy");

        // Lấy dữ liệu từ DashboardService
        List<Map<String, Object>> hotProducts = dashboardService.getHotProducts();

        // Tạo header
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Tên sản phẩm", headerStyle);
        createHeaderCell(headerRow, 1, "Số lượng bán", headerStyle);

        // Điền dữ liệu
        int rowNum = 1;
        for (Map<String, Object> product : hotProducts) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, product.get("productName").toString(), dataStyle);
            createCell(row, 1, product.get("totalQuantitySold").toString(), dataStyle);
        }

        // Auto-size các cột
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createStockAlertSheet(Workbook workbook, CellStyle headerStyle, CellStyle dataStyle) {
        Sheet sheet = workbook.createSheet("Cảnh báo tồn kho");

        // Lấy dữ liệu từ DashboardService
        List<Map<String, Object>> stockAlerts = dashboardService.getStockAlerts();

        // Tạo header
        Row headerRow = sheet.createRow(0);
        createHeaderCell(headerRow, 0, "Sản phẩm", headerStyle);
        createHeaderCell(headerRow, 1, "Khối lượng", headerStyle);
        createHeaderCell(headerRow, 2, "Số lượng tồn", headerStyle);
        createHeaderCell(headerRow, 3, "Trạng thái", headerStyle);

        // Điền dữ liệu
        int rowNum = 1;
        for (Map<String, Object> alert : stockAlerts) {
            Row row = sheet.createRow(rowNum++);
            createCell(row, 0, alert.get("productName").toString(), dataStyle);
            createCell(row, 1, alert.get("weight").toString(), dataStyle);
            createCell(row, 2, alert.get("stockQuantity").toString(), dataStyle);
            createCell(row, 3, alert.get("stockStatus").toString(), dataStyle);
        }

        // Auto-size các cột
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createHeaderCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public ByteArrayInputStream exportCustomDateRangeToExcel(LocalDate startDate, LocalDate endDate) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            Sheet sheet = workbook.createSheet("Doanh thu " + startDate + " - " + endDate);

            // Lấy dữ liệu từ service
            Map<String, Object> revenueData = dashboardService.getCustomDateRangeRevenue(
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59)
            );

            List<String> labels = (List<String>) revenueData.get("labels");
            List<Double> values = (List<Double>) revenueData.get("values");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            createHeaderCell(headerRow, 0, "Ngày", headerStyle);
            createHeaderCell(headerRow, 1, "Doanh thu", headerStyle);

            // Điền dữ liệu
            for (int i = 0; i < labels.size(); i++) {
                Row row = sheet.createRow(i + 1);
                createCell(row, 0, labels.get(i), dataStyle);
                createCell(row, 1, values.get(i).toString(), dataStyle);
            }

            // Thêm tổng cộng ở dòng cuối
            Row totalRow = sheet.createRow(labels.size() + 2);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("Tổng cộng");
            totalLabelCell.setCellStyle(headerStyle);

            Cell totalValueCell = totalRow.createCell(1);
            Double total = values.stream().mapToDouble(Double::doubleValue).sum();
            totalValueCell.setCellValue(total.toString());
            totalValueCell.setCellStyle(headerStyle);

            // Auto-size các cột
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}