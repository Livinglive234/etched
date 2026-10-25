package gg.moonflower.etched.api.util;

import gg.moonflower.etched.core.Etched;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Keeps URLs provided by players from reaching local or special-purpose network resources, which would let a player make the game
 * (or the server, when etching) send requests to services that are only reachable from that machine.
 * <p>
 * The destination of every request and every redirect is resolved and checked before connecting. The checked address cannot be pinned
 * through {@link HttpURLConnection}, so a DNS rebinding window remains between the check and the connection. A proxy that resolves hostnames
 * remotely must enforce an equivalent policy itself.
 * <p>
 * The design follows the network policy of Re-Etched by oogabooga-dev, https://github.com/oogabooga-dev/re-etched
 */
public final class NetworkPolicy {

    public static final int CONNECT_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public static final int MAX_REDIRECTS = 5;
    private static final long DNS_TIMEOUT_SECONDS = 5;
    private static final ExecutorService DNS_EXECUTOR = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(32), runnable -> {
        Thread thread = new Thread(runnable, "Etched DNS");
        thread.setDaemon(true);
        return thread;
    }, new ThreadPoolExecutor.AbortPolicy());

    private NetworkPolicy() {
    }

    /**
     * Opens a connection to the specified URL, following redirects manually so that every destination is checked.
     *
     * @param url     The URL to connect to
     * @param proxy   The proxy to use, or <code>null</code> to use the default proxy selection
     * @param method  The request method, either <code>GET</code> or <code>HEAD</code>
     * @param headers The request headers to send
     * @return A connection that has already received the response of the final destination
     * @throws IOException If the destination is not allowed, there are too many redirects or the connection fails
     */
    public static HttpURLConnection connect(URL url, @Nullable Proxy proxy, String method, Map<String, String> headers) throws IOException {
        URL current = url;
        for (int redirects = 0; ; redirects++) {
            check(current);

            HttpURLConnection connection = (HttpURLConnection) (proxy != null ? current.openConnection(proxy) : current.openConnection());
            connection.setInstanceFollowRedirects(false);
            connection.setUseCaches(false);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod(method);
            headers.forEach(connection::setRequestProperty);

            int response = connection.getResponseCode();
            if (response != HttpURLConnection.HTTP_MOVED_PERM && response != HttpURLConnection.HTTP_MOVED_TEMP && response != HttpURLConnection.HTTP_SEE_OTHER && response != 307 && response != 308) {
                return connection;
            }

            String location = connection.getHeaderField("Location");
            connection.disconnect();
            if (location == null || location.isBlank()) {
                throw new IOException("Redirect without a location");
            }
            if (redirects >= MAX_REDIRECTS) {
                throw new IOException("Too many redirects");
            }

            try {
                current = new URL(current, location);
            } catch (MalformedURLException e) {
                throw new IOException("Invalid redirect", e);
            }
        }
    }

    /**
     * Checks that the specified URL can be connected to.
     *
     * @param url The URL to check
     * @throws IOException If the URL is not an HTTP(S) URL without credentials, or it points to a destination that is not allowed
     */
    public static void check(URL url) throws IOException {
        String protocol = url.getProtocol().toLowerCase(Locale.ROOT);
        String host = url.getHost();
        if (!protocol.equals("http") && !protocol.equals("https") || host == null || host.isBlank() || url.getUserInfo() != null || host.contains("%") || containsControlCharacter(url.toString())) {
            throw new IOException("Only absolute HTTP(S) URLs without credentials are allowed");
        }
        if (host.startsWith("[") && host.endsWith("]")) {
            host = host.substring(1, host.length() - 1);
        }

        InetAddress[] addresses = resolve(host);
        boolean hasPublic = false;
        boolean hasPrivate = false;
        for (InetAddress address : addresses) {
            switch (NetworkAddressClassifier.classify(address)) {
                case PUBLIC -> hasPublic = true;
                case PRIVATE -> hasPrivate = true;
                case FORBIDDEN -> throw new IOException("That address is not allowed");
            }
        }

        if (hasPublic && hasPrivate) {
            throw new IOException("That address is not allowed");
        }
        if (hasPrivate && !isPrivateNetworkAllowed()) {
            throw new IOException("Addresses on private networks are not allowed");
        }
    }

    private static boolean isPrivateNetworkAllowed() {
        try {
            return Etched.CLIENT_CONFIG.allowPrivateNetwork.get();
        } catch (IllegalStateException e) {
            return false; // The client config is not loaded on a dedicated server
        }
    }

    private static InetAddress[] resolve(String host) throws IOException {
        Future<InetAddress[]> lookup;
        try {
            lookup = DNS_EXECUTOR.submit(() -> InetAddress.getAllByName(host));
        } catch (RejectedExecutionException e) {
            throw new IOException("Too many address lookups", e);
        }

        try {
            InetAddress[] addresses = lookup.get(DNS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (addresses == null || addresses.length == 0) {
                throw new UnknownHostException(host);
            }
            return addresses;
        } catch (TimeoutException e) {
            throw new IOException("Timed out while resolving " + host, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while resolving " + host, e);
        } catch (ExecutionException e) {
            throw new IOException("Could not resolve " + host, e.getCause());
        } finally {
            lookup.cancel(true);
        }
    }

    private static boolean containsControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isISOControl(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
