import java.io.File;
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

        Git.initGit();
        assertRepoCreated();

        // check hash
        Git.stage("basd");

        Git.commit("BALLS");
    }
}
