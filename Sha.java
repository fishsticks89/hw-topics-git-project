import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Sha {
    // returns Sha1 hash (hexadecimal index) for input file
    // copied off internet (forgot where exactly)
    public static String genSha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String shaFile(String path) {
        // used bufferedReader to read file
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuffer sb = new StringBuffer();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            return genSha1(sb.toString());
        } catch (Exception e) {
            System.err.println(path);
            e.printStackTrace();
            throw new Error("File not found");
        }
    }
}
