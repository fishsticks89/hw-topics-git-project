
/**
 * I have neither given nor received unauthorized aid on this assignment.
 * Thank you to .
* Majestic King Dylan
*/
import java.util.zip.DeflaterOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Git {

    public static final boolean COMPRESS = false;

    public Git() {
        initGit();
    }

    public void initGit() { // makes repo
        File gitDir = new File("git");
        File objDir = new File("./git/objects/");
        File indexFile = new File("./git/index");
        // checks if repo alr exists
        if (!gitDir.exists()) {
            gitDir.mkdir();
        }
        if (!objDir.exists()) {
            objDir.mkdir();
        }
        if (!indexFile.exists()) {
            try {
                indexFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addDir(String dirPath) throws NoSuchAlgorithmException {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new Error("This is not an existing directory");
        }
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addDir(file.getPath());
            } else {
                addBlob(file.getPath());
            }
        }
    }

    public void addToTree(String treePath, String sha, String name, boolean isBlob) {
        final var dir = "./git/objects/" + treePath;

        if (!Files.exists(Path.of(dir))) {
            throw new Error("The tree path does not exist");
        }

        try {
            var treeFile = new File(dir + "/tree");
            if (dir.equals("./git/objects/")) {
                treeFile = new File("./git/index");
            }
            if (!treeFile.exists()) {
                treeFile.createNewFile();
            }

            final var br = new BufferedReader(new FileReader(treeFile));
            final var bw = new BufferedWriter(new FileWriter(treeFile, true));

            final var lineItem = (isBlob ? "blob " : "tree ") + sha + " " + name;

            if (br.readLine() == null) {
                bw.write(lineItem);
            } else {
                bw.newLine();
                bw.write(lineItem);
            }

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (treePath.equals("")) {
            return;
        }
        addToTree(upDir(treePath), shaFile("./git/objects/" + treePath + "/tree"),
                lastDir(treePath), false);
    }

    String upDir(String dir) {
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

    String lastDir(String dir) {
        final var split = dir.split("/");
        return split[split.length - 1];
    }

    // filePath is the name/path of the file that will be 'Blobbed'
    public void addBlob(String filePath) throws NoSuchAlgorithmException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new NullPointerException();
        }
        if (COMPRESS) {
            file = compress(file);
        }
        // checks if file is stored already
        File storingFile = new File("./git/objects/" + shaFile(filePath));
        if (!upDir(filePath).equals("")) {
            storingFile = new File("./git/objects/" + upDir(filePath) + "/" + shaFile(filePath));
        }
        try {
            Files.createDirectories(Paths.get(storingFile.getParent()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new Error("Could not create directories");
        }
        // put actual file in obj directory is it isnt already there
        if (!storingFile.exists()) {
            try {
                // todo tree path make exists
                storingFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(storingFile, true));
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                while (br.ready()) {
                    bw.write(br.readLine());
                    bw.newLine();
                }
                br.close();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        addToTree(upDir(filePath), shaFile(filePath), Paths.get(filePath).getFileName().toString(), true);
    }

    public File compress(File file) {
        try {
            File compressedFile = File.createTempFile("compress", null);
            FileInputStream input = new FileInputStream(file);
            DeflaterOutputStream output = new DeflaterOutputStream(new FileOutputStream(compressedFile));
            int data = input.read();
            while (data != -1) {
                output.write(data);
                data = input.read();
            }
            input.close();
            output.close();
            return compressedFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // deletes git folder (everything basically)
    public static void resetGit() {
        File fodder = new File("git/");
        if (fodder.exists()) {
            deleteDir(fodder);
            fodder.delete();
        }
    }

    // deletes directories recursively (gets rid of the subfiles too)
    public static void deleteDir(File dir) {
        if (!dir.isDirectory()) {
            if (dir.isFile())
                dir.delete();
            else
                throw new IllegalArgumentException();
        }
        if (dir.exists()) {
            for (File subfile : dir.listFiles()) {
                deleteDir(subfile);
            }
            dir.delete();
        }
    }

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
            e.printStackTrace();
            throw new Error("File not found");
        }
    }
}
