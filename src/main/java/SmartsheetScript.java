import com.smartsheet.api.Smartsheet;
import com.smartsheet.api.SmartsheetBuilder;
import com.smartsheet.api.SmartsheetException;

import com.smartsheet.api.models.Cell;
import com.smartsheet.api.models.Row;
import com.smartsheet.api.models.Sheet;

import java.util.List;

public class SmartsheetScript {

    public static void buildSmartsheet() throws SmartsheetException {
        String accessToken = "8u3h7u10lxlvz0q3pfmvk6jp5i";

        //connect to Smartsheet
        Smartsheet smartsheet = new SmartsheetBuilder().setAccessToken(accessToken).build();

        //get project intake sheet
        Sheet sheet = smartsheet.sheetResources().getSheet(2847324411062148L, null, null, null, null,null, null, null);
        List<Row> rowList = sheet.getRows();

        if (rowList.size() > 0) {
            for (Row row : rowList) {
                List<Cell> cellsList = row.getCells();

                for (Cell cell: cellsList) {

                }
            }
        }
    }




}
