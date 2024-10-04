package util;

public class Terminate {
    public static void exception(Exception e) {
        System.err.println(e);
        e.printStackTrace();
        System.exit(0);
    }
}
