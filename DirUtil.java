import java.io.File;

public class DirUtil {
    static String removeBeginningSlash(String dir) {
        if (dir.length() == 0)
            return dir;
        if (dir.charAt(0) == '/') {
            return dir.substring(1);
        }
        return dir;
    }

    static String up(String dir) {
        dir = removeBeginningSlash(dir);
        final var split = dir.split("/");
        StringBuffer combined = new StringBuffer();
        for (int i = 0; i < split.length - 1; i++) {
            combined.append(split[i]);
            if (i != split.length - 2) {
                combined.append("/");
            }
        }
        return combined.toString();
    }

    static String last(String dir) {
        final var split = dir.split("/");
        return split[split.length - 1];
    }

    // deletes directories recursively (gets rid of the subfiles too)
    public static void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

}
