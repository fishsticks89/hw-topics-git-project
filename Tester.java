import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.security.NoSuchAlgorithmException;

public class Tester {
    static void assertRepoCreated() {
        File git = new File("./git/");
        File objects = new File("./git/objects/");
        File index = new File("./git/index");
        if (git.exists())
            System.out.println("git created");
        else
            System.out.println("git not created ");
        if (objects.exists())
            System.out.println("objects created");
        else
            System.out.println("objects not created");
        if (index.exists())
            System.out.println("index created");
        else
            System.out.println("index not created");
    }

    static void testHashing() throws NoSuchAlgorithmException {
        File file = new File("testerjunk2.txt");
        if (Sha.genSha1(file.getName()).equals("6b3b4c3312922dc20b7c51c9c6165c973adb4e56")) {
            System.out.println("hash worked");
        } else {
            System.out.println("hash didn't work");
        }
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        testHashing();

        // leave if you want repository to reset at end of test
        Git.resetGit();
        
        File testing = new File("basd/test/ouch");
        if (testing.exists())
            testing.delete();
        File testingAgain = new File("basd/test/test1/test2/test3/test4/test5/test6/test7/test8/test9/deepFile.txt");
        if (testingAgain.exists()){
            File testingAgainSuperGreatGrandParent = new File("basd/test/test1");
            DirUtil.deleteDir(testingAgainSuperGreatGrandParent);
        }
        
        Git repo = new Git();
        assertRepoCreated();

        // check hash
        repo.stage("basd");

        repo.commit("commited most testing files", "milomessinger");

        try {
            testing.createNewFile();
        }
        catch (IOException e) {e.printStackTrace();}

        repo.stage("basd/test/ouch");
        repo.commit("added file in basd/test/ouch", "milomessinger");

        File testingAgainParent = new File("basd/test/test1/test2/test3/test4/test5/test6/test7/test8/test9/");
        testingAgainParent.mkdirs();
        try {
            testingAgain.createNewFile();
            FileWriter writer = new FileWriter(testingAgain);
            writer.append("the deep");
            writer.close();
        } catch (IOException e) {e.printStackTrace();}

        repo.stage("basd/test/test1/test2/test3/test4/test5/test6/test7/test8/test9/deepFile.txt");
        repo.commit("added DeepFile", "milomessinger");

        // repo.checkout("");

        // try{
        //     BufferedReader reader = new BufferedReader(new FileReader("git/HEAD"));
        //     repo.checkout(reader.readLine());
        //     reader.close();
        // }
        // catch (Exception e) {e.printStackTrace();}
    }
}
