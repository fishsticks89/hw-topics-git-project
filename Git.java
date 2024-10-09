
/**
 * I have neither given nor received unauthorized aid on this assignment.
 * Thank you to .
 * Majestic King Dylan
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.NoSuchAlgorithmException;
import java.nio.file.Files;
import java.io.IOException;
import util.Terminate;

public class Git /**implements GitInterface*/{

    // Adds a Blob in `objects` to the `index` file
    private static void addBlobToIndex(String path, String sha) {
        var indexFile = new DirectoryFile("./git/index");
        var line = new DirectoryLine(false, sha, path);
        indexFile.addIfNotExist(line);
    }

    // Adds the blob to the tree, then rehashes it
    // and renames the tree to it's new hash
    private static void registerBlob(String blobPath, String blobHash) {
        addBlobToIndex(blobPath, blobHash);

        var index = getIndex();
        var treePath = DirUtil.up(blobPath);

        System.out.println("treepath: \"" + treePath +"\"");

        if (treePath.equals("")) // don't need a tree if blob is in root
            return;

        var treeIndex = index.lineWithNameAndType(treePath, false);
        String treeSha;
        if (treeIndex == -1) {
            // create new tree
            File newTree = new File("./git/objects/new_tree");
            try {
                if (newTree.exists())
                    newTree.delete();
                newTree.createNewFile();
            } catch (Exception e) {
                Terminate.exception(e);
            }
            treeSha = "new_tree";
        } else {
            var treeLine = index.getLine(treeIndex);
            treeSha = treeLine.hash;
        }
        DirectoryFile tree = new DirectoryFile("./git/objects/" + treeSha);
        int existingBlobLineIndex = tree.lineWithHash(blobHash);
        var blobLine = new DirectoryLine(false, blobHash, DirUtil.last(blobPath));
        if (existingBlobLineIndex != -1) {
            tree.setLine(existingBlobLineIndex, blobLine);
        } else {
            tree.addIfNotExist(blobLine);
        }
        registerTree(tree.getFile(), treePath);
    }

    // Renames the tree to it's new hash wherever it is referenced
    // Does the same for all the trees it changes
    private static void registerTree(File tree, String treePath) {
        var index = getIndex();

        // move the tree to it's new hash
        var newHash = Sha.shaFile(tree.getAbsolutePath());
        tree.renameTo(new File("./git/objects/" + newHash));

        // make sure the tree is inserted with the right hash in index
        var treeLineIndexInIndex = index.lineWithNameAndType(treePath, false);
        var idealTreeLineInIndex = new DirectoryLine(true, newHash, treePath);
        if (treeLineIndexInIndex == -1) {
            index.addIfNotExist(idealTreeLineInIndex);
        } else {
            index.setLine(treeLineIndexInIndex, idealTreeLineInIndex);
        }

        // if there's no parent tree, the tree is fully registered in just index
        if (DirUtil.up(treePath).equals(""))
            return; // no parent tree

        // find parent tree
        var parentTreeLine = index.lineWithNameAndType(DirUtil.up(treePath), false);
        String parentTreeSha;
        if (parentTreeLine == -1) {
            // create new tree
            File newTree = new File("./git/objects/new_tree");
            try {
                if (newTree.exists())
                    newTree.delete();
                newTree.createNewFile();
            } catch (Exception e) {
                Terminate.exception(e);
            }
            parentTreeSha = "new_tree";
        } else {
            var treeLine = index.getLine(parentTreeLine);
            parentTreeSha = treeLine.hash;
        }
        DirectoryFile parentTree = new DirectoryFile("./git/objects/" + parentTreeSha);
        var lineInParent = parentTree.lineWithNameAndType(treePath, false);
        var idealLineInParent = new DirectoryLine(true, newHash, DirUtil.last(treePath));
        if (lineInParent == -1) {
            parentTree.addIfNotExist(idealLineInParent);
        } else {
            parentTree.setLine(lineInParent, idealLineInParent);
        }
        registerTree(parentTree.getFile(), DirUtil.up(treePath));
    }

    //adds a file to git
    //filePath denotes path of file to store
    public static void addBlob (String filePath){
        addBlob(new File(filePath));
    }

