package com.tarento.formservice.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tarento.formservice.models.Field;
import com.tarento.formservice.models.FormDetail;
import com.tarento.formservice.producer.FormServiceProducer;

public class ExcelFileUtil {
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ExcelFileUtil.class);

	private static final String CSV_CHARACTER_SEPERATOR = ",";

	@Autowired
	FormServiceProducer formServiceProducer;

	private static Long clientDateString(String source) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.UK);
		Date date = null;
		try {
			date = df.parse(source);
		} catch (ParseException e) {
			LOGGER.error("Encountered an Exception while parsing the Date : {}", e.getMessage());
		}
		if (date != null) {
			return date.getTime();
		} else {
			return null;
		}
	}

	private static Long removePercentSymbol(String source) {
		String value = source.replace("%", "");
		if (StringUtils.isNotBlank(value)) {
			return Long.parseLong(value);
		} else {
			return null;
		}
	}

	private static Long applyLogicOnfieldEnrolmentDate(String enrolmentDate) {
		if (!enrolmentDate.equals("") && !enrolmentDate.equals("\"\"") && StringUtils.isNotBlank(enrolmentDate)) {
			Calendar cal = Calendar.getInstance();
			String[] splitArray = enrolmentDate.split(" ");
			List<String> stringArray = Arrays.asList(splitArray);
			String firstPart = stringArray.get(0);
			String secondPart = stringArray.get(1);
			String[] datePart = firstPart.split("-");
			List<String> datePartList = Arrays.asList(datePart);
			String[] timePart = secondPart.split(":");
			List<String> timePartList = Arrays.asList(timePart);

			cal.set(Calendar.YEAR, Integer.parseInt(datePartList.get(0)));
			cal.set(Calendar.MONTH, Integer.parseInt(datePartList.get(1)) - 1);
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datePartList.get(2)));
			if (Integer.parseInt(timePartList.get(0)) < 12) {
				cal.set(Calendar.HOUR, Integer.parseInt(timePartList.get(0)) - 1);
				cal.set(Calendar.AM_PM, Calendar.AM);
			} else {
				cal.set(Calendar.HOUR, Integer.parseInt(timePartList.get(0)) - 13);
				cal.set(Calendar.AM_PM, Calendar.PM);
			}
			cal.set(Calendar.MINUTE, Integer.parseInt(timePartList.get(1)));
			cal.set(Calendar.SECOND, Integer.parseInt(timePartList.get(2)));
			return cal.getTimeInMillis();
		} else {
			return null;
		}

	}

	private static Long applyLogicOnfieldCompletionDate(String completionDate) {
		if (!completionDate.equals("") && !completionDate.equals("\"\"") && StringUtils.isNotBlank(completionDate)) {
			String[] splitArray = null;
			splitArray = completionDate.split("T");
			List<String> stringArray = Arrays.asList(splitArray);
			String firstPart = stringArray.get(0);
			String secondPart = stringArray.get(1);
			String[] datePart = firstPart.split("-");
			List<String> datePartList = Arrays.asList(datePart);
			String[] timePart = secondPart.split(":");
			List<String> timePartList = Arrays.asList(timePart);
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, Integer.parseInt(datePartList.get(0)));
			cal.set(Calendar.MONTH, Integer.parseInt(datePartList.get(1)) - 1);
			cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(datePartList.get(2)));
			if (Integer.parseInt(timePartList.get(0)) < 12) {
				cal.set(Calendar.HOUR, Integer.parseInt(timePartList.get(0)) - 1);
				cal.set(Calendar.AM_PM, Calendar.AM);
			} else {
				cal.set(Calendar.HOUR, Integer.parseInt(timePartList.get(0)) - 12);
				cal.set(Calendar.AM_PM, Calendar.PM);
			}
			cal.set(Calendar.MINUTE, Integer.parseInt(timePartList.get(1)));
			if (StringUtils.isNotBlank(timePartList.get(2))) {
				String[] secondsAndMilli = timePartList.get(2).split(".");
				if (secondsAndMilli.length > 1)
					cal.set(Calendar.SECOND, Integer.parseInt(secondsAndMilli[1]));
				return cal.getTimeInMillis();
			}
			return cal.getTimeInMillis();
		} else {
			return null;
		}
	}

	private static Map<Integer, Field> getOrderFormFieldMap(FormDetail formDetail) {
		Map<Integer, Field> fieldOrderMap = new LinkedHashMap<>();
		for (Field eachField : formDetail.getFields()) {
			if (!ExcelConstants.RATING_LIST_STRING.contains(eachField.getFieldType().toLowerCase())) {
				fieldOrderMap.put(eachField.getOrder(), eachField);
			}
		}
		return fieldOrderMap;
	}

	// Get Form Field Map
	// Key And Value both are field name
	private static Map<String, Field> getFormFieldMap(FormDetail formDetail) {
		Map<String, Field> fieldOrderMap = new LinkedHashMap<>();
		for (Field eachField : formDetail.getFields()) {
			if (!ExcelConstants.RATING_LIST_STRING.contains(eachField.getFieldType().toLowerCase())) {
				fieldOrderMap.put(eachField.getName(), eachField);
			}
		}
		return fieldOrderMap;
	}

	private static List<Row> filterActiveRowsFromWorksheet(XSSFSheet activeWorkSheet) {
		List<Row> activeRowList = new ArrayList<>();
		Iterator<Row> itr = activeWorkSheet.rowIterator();
		while (itr.hasNext()) {
			Row row = itr.next();
			if (row.getCell(1) != null) {
				activeRowList.add(row);
			}
		}
		return activeRowList;
	}

	@SuppressWarnings("unused")
	private static List<Row> filterActiveRowsFromWorksheet(HSSFSheet activeWorkSheet) {
		List<Row> activeRowList = new ArrayList<>();
		Iterator<Row> itr = activeWorkSheet.rowIterator();
		while (itr.hasNext()) {
			Row row = itr.next();
			if (row.getCell(1) != null) {
				activeRowList.add(row);
			}
		}
		return activeRowList;
	}

	@SuppressWarnings("unused")
	private static Map<String, Integer> constructHeaderIndexMap(XSSFSheet worksheet) {
		Map<String, Integer> headerIndexMap = new HashMap<>();
		XSSFRow currentRow = null;
		for (int i = 0; i < currentRow.getPhysicalNumberOfCells(); i++) {
			try {
				if (currentRow.getCell(i) != null && !currentRow.getCell(i).getStringCellValue().isEmpty()) {
					headerIndexMap.put(currentRow.getCell(i).getStringCellValue(), i);
				}
			} catch (NullPointerException nullEx) {
				LOGGER.error("Invalid cell references found : {}", nullEx.getMessage());
			} catch (Exception ex) {
				LOGGER.error("Unknown exception found : {} ", ex.getMessage());
			}
		}
		return headerIndexMap;
	}

	public static int randInt(int min, int max) {
		Random rand;
		rand = new Random();
		int randomNum;
		randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
}
