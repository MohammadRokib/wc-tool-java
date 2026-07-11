package ccwc;

import java.io.IOException;
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
        Counter counter = new Counter();

        if (opts.fileName == null) {
            counter.count(System.in, opts);
            printResults(counter, opts, null);
        } else {
            Path path = Paths.get(opts.fileName);
            counter.count(path, opts);
            printResults(counter, opts, opts.fileName);
        }
    }

    /**
     * Prints the results from the counter to standard output. When all of
     * {@code -c}, {@code -l}, {@code -w} are active and {@code -m} is not,
     * the output is formatted as {@code "%8d %8d %8d [filename]"} matching
     * the classic {@code wc} default format. Otherwise, each enabled metric
     * is printed on its own line.
     *
     * @param counter  the counter whose results to print
     * @param opts     the options indicating which metrics are enabled
     * @param fileName the file name to include in the output, or {@code null}
     *                 when reading from standard input
     */
    private static void printResults(Counter counter, Options opts, String fileName) {
        String suffix = fileName == null ? "" : fileName;

        if (opts.countBytes && opts.countLines && opts.countWords && !opts.countChars) {
            System.out.printf("%8d %8d %8d %s\n", counter.lines, counter.words, counter.bytes, suffix);
        } else {
            if (opts.countBytes) {
                System.out.println(counter.bytes + " " + suffix);
            }
            if (opts.countLines) {
                System.out.println(counter.lines + " " + suffix);
            }
            if (opts.countWords) {
                System.out.println(counter.words + " " + suffix);
            }
            if (opts.countChars) {
                System.out.println(counter.chars + " " + suffix);
            }
        }
    }
}
