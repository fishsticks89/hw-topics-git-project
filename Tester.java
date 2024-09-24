import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Tester {
    public static void main (String [] args) throws NoSuchAlgorithmException {
        Git gitrepo = new Git();
        // git.resetGit();
        // git.initGit();
        // //git.addBlob("testerjunk.txt");
        // git.addBlob("testerjunk2.txt");
        // //git.deleteDir(new File("./git/objects"));

        //checking repo initializing
        File git = new File ("./git/");
        File objects = new File ("./git/objects/");
        File index = new File ("./git/index");
        if (git.exists())
            System.out.println ("git created");
        else
            System.out.println ("git not created ");
        if (objects.exists())
            System.out.println ("objects created");
        else
            System.out.println ("objects not created");
        if (index.exists())
            System.out.println ("index created");
        else
            System.out.println ("index not created");

        //check sha1 hashing
        File file = new File ("testerjunk2.txt");
        if (Git.genSha1(file.getName()).equals("6b3b4c3312922dc20b7c51c9c6165c973adb4e56")) {
            System.out.println ("hash worked");
        }
        else {
            System.out.println ("hash didn't work");
        }
        //check hash
        gitrepo.addBlob("testerjunk2.txt");
        boolean inIndex = false;
        String hash;
        //finds compressed hashcode if nessesary
        if (gitrepo.COMPRESS)
            hash = gitrepo.genSha1(gitrepo.compress(file).getName());
        else
            hash = gitrepo.genSha1(file.getName());
        
        //checks to see if in index
        String expectedIndex = (hash + " " + file.getName());
        try {
            BufferedReader br = new BufferedReader(new FileReader("./git/index"));
            while (br.ready()){
                if (expectedIndex.equals(br.readLine()))
                    inIndex = true;
            }
            br.close();
        }
        catch (IOException e){
            e.printStackTrace();;
        }
        if (inIndex)
            System.out.println("indexing for blob works");
        else
            System.out.println("blob indexing failed");
        
        //check to see if in objects
        File blob = new File("./git/objects/" + hash);
        if (blob.exists())
            System.out.println ("blob created");
        else
            System.out.println ("blob not created");

        //leave true if you want repository to reset at end of test
        if (false){
            gitrepo.resetGit();
    }
}
}
