package gg.moonflower.etched.api.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Keeps responses from services chosen by players from being read without end, which would use up the memory or disk of the game or server.
 * <p>
 * The idea of bounding responses comes from the size limits in Re-Etched by oogabooga-dev, https://github.com/oogabooga-dev/re-etched
 */
public final class BoundedStreams {

    /**
     * The most that is read from a web page or script of a music service
     */
    public static final long MAX_PAGE_BYTES = 8L * 1024 * 1024;
    /**
     * The most that is read from the JSON reply of a music service
     */
    public static final long MAX_JSON_BYTES = 2L * 1024 * 1024;
    /**
     * The most that is read from a playlist
     */
    public static final long MAX_PLAYLIST_BYTES = 1024L * 1024;
    /**
     * The most entries a playlist can have
     */
    public static final int MAX_PLAYLIST_ENTRIES = 8192;
    /**
     * The most that is stored for a cover image
     */
    public static final long MAX_IMAGE_BYTES = 16L * 1024 * 1024;

    private BoundedStreams() {
    }

    /**
     * Wraps a stream so reading more than the limit fails.
     *
     * @param stream   The stream to read from
     * @param maxBytes The most bytes that may be read
     * @return A stream that throws an {@link IOException} once more than the limit is read
     */
    public static InputStream limit(InputStream stream, long maxBytes) {
        return new LimitedInputStream(stream, maxBytes);
    }

    /**
     * Reads a stream as UTF-8 text.
     *
     * @param stream   The stream to read from
     * @param maxBytes The most bytes that may be read
     * @return The text of the stream
     * @throws IOException If the stream is bigger than the limit or cannot be read
     */
    public static String readString(InputStream stream, long maxBytes) throws IOException {
        try (InputStream limited = limit(stream, maxBytes)) {
            return new String(limited.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static class LimitedInputStream extends FilterInputStream {

        private final long max;
        private long read;

        private LimitedInputStream(InputStream in, long max) {
            super(in);
            this.max = max;
        }

        private void count(long amount) throws IOException {
            this.read += amount;
            if (this.read > this.max) {
                throw new IOException("The response is bigger than the limit of " + this.max + " bytes");
            }
        }

        @Override
        public int read() throws IOException {
            int value = super.read();
            if (value != -1) {
                this.count(1);
            }
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int amount = super.read(b, off, len);
            if (amount > 0) {
                this.count(amount);
            }
            return amount;
        }

        @Override
        public long skip(long n) throws IOException {
            long amount = super.skip(n);
            if (amount > 0) {
                this.count(amount);
            }
            return amount;
        }
    }
}
