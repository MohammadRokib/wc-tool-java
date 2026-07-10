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

    /**
     * Entry point for the ccwc word count utility. Parses command-line arguments
     * and delegates to the {@link Counter} class to perform the requested counts.
     *
     * @param args command-line arguments, expected to contain a flag and a filename
     * @throws IOException if an I/O error occurs reading the file
     */
    public static void main(String[] args) throws IOException {
        Options opts = Options.parse(args);

        if (opts.fileName == null) {
            System.err.println("Usage: ccwc [-c] [-l] [-w] [-m] <filename>");
            System.exit(1);
        }

        Path path = Paths.get(opts.fileName);
        Counter counter = new Counter();
        counter.count(path, opts);

        if (opts.countBytes && opts.countLines && opts.countWords && !opts.countChars) {
            System.out.printf("%8d %8d %8d %s\n", counter.lines, counter.words, counter.bytes, opts.fileName);
        } else {
            if (opts.countBytes) {
                System.out.println(counter.bytes + " " + opts.fileName);
            }
            if (opts.countLines) {
                System.out.println(counter.lines + " " + opts.fileName);
            }
            if (opts.countWords) {
                System.out.println(counter.words + " " + opts.fileName);
            }
            if (opts.countChars) {
                System.out.println(counter.chars + " " + opts.fileName);
            }
        }
    }
}
