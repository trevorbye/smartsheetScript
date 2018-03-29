import com.smartsheet.api.SmartsheetException;

public class App {
    public static void main(String[] args) {

        try {
            SmartsheetScript.buildSmartsheet();
        } catch (SmartsheetException e) {
            e.printStackTrace();
        }

    }
}
