package ccwc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: ccwc (-c | -l) <filename>");
            System.exit(1);
        }

        String flag = args[0];
        String fileName = args[1];
        Path path = Paths.get(fileName);

        switch (flag) {
            case "-c":
                System.out.println(countBytes(path) + " " + fileName);
                break;
            case "-l":
                System.out.println(countLines(path) + " " + fileName);
                break;
            case "-w":
                System.out.println(countWords(path) + " " + fileName);
                break;
            default:
                System.err.println("Unknown flag: " + flag);
                System.exit(1);
        }
    }

    /**
     * Counts the number of bytes in a file by streaming it through a buffer,
     * without ever loading the whole file into memory
     * */
    private static long countBytes(Path path) throws IOException {
        long count = 0;

        try (InputStream in = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while((bytesRead = in.read(buffer)) != -1) {
                count += bytesRead;
            }
        }
        return count;
    }

    /**
     * Counts newline characters by streaming decoded characters through a BufferedReader
     * */
    private static long countLines(Path path) throws IOException {
        long count = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8))) {
            int ch;
            while ((ch = reader.read()) != -1) {
                if (ch == '\n') {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Counts words by streaming decoded characters and detecting transitions
     * from whitespace to non-whitespace.
     * */
    private static long countWords(Path path) throws IOException {
        long count = 0;
        boolean inWord = false;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8))) {
            int ch;
            while ((ch = reader.read()) != -1) {
               if (Character.isWhitespace(ch)) {
                   inWord = false;
               } else if (!inWord) {
                   count++;
                   inWord = true;
               }
            }
        }
        return count;
    }
}
