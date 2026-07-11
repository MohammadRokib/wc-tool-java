package ccwc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
            try (InputStream in = Files.newInputStream(path)) {
                countFromStream(in, opts);
            }
        }
    }

    /**
     * Counts the specified metrics from the given input stream. Which
     * metrics are collected is controlled by the flags set on {@code opts}.
     * <p>
     * When only byte counting is requested the stream is read directly
     * in a raw byte loop. Otherwise, the stream is wrapped in a
     * {@link CountingInputStream} so that line, word, and character
     * counting can share the single read pass while bytes are still
     * accumulated.
     *
     * @param in   the input stream to read from
     * @param opts specifies which counters to enable
     * @throws IOException if an I/O error occurs
     */
    public void count(InputStream in, Options opts) throws IOException {
        lines = words = chars = bytes = 0;

        if (opts.countBytes && !opts.countLines && !opts.countWords && !opts.countChars) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while((bytesRead = in.read(buffer)) != -1) {
                bytes += bytesRead;
            }
            return;
        }

        CountingInputStream countingIn = new CountingInputStream(in);
        countFromStream(countingIn, opts);

        if (opts.countBytes) {
            bytes = countingIn.getBytesRead();
        }
    }

    /**
     * Reads UTF-8 decoded characters from the given input stream and
     * increments line, word, and character counters as requested by
     * {@code opts}. If a {@link CountingInputStream} is passed, its
     * byte counter is also accumulated into {@link #bytes}.
     *
     * @param in   the input stream to read from (may be a
     *             {@code CountingInputStream} for byte tracking)
     * @param opts specifies which counters to enable
     * @throws IOException if an I/O error occurs
     */
    private void countFromStream(InputStream in, Options opts) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {

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
