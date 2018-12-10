package com.company;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;

import java.io.FileOutputStream;
import java.io.IOException;

public class TimetableGenerator {

    private static final int NUMBER_OF_STREAMS = 4;
    private static final int NUMBER_OF_COLUMNS = 2 + 4 * 2 + 4 * 2 + 3 * 2 + 2 * 2;
    private static final int[] numberOfColumnsForOneStream = {4 * 2, 4 * 2, 3 * 2, 2 * 2};
    private static final String[] headerValues = {"1 Поток\n Информатика",
                                                    "2 Поток\n Прикладная математика",
                                                    "3 Поток\n Компьютерная безопасность\n Экономическая кибернетика\n Актуальная математика",
                                                    "4 Поток\n Прикладная информатика"};
    private static final String[] groupNames = {"МСС", "ИСУ", "ДМА", "БМИ", "КТС", "ВычМ", "КТС", "ММУ", "ТВиМС", "ММАД", "МОУ", "ТП",
    "ММАД", "ТП", "ММАД", "МОУ", "ТВиМС", "ТП", "ИСУ", "МСС", "КТС"};
    private static final int[] groupOffSets = {2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1};

    private static final String[] days = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    private static final String[] timesForFirstShift = {"8.15", "9.45", "11.15", "13.00", "14.20"};
    private static final String[] timesForSecondShift = {"13.00", "14.20", "16.00", "17.30", "19.00"};
    private static final int NUMBER_OF_PAIRS_FOR_SECOND_SHIFT = 5;
    private static final int NUMBER_OF_PAIRS_FOR_FIRST_SHIFT = 3;
    private static final int NUMBER_OF_ROWS_FOR_ONE_PAIR = 4;
    private static final int COURSES = 4;
    private static final int NUMBER_OF_GROUPS = 13;
    private static final int NUMBER_OF_COLUMNS_FOR_ONE_PAIR = 2;
    private static final int NUMBER_OF_ROWS_FOR_HEADER = 6;
    private static final int NUMBER_OF_COLUMNS_FOR_HEADER = 2;

    static HSSFWorkbook wb;

    static HSSFSheet[] sheet;


