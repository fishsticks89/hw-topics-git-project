import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.util.ArrayList;

import util.Terminate;

import java.io.BufferedWriter;
import java.io.IOException;

class DirectoryLine {
    boolean isTree;
    String hash;
    String name;

    DirectoryLine(boolean isTree, String hash, String name) {
        this.isTree = isTree;
        this.hash = hash;
        this.name = name;
    }

    static DirectoryLine fromString(String line) {
        String[] parts = line.split(" ");
        return new DirectoryLine(parts[0].equals("tree"), parts[1], parts[2]);
    }

    boolean equals(DirectoryLine other) {
        return this.isTree == other.isTree && this.name.equals(other.name) && this.hash.equals(other.hash);
    }

    public String toString() {
        final var str = (isTree ? "tree " : "blob ") + hash + " " + name;
        if (!fromString(str).equals(this))
            throw new Error("String encoding failed");
        return str;
    }
}

public class DirectoryFile {
    private File file;

    public File getFile() {
        return file;
    }

    DirectoryFile(String path) {
        file = new File(path);
        if (!file.exists()) {
            DirectoryFile.generateDirectoryFile(path, new ArrayList<>());
        }
    }

    public int lineWithHash(String sha) {
        var lines = getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).hash.equals(sha)) {
                return i;
            }
        }
        return -1;
    }

    public void setLine(int index, DirectoryLine line) {
        var lines = getLines();
        lines.set(index, line);
        generateDirectoryFile(file.getPath(), lines);
    }

    public int findLine(DirectoryLine line) {
        var lines = getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(line))
                return i;
        }
        return -1;
    }

    public void addLine(DirectoryLine line) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            bw.write(line.toString());
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            Terminate.exception(e);
        }
    }

    public void addIfNotExist(DirectoryLine line) {
        if (findLine(line) == -1)
            addLine(line);
    }

    public int lineWithNameAndType(String name, boolean isBlob) {
        var lines = getLines();
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).name == name && lines.get(i).isTree == !isBlob) {
                return i;
            }
        }
        return -1;
    }

    static DirectoryFile generateDirectoryFile(String path, ArrayList<DirectoryLine> lines) {
        var file = new File(path);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (Exception e) {
            Terminate.exception(e);
        }
        DirectoryFile dir = new DirectoryFile(path);
        try {
            file.createNewFile();
            for (var l : lines)
                dir.addLine(l);
        } catch (IOException e) {
            Terminate.exception(e);
        }
        return dir;
    }

    public DirectoryLine getLine(int i) {
        return getLines().get(i);
    }

    public ArrayList<DirectoryLine> getLines() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            ArrayList<DirectoryLine> lines = new ArrayList<DirectoryLine>();
            while (br.ready()) {
                lines.add(DirectoryLine.fromString(br.readLine()));
            }
            br.close();
            return lines;
        } catch (Exception e) {
            Terminate.exception(e);
        }
        Terminate.exception(new Exception("How did we get here"));
        return null;
    }
}
