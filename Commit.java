import java.io.BufferedWriter;
import java.io.File;
import java.nio.Buffer;
import java.util.Calendar;

public class Commit {
    public void commitRepo (){
        commitRepo("");
    }
    public void commitRepo(String commitMessage){
        File commitTemp = File.createTempFile("currentCommit", null);
        BufferedWriter writer = new BufferedWriter(new FileWriter(commitTemp));
        
        //commit current tree;
        writer.append("tree: ");
        //tree functionality not present in michale code B)
        //will code later once it works
        writer.newLine();
        
        //add parent to commit
        String parentHash = Files.readString(new Path("./git/HEAD"));
        writer.append("parent: " + parentHash);
        writer.newLine();

        //add author
        writer.append("author: " + System.getProperty("user.name"));
        writer.newLine();

        //add date
        writer.append("date: " + java.time.LocalDate.now());
        writer.newLine();

        //author message
        writer.append("message: " + commitMessage);
        writer.close();

        Git.addBlob(commitTemp);

        BufferedWriter HEADwriter = new BufferedWriter(FileWriter("./git/HEAD"));
        HEADwriter.append(Sha.shaFile(commitTemp));

    }
}
