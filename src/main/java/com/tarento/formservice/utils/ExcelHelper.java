package com.tarento.formservice.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tarento.formservice.model.InstituteFormDataDto;

public class ExcelHelper {
	
	static String[] HEADERs = { "SI","District Name", "Parent Training Center Code", "Name of Institution", "Degree", "Course", "Forms(s) saved as draft", "Forms(s) submitted", "Timestamp of forms(s) submission" };
	
	static String SHEET = "Institute and forms Details";


	 public static ByteArrayInputStream instituteToExcel(List<InstituteFormDataDto> dataList) {

		    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
		      Sheet sheet = workbook.createSheet(SHEET);

		      // Header
		      Row headerRow = sheet.createRow(0);

		      for (int col = 0; col < HEADERs.length; col++) {
		        Cell cell = headerRow.createCell(col);
		        cell.setCellValue(HEADERs[col]);
		      }

		      int rowIdx = 1;
		      for (InstituteFormDataDto data : dataList) {
		        Row row = sheet.createRow(rowIdx++);

		        row.createCell(0).setCellValue(rowIdx-1);
		        row.createCell(1).setCellValue(data.getDistrictCode());
		        row.createCell(2).setCellValue(data.getCenterCode());
		        row.createCell(3).setCellValue(data.getInstituteName());
		        row.createCell(4).setCellValue(data.getDegree());
		        row.createCell(5).setCellValue(data.getCourse());
		        row.createCell(6).setCellValue(data.getFormsSavedAsDraft());
		        row.createCell(7).setCellValue(data.getFormsSubmitted());
		        row.createCell(8).setCellValue(data.getFormsSubmittedTimestamp());
		      }

		      workbook.write(out);
		      return new ByteArrayInputStream(out.toByteArray());
		    } catch (IOException e) {
		      throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
		    }
		  }
}
