import org.simplejavamail.email.Email;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.simplejavamail.mailer.config.TransportStrategy;

public class SMTPService {

    private static Mailer connectToSMTP() {
        Mailer mailer = MailerBuilder
                .withSMTPServerHost("Appmail.darigold.com")
                .withSMTPServerPort(25)
                .withTransportStrategy(TransportStrategy.SMTP)
                .withDebugLogging(false)
                .buildMailer();

        return mailer;
    }

    public static void successEmail(String email, String projectName) {
        Mailer mailer = connectToSMTP();

        Email mailObject = EmailBuilder.startingBlank()
                .from("Darigold Analytics", "scanalytics@darigold.com")
                .to(email)
                .withSubject("Smartsheet AOP Project Input Success Notification")
                .withPlainText("This is an automated notification. Your project " + "\"" + projectName + "\"" + " was successfully added to the Darigold AOP project tracker.")
                .buildEmail();

        mailer.sendMail(mailObject);
    }

    public static void imperativeErrorEmail(String email, String projectName) {
        Mailer mailer = connectToSMTP();

        Email mailObject = EmailBuilder.startingBlank()
                .from("Darigold Analytics", "scanalytics@darigold.com")
                .to(email)
                .withSubject("Smartsheet AOP Project Input Error")
                .withPlainText("This is an automated error alert. Your project " + "\"" + projectName + "\"" + " was not accepted because your chosen Business Imperative does not exist in the selected Division. Please submit project again.")
                .buildEmail();

        mailer.sendMail(mailObject);
    }

    public static void appErrorEmail(String email, String errorDetails) {
        Mailer mailer = connectToSMTP();

        Email mailObject = EmailBuilder.startingBlank()
                .from("Darigold Analytics", "scanalytics@darigold.com")
                .to(email)
                .withSubject("Back-end Smartsheet AOP Application Error")
                .withPlainText(errorDetails)
                .buildEmail();

        mailer.sendMail(mailObject);
    }

    public static void executionPlanErrorEmail(String email, String projectName) {
        Mailer mailer = connectToSMTP();

        Email mailObject = EmailBuilder.startingBlank()
                .from("Darigold Analytics", "scanalytics@darigold.com")
                .to(email)
                .withSubject("Smartsheet AOP Project Input Error")
                .withPlainText("This is an automated error alert. Your project " + "\"" + projectName + "\"" + " was not accepted because your chosen Execution Plan does not exist under the selected imperative. Please submit project again.")
                .buildEmail();

        mailer.sendMail(mailObject);
    }

    public static void testEmail() {
        Mailer mailer = connectToSMTP();

        Email mailObject = EmailBuilder.startingBlank()
                .from("Darigold Analytics", "scanalytics@darigold.com")
                .to("aaron.burton@darigold.com")
                .withSubject("Smartsheet AOP Tracker Notification")
                .withPlainText("This is an automated notification from the Darigold Enterprise AOP tracker. Please consider updating your current projects:\n" +
                        "\n" +
                        "Project: \"Logistics Injury Performance\"  Status: On-track\n" +
                        "Project: \"Fleet Safety Performance\"  Status: On-track\n" +
                        "Project: \"Safety Committee Weekly Review\"  Status: Not started\n" +
                        "\n" +
                        "\n" +
                        "SC Report Link: https://app.smartsheet.com/b/home?lx=LH02hAyJH7-9xH9OPCg8lA\n\n" +
                        "OPS Report Link: https://app.smartsheet.com/b/home?lx=-03wyM1nu2DCfmtYKs_kJg\n\n" +
                        "NDA Report Link: https://app.smartsheet.com/b/home?lx=1Lh3R_GYKPHs20GLxRv7cw\n\n" +
                        "Legal Report Link: https://app.smartsheet.com/b/home?lx=lcHPjc75qA6If0IMf_9Ymg\n\n" +
                        "IT Report Link: https://app.smartsheet.com/b/home?lx=1K9-YBUa5R4OIyzQNgQ5OQ\n\n" +
                        "HR Report Link: https://app.smartsheet.com/b/home?lx=DXNzp7CHfD-fsbYB84TzRQ\n\n" +
                        "Finance Report Link: https://app.smartsheet.com/b/home?lx=HUCI_CbPC3Mx1bBLZHrWEA\n\n" +
                        "Commercial Report Link: https://app.smartsheet.com/b/home?lx=02rEAwPAfQ4aDSJu4tRxcw\n\n" +
                        "\n" +
                        "Project Intake Form Link: https://app.smartsheet.com/b/form/6ce7b7af63c941e9bfb1da52fb91077f")
                .buildEmail();

        mailer.sendMail(mailObject);
    }
}
