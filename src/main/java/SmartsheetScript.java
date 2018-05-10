import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetBuilder;
import com.smartsheet.api.SmartsheetException;

import com.smartsheet.api.models.*;
import com.smartsheet.api.models.format.Currency;
import com.smartsheet.api.models.format.Format;
import com.smartsheet.api.models.format.NumberFormat;
import com.smartsheet.api.models.format.ThousandsSeparator;

import java.util.*;

public class SmartsheetScript {

    public static void buildSmartsheet() throws SmartsheetException {
        List<IntakeEntity> intakeList = new ArrayList<>();

        //set at the personal account level. Go to Profile>Personal Settings/API Access to generate a token. Token credentials match account credentials
        String accessToken = "8u3h7u10lxlvz0q3pfmvk6jp5i";

        //connect to Smartsheet
        Smartsheet smartsheet = new SmartsheetBuilder().setAccessToken(accessToken).build();

        //get project intake sheet
        Sheet sheet = smartsheet.sheetResources().getSheet(2847324411062148L, null, null, null, null,null, null, null);
        List<Row> rowList = sheet.getRows();

        if (rowList.size() > 0) {
            for (Row row : rowList) {
                List<Cell> cellsList = row.getCells();
                IntakeEntity intakeEntity = new IntakeEntity();

                intakeEntity.setCreatedByEmail(cellsList.get(2).getDisplayValue());
                intakeEntity.setDivision(cellsList.get(3).getDisplayValue());
                intakeEntity.setBusinessImperative(cellsList.get(4).getDisplayValue());
                intakeEntity.setExecutionPlan(cellsList.get(5).getDisplayValue());
                intakeEntity.setProject(cellsList.get(6).getDisplayValue());
                intakeEntity.setAccountable(cellsList.get(7).getDisplayValue());
                intakeEntity.setPlant(cellsList.get(8).getDisplayValue());
                intakeEntity.setMetric(cellsList.get(9).getDisplayValue());
                intakeEntity.setMetricDataType(cellsList.get(10).getDisplayValue());

                //use .getValue() for variant input types
                intakeEntity.setBudget(cellsList.get(11).getValue());
                intakeEntity.setStretch(cellsList.get(12).getValue());
                intakeEntity.setStatus(cellsList.get(14).getDisplayValue());
                intakeEntity.setDepartment1(cellsList.get(27).getDisplayValue());
                intakeEntity.setDepartment2(cellsList.get(28).getDisplayValue());
                intakeEntity.setDepartment3(cellsList.get(29).getDisplayValue());
                intakeEntity.setDepartment4(cellsList.get(30).getDisplayValue());
                intakeEntity.setDepartment5(cellsList.get(31).getDisplayValue());

                intakeEntity.replaceNullsWithEmptyStrings(intakeEntity);

                intakeList.add(intakeEntity);
            }

            //delete rows from intake sheet
            Set<Long> rowIds = new HashSet<>();

            for (Row row : rowList) {
                rowIds.add(row.getId());
            }
            smartsheet.sheetResources().rowResources().deleteRows(2847324411062148L, rowIds, true);

            //run other method and populate correct sheet
            populateProjects(intakeList, smartsheet);
        }
    }

