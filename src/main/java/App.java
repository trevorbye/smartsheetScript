import com.smartsheet.api.SmartsheetException;

public class App {
    public static void main(String[] args) {

        try {
            SmartsheetScript.buildSmartsheet();
        } catch (SmartsheetException e) {
            SMTPService.appErrorEmail("trevor.bye@darigold.com", e.getMessage());
        }


    }
}
