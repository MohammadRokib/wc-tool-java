package ccwc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2 || !args[0].equals("-c")) {
            System.err.println("Usage: ccwc -c <filename>");
            System.exit(1);
        }

        String fileName = args[1];
        Path path = Paths.get(fileName);

        long byteCount = countBytes(path);
        System.out.println(byteCount + " " + fileName);
    }

    /**
     * Counts the number of bytes in a file by streaming it through a buffer,
     * without ever loading the whole file into memory
     * */
    private static long countBytes(Path path) throws IOException {
        long count = 0;

        try (InputStream in = Files.newInputStream(path)) {
            byte[] buffer = new byte[8192];
            int bytesRead = 0;

            while((bytesRead = in.read(buffer)) != -1) {
                count += bytesRead;
            }
        }
        return count;
    }
}
