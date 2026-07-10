package ccwc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Accumulates byte, line, word, and character counts for a file.
 * A single pass is made over the file and all requested counts are
 * collected concurrently.
 */
public class Counter {
    /** Number of lines counted in the file. */
    public long lines = 0;
    /** Number of words counted in the file. */
    public long words = 0;
    /** Number of bytes counted in the file. */
    public long bytes = 0;
    /** Number of characters counted in the file. */
    public long chars = 0;

    /**
     * Counts the specified metrics from the given file. Which metrics
     * are collected is controlled by the flags set on {@code opts}.
     * The byte count is obtained directly from the file system; the
     * remaining metrics are gathered by streaming UTF-8 decoded
     * characters through a {@code BufferedReader}.
     *
     * @param path the path to the file to count
     * @param opts specifies which counters to enable
     * @throws IOException if an I/O error occurs
     */
    public void count(Path path, Options opts) throws IOException {
        lines = words = bytes = chars = 0;

        if (opts.countBytes) {
            bytes = Files.size(path);
        }

        boolean needCharStream = opts.countLines || opts.countWords || opts.countChars;
        if (needCharStream) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8))) {

                boolean inWord = false;
                int ch;
                while((ch = reader.read())!= -1) {
                    if (opts.countChars) {
                        chars++;
                    }

                    if (opts.countLines && ch == '\n') {
                        lines++;
                    }

                    if (opts.countWords) {
                        if (Character.isWhitespace(ch)) {
                            inWord = false;
                        } else if (!inWord) {
                            words++;
                            inWord = true;
                        }
                    }
                }
            }
        }
    }
}