    private static void populateProjects(List<IntakeEntity> intakeEntityList, Smartsheet smartsheet) throws SmartsheetException {
        for (IntakeEntity entity : intakeEntityList) {

            //get sheet based on division name and get all rows
            Sheet projectSheet = smartsheet.sheetResources().getSheet(getSheetIdFromDivisionString(entity.getDivision()), null, null, null, null, null, null, null);
            PagedResult<Column> columns = smartsheet.sheetResources().columnResources().listColumns(getSheetIdFromDivisionString(entity.getDivision()),null, null);

            List<Column> columnList = columns.getData();
            List<Row> rows = projectSheet.getRows();

            //find business imperative in sheet
            boolean imperativeExists = false;

            if (entity.getDivision().equals("Other")) {
                imperativeExists = true;
            } else {
                for (Row row : rows) {

                    //handle potential NullPointerException
                    String displayValue = row.getCells().get(4).getDisplayValue();
                    if (displayValue == null) {
                        displayValue = "";
                    }

                    if (displayValue.equals(entity.getBusinessImperative())) {
                        imperativeExists = true;
                        break;
                    }
                }
            }

            if (!imperativeExists) {
                //send error email
                SMTPService.imperativeErrorEmail(entity.getCreatedByEmail(), entity.getProject());
            } else {
                //search for execution plan
                boolean executionPlanExists = false;

                if (entity.getDivision().equals("Other")) {
                    executionPlanExists = true;

                    //insert row at bottom of sheet and populate
                    Row newRow = new Row();
                    newRow.setToBottom(true);

                    List<Cell> cells = new ArrayList<>();

                    cells.add(new Cell(columnList.get(2).getId()).setValue(entity.getCreatedByEmail()));
                    cells.add(new Cell(columnList.get(4).getId()).setValue(entity.getBusinessImperative()));
                    cells.add(new Cell(columnList.get(5).getId()).setValue(entity.getExecutionPlan()));
                    cells.add(new Cell(columnList.get(6).getId()).setValue(entity.getProject()));
                    cells.add(new Cell(columnList.get(7).getId()).setValue(entity.getAccountable()));
                    cells.add(new Cell(columnList.get(8).getId()).setValue(entity.getPlant()));
                    cells.add(new Cell(columnList.get(9).getId()).setValue(entity.getMetric()));
                    cells.add(new Cell(columnList.get(10).getId()).setValue(entity.getMetricDataType()));


                    //add currency cell formatting
                    if (entity.getMetricDataType().equals("$")) {
                        Cell actual = new Cell(columnList.get(13).getId());
                        actual.setValue(0);

                        Format.FormatBuilder builder = new Format.FormatBuilder();
                        builder.withCurrency(Currency.USD).withThousandsSeparator(ThousandsSeparator.ON).withNumberFormat(NumberFormat.CURRENCY);

                        actual.setFormat(builder.build());
                        cells.add(actual);

                        cells.add(new Cell(columnList.get(11).getId()).setValue(entity.getBudget()).setFormat(builder.build()));
                        cells.add(new Cell(columnList.get(12).getId()).setValue(entity.getStretch()).setFormat(builder.build()));

                        for (int x = 15; x <= 26; x++) {
                            Cell monthCell = new Cell(columnList.get(x).getId());
                            monthCell.setValue(0);
                            monthCell.setFormat(builder.build());
                            cells.add(monthCell);
                        }
                    } else if (entity.getMetricDataType().equals("Numeric")) {
                        Cell actual = new Cell(columnList.get(13).getId());
                        actual.setValue(0);

                        Format.FormatBuilder builder = new Format.FormatBuilder();
                        builder.withThousandsSeparator(ThousandsSeparator.ON);

                        actual.setFormat(builder.build());
                        cells.add(actual);

                        cells.add(new Cell(columnList.get(11).getId()).setValue(entity.getBudget()).setFormat(builder.build()));
                        cells.add(new Cell(columnList.get(12).getId()).setValue(entity.getStretch()).setFormat(builder.build()));

                        for (int x = 15; x <= 26; x++) {
                            Cell monthCell = new Cell(columnList.get(x).getId());
                            monthCell.setValue(0);
                            monthCell.setFormat(builder.build());
                            cells.add(monthCell);
                        }
                    } else {
                        cells.add(new Cell(columnList.get(11).getId()).setValue(entity.getBudget()));
                        cells.add(new Cell(columnList.get(12).getId()).setValue(entity.getStretch()));
                    }

                    cells.add(new Cell(columnList.get(14).getId()).setValue(entity.getStatus()));
                    cells.add(new Cell(columnList.get(27).getId()).setValue(entity.getDepartment1()));
                    cells.add(new Cell(columnList.get(28).getId()).setValue(entity.getDepartment2()));
                    cells.add(new Cell(columnList.get(29).getId()).setValue(entity.getDepartment3()));
                    cells.add(new Cell(columnList.get(30).getId()).setValue(entity.getDepartment4()));
                    cells.add(new Cell(columnList.get(31).getId()).setValue(entity.getDepartment5()));

                    newRow.setCells(cells);
                    smartsheet.sheetResources().rowResources().addRows(getSheetIdFromDivisionString(entity.getDivision()), Arrays.asList(newRow));

                    //after row has been added, go find row again to fetch rowNumber to use in setting formula for numeric metric types
                    if (entity.getMetricDataType().equals("$") || entity.getMetricDataType().equals("Numeric")) {

                        //refresh sheet and row reference to capture newly added row
                        projectSheet = smartsheet.sheetResources().getSheet(getSheetIdFromDivisionString(entity.getDivision()), null, null, null, null, null, null,null);
                        List<Row> updatedRowList = projectSheet.getRows();

                        for (Row updatedRow : updatedRowList) {
                            List<Cell> list = updatedRow.getCells();

                            if (list.get(6).getDisplayValue().equals(entity.getProject())) {
                                int rowNumber = updatedRow.getRowNumber();
                                long rowId = updatedRow.getId();


                                Cell formulaCell = new Cell(columnList.get(13).getId());
                                formulaCell.setFormula("=SUM(April" + rowNumber + ":March" + rowNumber + ")");

                                Row newRow2 = new Row(rowId);
                                newRow2.setCells(Arrays.asList(formulaCell));
                                smartsheet.sheetResources().rowResources().updateRows(getSheetIdFromDivisionString(entity.getDivision()), Arrays.asList(newRow2));
                                break;
                            }
                        }
                    }

                    //send success email
                    SMTPService.successEmail(entity.getCreatedByEmail(), entity.getProject());

                } else {
                    for (Row row : rows) {

                        //handle potential NullPointerException
                        String executionPlanString = row.getCells().get(5).getDisplayValue();
                        String imperativeString = row.getCells().get(4).getDisplayValue();

                        if (executionPlanString == null) {
                            executionPlanString = "";
                        }

                        if (imperativeString == null) {
                            imperativeString = "";
                        }

                        if (executionPlanString.equals(entity.getExecutionPlan()) && imperativeString.equals(entity.getBusinessImperative())) {
                            executionPlanExists = true;

                            //insert row and populate project
                            Row newRow = new Row();
                            newRow.setParentId(row.getId());
                            newRow.setToBottom(true);

                            List<Cell> cells = new ArrayList<>();

                            cells.add(new Cell(columnList.get(2).getId()).setValue(entity.getCreatedByEmail()));
                            cells.add(new Cell(columnList.get(4).getId()).setValue(entity.getBusinessImperative()));
                            cells.add(new Cell(columnList.get(6).getId()).setValue(entity.getProject()));
                            cells.add(new Cell(columnList.get(7).getId()).setValue(entity.getAccountable()));
                            cells.add(new Cell(columnList.get(8).getId()).setValue(entity.getPlant()));
                            cells.add(new Cell(columnList.get(9).getId()).setValue(entity.getMetric()));
                            cells.add(new Cell(columnList.get(10).getId()).setValue(entity.getMetricDataType()));


                            //add currency cell formatting
                            if (entity.getMetricDataType().equals("$")) {
                                Cell actual = new Cell(columnList.get(13).getId());
                                actual.setValue(0);

                                Format.FormatBuilder builder = new Format.FormatBuilder();
                                builder.withCurrency(Currency.USD).withThousandsSeparator(ThousandsSeparator.ON).withNumberFormat(NumberFormat.CURRENCY);

                                actual.setFormat(builder.build());
                                cells.add(actual);

                                cells.add(new Cell(columnList.get(11).getId()).setValue(entity.getBudget()).setFormat(builder.build()));
                                cells.add(new Cell(columnList.get(12).getId()).setValue(entity.getStretch()).setFormat(builder.build()));

                                for (int x = 15; x <= 26; x++) {
                                    Cell monthCell = new Cell(columnList.get(x).getId());
                                    monthCell.setValue(0);
                                    monthCell.setFormat(builder.build());
                                    cells.add(monthCell);
                                }
                            } else if (entity.getMetricDataType().equals("Numeric")) {
                                Cell actual = new Cell(columnList.get(13).getId());
                                actual.setValue(0);

                                Format.FormatBuilder builder = new Format.FormatBuilder();
                                builder.withThousandsSeparator(ThousandsSeparator.ON);

                                actual.setFormat(builder.build());
                                cells.add(actual);

                                cells.add(new Cell(columnList.get(11).getId()).setValue(entity.getBudget()).setFormat(builder.build()));
                                cells.add(new Cell(columnList.get(12).getId()).setValue(entity.getStretch()).setFormat(builder.build()));

                                for (int x = 15; x <= 26; x++) {
                                    Cell monthCell = new Cell(columnList.get(x).getId());
                                    monthCell.setValue(0);
                                    monthCell.setFormat(builder.build());
                                    cells.add(monthCell);
                                }
                            } else {
                                cells.add(new Cell(columnList.get(11).getId()).setValue(entity.getBudget()));
                                cells.add(new Cell(columnList.get(12).getId()).setValue(entity.getStretch()));
                            }

                            cells.add(new Cell(columnList.get(14).getId()).setValue(entity.getStatus()));
                            cells.add(new Cell(columnList.get(27).getId()).setValue(entity.getDepartment1()));
                            cells.add(new Cell(columnList.get(28).getId()).setValue(entity.getDepartment2()));
                            cells.add(new Cell(columnList.get(29).getId()).setValue(entity.getDepartment3()));
                            cells.add(new Cell(columnList.get(30).getId()).setValue(entity.getDepartment4()));
                            cells.add(new Cell(columnList.get(31).getId()).setValue(entity.getDepartment5()));

                            newRow.setCells(cells);
                            smartsheet.sheetResources().rowResources().addRows(getSheetIdFromDivisionString(entity.getDivision()), Arrays.asList(newRow));

                            //after row has been added, go find row again to fetch rowNumber to use in setting formula for numeric metric types
                            if (entity.getMetricDataType().equals("$") || entity.getMetricDataType().equals("Numeric")) {

                                //refresh sheet and row reference to capture newly added row
                                projectSheet = smartsheet.sheetResources().getSheet(getSheetIdFromDivisionString(entity.getDivision()), null, null, null, null, null, null,null);
                                List<Row> updatedRowList = projectSheet.getRows();

                                for (Row updatedRow : updatedRowList) {
                                    List<Cell> list = updatedRow.getCells();

                                    //handle potential nullpointerexception
                                    String projectDisplayedValue = list.get(6).getDisplayValue();
                                    if (projectDisplayedValue == null) {
                                        projectDisplayedValue = "";
                                    }

                                    if (projectDisplayedValue.equals(entity.getProject())) {
                                        int rowNumber = updatedRow.getRowNumber();
                                        long rowId = updatedRow.getId();


                                        Cell formulaCell = new Cell(columnList.get(13).getId());
                                        formulaCell.setFormula("=SUM(April" + rowNumber + ":March" + rowNumber + ")");

                                        Row newRow2 = new Row(rowId);
                                        newRow2.setCells(Arrays.asList(formulaCell));
                                        smartsheet.sheetResources().rowResources().updateRows(getSheetIdFromDivisionString(entity.getDivision()), Arrays.asList(newRow2));
                                        break;
                                    }
                                }
                            }

                            //send success email
                            SMTPService.successEmail(entity.getCreatedByEmail(), entity.getProject());
                        }
                    }
                }

                if (!executionPlanExists) {
                    //send error email
                    SMTPService.executionPlanErrorEmail(entity.getCreatedByEmail(), entity.getProject());
                }
            }
        }
    }

    private static long getSheetIdFromDivisionString(String division) {
        long returnValue;

        switch (division) {
            case "CP":
                returnValue = 1856143669651332L;
                break;
            case "ID":
                returnValue = 5168938918143876L;
                break;
            case "Other":
                returnValue = 7053693511067524L;
                break;
            default:
                returnValue = 0;
                break;
        }
        return returnValue;
    }
}