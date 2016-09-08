package scraper.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for file operations.
 */
public final class FileUtils {

    private static final String FORBIDDEN_CHARS_REGEX = "[<>:\"/\\\\|\\?\\*]";

    private static final int BLOCK_SIZE = 1024;

    private FileUtils() {
    }

    /**
     * Resolves given {@code path} against given nodes.
     * <p>
     * Uses {@link Path#resolve(String)}.
     *
     * @param path   path to resolve
     * @param first  first node to resolve
     * @param others other nodes to resolve. May be empty
     * @return resolved path
     */
    public static Path resolve(Path path, String first, String... others) {
        Path resolvedPath = path.resolve(first);
        for (String p : others) {
            resolvedPath = resolvedPath.resolve(p);
        }

        return resolvedPath;
    }

    /**
     * Gets file extension from {@code url}.
     *
     * @param url the url
     * @return file extension or <tt>null</tt> if extension can not be determined
     */
    public static String getExtension(String url) {
        return getExtension(url, null);
    }

    /**
     * Gets file extension from {@code url}.
     *
     * @param url          the url
     * @param defaultValue value to return if extension can not be determined
     * @return file extension or {@code defaultValue} if extension can not be determined
     */
    public static String getExtension(String url, String defaultValue) {
        String[] filenameParts = url.split(FORBIDDEN_CHARS_REGEX);
        String filename = filenameParts[filenameParts.length - 1];

        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? defaultValue : filename.substring(lastDot + 1);
    }

    /**
     * Sanitizes {@code filename}.
     * <p>
     * Replaces all filename forbidden characters with underscore (<tt>"_"</tt>) character.
     *
     * @param filename filename to sanitize
     * @return sanitized filename
     */
    public static String sanitize(String filename) {
        return filename.replaceAll(FORBIDDEN_CHARS_REGEX, "_");
    }

    /**
     * Sanitize all given {@code filenames}.
     * <p>
     * Uses {@link #sanitize(String)} method.
     *
     * @param filenames filenames to sanitize. May be empty
     * @return array of sanitized filenames
     */
    public static String[] sanitize(String... filenames) {
        String[] sanitizedFilenames = new String[filenames.length];
        for (int i = 0; i < filenames.length; ++i) {
            sanitizedFilenames[i] = sanitize(filenames[i]);
        }

        return sanitizedFilenames;
    }

    /**
     * Reads whole file content as string.
     *
     * @param path     path of the file to read
     * @param encoding encoding used to read file
     * @return file content as a string
     * @throws IOException if I/O problems occurred
     */
    public static String readFile(Path path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(path);
        return new String(encoded, encoding);
    }

    /**
     * Computes {@code file} sha-256 hash
     *
     * @param file file
     * @return sha of the {@code file}
     * @throws NoSuchAlgorithmException if could not find sha-256 algorithm
     * @throws IOException              if io failed
     */
    // TODO add tests
    public static String computeSHA2(Path file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] dataBytes = new byte[BLOCK_SIZE];
        int nread;
        try (InputStream is = Files.newInputStream(file)) {
            while ((nread = is.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }
        }

        return StringUtils.bytesToHex(md.digest());
    }
}
