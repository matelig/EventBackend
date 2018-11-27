package helpers;

public class DateHelper {
    public static Long getEpochTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
