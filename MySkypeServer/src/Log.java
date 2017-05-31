public class Log {
    private static String log = "";

    public static void add(String log) {
        Log.log += log + "\n";
    }

    public static String get() {
        return Log.log;
    }
}
