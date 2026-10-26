package gg.moonflower.etched.api.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Removes the metadata blocks that Shoutcast and Icecast servers mix into a stream and reports the current stream title.
 * <p>The idea of splitting metadata out of the audio, with bounded titles, comes from re-etched.
 */
public final class IcyInputStream extends InputStream {

    private static final int MAX_TITLE_LENGTH = 256;

    private final InputStream source;
    private final int interval;
    private final Consumer<String> titleListener;
    private final byte[] metadata = new byte[255 * 16];
    private int remaining;
    private String lastTitle;

    public IcyInputStream(InputStream source, int interval, Consumer<String> titleListener) {
        if (interval <= 0) {
            throw new IllegalArgumentException("The metadata interval must be positive");
        }
        this.source = source;
        this.interval = interval;
        this.titleListener = titleListener;
        this.remaining = interval;
    }

    private void skipMetadata() throws IOException {
        int length = this.source.read();
        if (length < 0) {
            throw new EOFException("The metadata length is missing");
        }
        length *= 16;
        int read = 0;
        while (read < length) {
            int count = this.source.read(this.metadata, read, length - read);
            if (count < 0) {
                throw new EOFException("The metadata ended early");
            }
            read += count;
        }
        this.remaining = this.interval;
        if (length > 0) {
            int end = 0;
            while (end < length && this.metadata[end] != 0) {
                end++;
            }
            String title = parseTitle(new String(this.metadata, 0, end, StandardCharsets.UTF_8));
            if (title != null && !title.equals(this.lastTitle)) {
                this.lastTitle = title;
                try {
                    this.titleListener.accept(title);
                } catch (RuntimeException ignored) {
                }
            }
        }
    }

    /**
     * Finds the stream title in a metadata block such as <code>StreamTitle='Artist - Song';StreamUrl='';</code>
     *
     * @param metadata The metadata text
     * @return The cleaned title, or <code>null</code> if there is none
     */
    public static String parseTitle(String metadata) {
        int start = metadata.indexOf("StreamTitle='");
        if (start < 0) {
            return null;
        }
        start += 13;
        int end = metadata.indexOf("';", start);
        if (end < 0) {
            end = metadata.lastIndexOf('\'');
            if (end < start) {
                end = metadata.length();
            }
        }
        StringBuilder title = new StringBuilder();
        boolean space = false;
        for (int i = start; i < end && title.length() < MAX_TITLE_LENGTH; i++) {
            char c = metadata.charAt(i);
            if (Character.isISOControl(c) || Character.isWhitespace(c)) {
                space = title.length() > 0;
                continue;
            }
            if (space) {
                title.append(' ');
                space = false;
            }
            title.append(c);
        }
        return title.isEmpty() ? null : title.toString();
    }

    @Override
    public int read() throws IOException {
        if (this.remaining == 0) {
            this.skipMetadata();
        }
        int value = this.source.read();
        if (value >= 0) {
            this.remaining--;
        }
        return value;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (this.remaining == 0) {
            this.skipMetadata();
        }
        int count = this.source.read(b, off, Math.min(len, this.remaining));
        if (count > 0) {
            this.remaining -= count;
        }
        return count;
    }

    @Override
    public void close() throws IOException {
        this.source.close();
    }
}
