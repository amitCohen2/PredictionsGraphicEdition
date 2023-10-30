package design.body.execution.business;
public class HistogramsUtils {
    public static void sleepForAWhile(long sleepTime) {
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }

    public static void log(String message) {
        System.out.println(message);
    }
}