
/**
 * I have neither given nor received unauthorized aid on this assignment.
 * Thank you to .
* Majestic King Dylan
*/
import java.util.*;
import java.util.zip.DeflaterOutputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Git {

    public static final boolean COMPRESS = false;

    public Git () {
        initGit();
    }
    public void initGit() { //makes repo
        File gitDir = new File("git");
        File objDir = new File( "./git/objects/");
        File indexFile = new File( "./git/index");
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
    //filePath is the name/path of the file that will be 'Blobbed'
    public void addBlob(String filePath) throws NoSuchAlgorithmException { 
        File file = new File(filePath);
        if (!file.exists()) {
            throw new NullPointerException();
        }
        if (COMPRESS) {
            file = compress(file);
        }
        //checks if file is stored already
        File storingFile = new File ("./git/objects/" + genSha1(filePath));
        //put actual file in obj directory is it isnt already there
        if (!storingFile.exists()) {
            try{
                storingFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(storingFile, true));
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                while (br.ready()) {
                    bw.write(br.readLine());
                    bw.newLine();
                }
                br.close();
                bw.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
            //adding to index
            try {
                //checking if alr in index file
                boolean inIndex = false;
                String index = genSha1(filePath) + " " + file.getName();
                BufferedReader br = new BufferedReader(new FileReader("./git/index"));
                while(br.ready()) {
                    if (index.equals(br.readLine())) {
                        inIndex = true;
                    }
                }
                br.close();
                //adding if not in index file
                if (!inIndex) {
                    BufferedWriter bw = new BufferedWriter(new FileWriter("./git/index", true));
                    bw.write(index);
                    bw.newLine();
                    bw.close();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    public File compress (File file) {
        try {
            File compressedFile = File.createTempFile("compress",null);
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
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    //deletes git folder (everything basically)
    public void resetGit () {
        File fodder = new File ("git/");
        if (fodder.exists()) {
            deleteDir(fodder);
            fodder.delete();
        }
    }

    //deletes directories recursively (gets rid of the subfiles too)
    public void deleteDir(File dir) {
        if (!dir.isDirectory()) {
            if (dir.isFile()) 
                dir.delete();
            else 
                throw new IllegalArgumentException();
        }
        if (dir.exists()) {
            for (File subfile:dir.listFiles()) {
                deleteDir(subfile);
            }
            dir.delete(); 
        }
    }


    //returns Sha1 hash (hexadecimal index) for input file
    //copied off internet (forgot where exactly)
    public static String genSha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