    // Adds a file to Git
    public static void addBlob(File inFile) {
        if (!inFile.isFile())
            throw new Error("Blob does not exist");

        File storingFile = new File("./git/objects/" + Sha.shaFile(inFile.getPath()));
        storingFile.delete();

        registerBlob(inFile.getPath(), storingFile.getName());

        try {
            storingFile.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(storingFile, true));
            BufferedReader br = new BufferedReader(new FileReader(inFile));
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

    // Adds all blobs in a directory
    // dirPath is path of directory to add
    public static void addDir(String dirPath) throws NoSuchAlgorithmException {
        addDir(new File(dirPath));
    }

    // Adds all the blobs in a directory
    public static void addDir(File dir) throws NoSuchAlgorithmException {

        if (!dir.exists() || !dir.isDirectory()) {
            throw new Error("This is not an existing directory");
        }

        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addDir(file.getPath()); // todo: is this a relative path
            } else {
                addBlob(file.getPath());
            }
        }
    }

    //
    public static void stage (String filePath){
        File file = new File(filePath);
        if (!file.exists())
            throw new Error("File does not exist");
        if (file.isDirectory()){
            try { 
                addDir(file); 
            }
            catch (NoSuchAlgorithmException e) { 
                e.printStackTrace(); 
            }
        }
        else if (file.isFile())
            addBlob(file);
    }

    //creates a commit with no commitMessage
    //records current system user and time automatically
    public static String commit(){
        return commit("", System.getProperty("user.name"));
    }

    //creates a commit with message commitMessage
    //records current system user and time automatically
    public static String commit(String commitMessage){ 
        return commit(commitMessage, System.getProperty("user.name"));
    }

    public static String commit (String commitMesage, String authorName){
        try {
            File commitTemp = File.createTempFile("currentCommit", null);
            BufferedWriter writer = new BufferedWriter(new FileWriter(commitTemp));

            // commit current tree;
            writer.append("tree: " + commitStagedFiles());
            writer.newLine();

            // add parent to commit
            String parentHash = Files.readString(new File("./git/HEAD").toPath());
            writer.append("parent: " + parentHash);
            writer.newLine();

            // add author
            writer.append("author: " + authorName);
            writer.newLine();

            // add date
            writer.append("date: " + java.time.LocalDate.now());
            writer.newLine();

            // author message
            writer.append("message: " + commitMesage);
            writer.close();

            String commitSHA = Sha.shaFile(commitTemp);
            //copy temp commit into objects folder
            File commitBlob = new File("./git/objects/" + commitSHA);
            BufferedReader reader = new BufferedReader(new FileReader(commitTemp));
            BufferedWriter commitWriter = new BufferedWriter(new FileWriter(commitBlob));
            while (reader.ready()){
                commitWriter.write(reader.readLine() + "\n");
            }
            reader.close();
            commitWriter.close();
            //write into head to track HEAD commit
            BufferedWriter HEADwriter = new BufferedWriter(new FileWriter("./git/HEAD"));
            HEADwriter.append(commitSHA);
            HEADwriter.close();
            return Sha.shaFile(commitTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Creates a listing of all staged files in the index.
    //Returns Sha of commited files listing
    public static String commitStagedFiles (){
        File index = new File("./git/index");
        String filesSHA = Sha.shaFile(index);
        File commitedFiles = new File("./git/objects/" + filesSHA);
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(index));
            BufferedWriter writer = new BufferedWriter(new FileWriter(commitedFiles));
            while (reader.ready()){
                writer.write(reader.readLine() + "\n");
            }
            reader.close();
            writer.close();

            //wipe the index 😈😈
            FileWriter indexWiper = new FileWriter(index, false);
            indexWiper.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return filesSHA;
    }

    //Returns working directory to state at commit
    public void checkout(String commitHash){
        //implementation not programmed yet
    }

    // Creates repository
    public static void initGit() {
        File gitDir = new File("git");
        File objDir = new File("./git/objects/");
        if (!gitDir.exists()) {
            gitDir.mkdir();
        }
        if (!objDir.exists()) {
            objDir.mkdir();
        }
        getIndex();
        getHEAD();
    }

    private static DirectoryFile getIndex() {
        return new DirectoryFile("./git/index");
    }

    private static DirectoryFile getHEAD(){
        return new DirectoryFile("./git/HEAD");
    }

    // Deletes `git` folder
    public static void resetGit() {
        File gitDir = new File("git/");
        if (gitDir.exists()) {
            DirUtil.deleteDir(gitDir);
            gitDir.delete();
        }
    }
}