    private static void fillAndMergeCell(HSSFRow row, int column, String value, int rowOffSet, int columnOffSet, HSSFWorkbook wb, HSSFSheet sheet) {
        HSSFCell cell = row.createCell(column);
        cell.setCellValue(value);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

//        cellStyle.setBorderBottom(BorderStyle.THICK);
//        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
//        cellStyle.setBorderLeft(BorderStyle.THIN);
//        cellStyle.setLeftBorderColor(IndexedColors.GREEN.getIndex());
//        cellStyle.setBorderRight(BorderStyle.THIN);
//        cellStyle.setRightBorderColor(IndexedColors.BLUE.getIndex());
//        cellStyle.setBorderTop(BorderStyle.MEDIUM_DASHED);
//        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());



        // Style the cell with borders all around.


        if (rowOffSet > 0 || columnOffSet > 0)
        sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum() + rowOffSet, column,
                column + columnOffSet));
        cell.setCellStyle(cellStyle);
    }

    public static void header(HSSFWorkbook wb, HSSFSheet sheet, Integer course) {

        int column = NUMBER_OF_COLUMNS_FOR_HEADER;
        int rowIndex = 0;

        HSSFRow row = sheet.getRow(rowIndex);
        fillAndMergeCell(row, 0, (course + 1) + " Курс", 3, 1, wb, sheet);

        // Names of streams
        for (int i = 0; i < NUMBER_OF_STREAMS; i++) {
            fillAndMergeCell(row, column, headerValues[i], 3, numberOfColumnsForOneStream[i] - 1, wb, sheet);
            column += numberOfColumnsForOneStream[i];
        }

        rowIndex += 4;
        int rowOffset = 1;

        // Names of departments
        if (course > 1) {
            row = sheet.getRow(rowIndex);
            column = NUMBER_OF_COLUMNS_FOR_HEADER;
            for (int i = 0; i < groupNames.length; i++) {
                fillAndMergeCell(row, column, groupNames[i], 0, groupOffSets[i] - 1, wb, sheet);
                column += groupOffSets[i];
            }
            rowIndex++;
            rowOffset--;
        }

        // Number of groups
        column = NUMBER_OF_COLUMNS_FOR_HEADER;
        row = sheet.getRow(rowIndex);
        Integer groupNumber = 1;
        for (int i = 2; i < NUMBER_OF_COLUMNS; i += 2) {
            fillAndMergeCell(row, column, groupNumber.toString() + " группа", rowOffset, 1, wb, sheet);
            groupNumber++;
            column += 2;
        }
    }

    private static void fillBorders(HSSFWorkbook wb, HSSFSheet sheet, Integer course) {
        int rowNumber = NUMBER_OF_ROWS_FOR_HEADER;
        int rowOffset = (course > 1) ? NUMBER_OF_PAIRS_FOR_SECOND_SHIFT : NUMBER_OF_PAIRS_FOR_FIRST_SHIFT;
        HSSFRow row;
        for (int i = 0; i < days.length; i++) {
            row = sheet.getRow(rowNumber);
            fillAndMergeCell(row, 0, days[i], rowOffset * NUMBER_OF_ROWS_FOR_ONE_PAIR - 1, 0, wb, sheet);
            for (int j = 0; j < rowOffset; j++) {
                row = sheet.getRow(rowNumber);
                String value = (course > 1) ? timesForSecondShift[j] : timesForFirstShift[j];
                fillAndMergeCell(row, 1, value, NUMBER_OF_ROWS_FOR_ONE_PAIR - 1, 0, wb, sheet);
                rowNumber += NUMBER_OF_ROWS_FOR_ONE_PAIR;
            }
        }
    }

    public static void fillRowWithPairs(TableBlock tableBlock, int day, int pairNumber) {
        GenericIterator<TableBlock> it = new GenericIterator(tableBlock);
        while (it.hasNext()) {
            GenericTuple<Pair, String> pairWithRoom = (GenericTuple<Pair, String>)it.next();
            Pair pair = pairWithRoom.getFirst();
            int course = pair.getCourse();
            int numberOfPairsInOneDay = (course > 1) ? NUMBER_OF_PAIRS_FOR_SECOND_SHIFT : NUMBER_OF_PAIRS_FOR_FIRST_SHIFT;

            HSSFSheet currentSheet = sheet[course];

            int rowOffSet = NUMBER_OF_ROWS_FOR_HEADER + day * numberOfPairsInOneDay * NUMBER_OF_ROWS_FOR_ONE_PAIR;
            if (course > 1) {
                rowOffSet += (pairNumber - 3) * NUMBER_OF_ROWS_FOR_ONE_PAIR;
            }
            else {
                rowOffSet += pairNumber * NUMBER_OF_ROWS_FOR_ONE_PAIR;
            }
            HSSFRow row = currentSheet.getRow(rowOffSet);

            int columnOffSet = pair.getNumberOfIterableElements() * NUMBER_OF_COLUMNS_FOR_ONE_PAIR;

            int columnIndex = NUMBER_OF_COLUMNS_FOR_HEADER + (int) pair.getIterableElement(0) * NUMBER_OF_COLUMNS_FOR_ONE_PAIR;
            if (tableBlock.isPairShiftToRight(pair, (int)pair.getIterableElement(0))) {
                columnIndex++;
            }

            if (pair instanceof Lab)
                columnOffSet /= 2;
            fillAndMergeCell(row, columnIndex, pair.getSubject(), 0, columnOffSet - 1, wb, currentSheet);
            row = currentSheet.getRow(rowOffSet + 1);
            fillAndMergeCell(row, columnIndex,pair.getTeacher(), 1, columnOffSet - 1, wb, currentSheet);
            row = currentSheet.getRow(rowOffSet + 3);
            fillAndMergeCell(row, columnIndex, pairWithRoom.getSecond(), 0, columnOffSet - 1, wb, currentSheet);
        }

    }

    public static void sink() {
        try {
            for (int k = 0; k < COURSES; k++) {
                PropertyTemplate pt = new PropertyTemplate();

                for (int i = 1; i < days.length; i++) {
                    // #1) these borders will all be medium in default color
                    pt.drawBorders(new CellRangeAddress(NUMBER_OF_ROWS_FOR_HEADER + i * NUMBER_OF_ROWS_FOR_ONE_PAIR * NUMBER_OF_PAIRS_FOR_FIRST_SHIFT,
                                    NUMBER_OF_ROWS_FOR_HEADER + i * NUMBER_OF_ROWS_FOR_ONE_PAIR * NUMBER_OF_PAIRS_FOR_FIRST_SHIFT, 0, NUMBER_OF_COLUMNS),
                            BorderStyle.MEDIUM, BorderExtent.TOP);
                }

                pt.applyBorders(sheet[k]);

                for (int r = 0; r < NUMBER_OF_COLUMNS_FOR_HEADER + NUMBER_OF_GROUPS * NUMBER_OF_COLUMNS_FOR_ONE_PAIR; r++) {
                    sheet[k].autoSizeColumn(r, true);
                }
            }
            String excelFileName = "test.xls";//name of excel file
            FileOutputStream fileOut = new FileOutputStream(excelFileName);

            //write this workbook to an Outputstream.
            wb.write(fileOut);
            fileOut.flush();
            fileOut.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize() {

            String excelFileName = "test.xls";//name of excel file

            String[] sheetName = {"First course", "Second course", "Third course", "Fouth course"};//name of sheet

            wb = new HSSFWorkbook();

            sheet = new HSSFSheet[4];
            for (int i = 0; i < COURSES; i++) {
                sheet[i] =  wb.createSheet(sheetName[i]);
            }
            for (int k = 0; k < COURSES; k++) {



                //iterating r number of rows
                int numberOfPairs = (k > 1) ? NUMBER_OF_PAIRS_FOR_SECOND_SHIFT : NUMBER_OF_PAIRS_FOR_FIRST_SHIFT;
                for (int r = 0; r < NUMBER_OF_ROWS_FOR_HEADER + days.length * numberOfPairs * NUMBER_OF_ROWS_FOR_ONE_PAIR; r++) {
                    HSSFRow row = sheet[k].createRow(r);

                    //iterating c number of columns
                    for (int c = 0; c < NUMBER_OF_COLUMNS_FOR_HEADER + NUMBER_OF_GROUPS * NUMBER_OF_COLUMNS_FOR_ONE_PAIR; c++) {
                        HSSFCell cell = row.createCell(c);
                        cell.setCellValue("");
                    }
                }
                header(wb, sheet[k], k);
                fillBorders(wb, sheet[k], k);
            }
    }
}
