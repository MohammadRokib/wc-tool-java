package ccwc;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link java.io.InputStream} wrapper that tracks the total number of bytes
 * read from the underlying stream. Useful for counting bytes without an
 * additional pass over the data.
 */
public class CountingInputStream extends FilterInputStream {
    private long bytesRead = 0;

    /**
     * Constructs a {@code CountingInputStream} wrapping the given input stream.
     *
     * @param in the input stream to wrap
     */
    CountingInputStream(InputStream in) {
        super(in);
    }

    /**
     * Reads a single byte from the underlying stream and increments the
     * byte counter if a byte was successfully read.
     *
     * @return the next byte of data, or {@code -1} if the end of the stream
     *         has been reached
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        int b = super.read();
        if (b != -1) {
            bytesRead++;
        }
        return b;
    }

    /**
     * Reads up to {@code len} bytes from the underlying stream into the
     * given buffer and increments the byte counter by the number of bytes
     * actually read.
     *
     * @param b   the buffer into which the data is read
     * @param off the start offset in the buffer at which the data is written
     * @param len the maximum number of bytes to read
     * @return the total number of bytes read into the buffer, or {@code -1}
     *         if there is no more data
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int n = super.read(b, off, len);
        if (n != -1) {
            bytesRead += n;
        }
        return n;
    }

    /**
     * Returns the total number of bytes that have been read from the
     * underlying stream since this object was created.
     *
     * @return the total bytes read
     */
    long getBytesRead() {
        return bytesRead;
    }
}
