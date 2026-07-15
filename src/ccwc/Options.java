package ccwc;

/**
 * Parses and stores command-line flag options for the ccwc utility.
 * If no flag is specified, all metrics (bytes, lines, words, chars)
 * are enabled by default.
 */
public class Options {
    /** Whether to count bytes ({@code -c}). */
    public boolean countBytes = false;
    /** Whether to count lines ({@code -l}). */
    public boolean countLines = false;
    /** Whether to count words ({@code -w}). */
    public boolean countWords = false;
    /** Whether to count characters ({@code -m}). */
    public boolean countChars = false;
    /** The filename argument, or {@code null} if not provided. */
    public String fileName = null;

    /**
     * Parses the command-line arguments and returns an {@code Options}
     * instance with the appropriate flags set.
     * <p>
     * Recognized flags are {@code -c}, {@code -l}, {@code -w}, and
     * {@code -m}. The first non-flag argument is treated as the filename.
     * If no flags are specified, all four metrics are enabled.
     *
     * @param args the command-line arguments to parse
     * @return an {@code Options} instance reflecting the parsed flags
     */
    public static Options parse(String[] args) {
        Options opts = new Options();
        for (String arg : args) {
            switch (arg) {
                case "-c": opts.countBytes = true; break;
                case "-l": opts.countLines = true; break;
                case "-w": opts.countWords = true; break;
                case "-m": opts.countChars = true; break;
                default:
                    if (opts.fileName == null) {
                        opts.fileName = arg;
                    }
                    break;
            }
        }

        boolean anyFlagSet = opts.countBytes || opts.countLines  || opts.countWords || opts.countChars;
        if (!anyFlagSet) {
            opts.countBytes = true;
            opts.countLines = true;
            opts.countWords = true;
        }
        return opts;
    }
}
